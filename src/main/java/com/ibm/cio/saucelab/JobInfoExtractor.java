package com.ibm.cio.saucelab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JobInfoExtractor
{
	static HttpClient client = new DefaultHttpClient();
	static HttpResponse response; 
	
	public HttpResponse makeGetRequest(String url, String type) throws IOException, ParseException
	{
		if(response!=null)
			EntityUtils.consume(response.getEntity());
		
		HttpGet getRequest = new HttpGet(url);
		getRequest.setHeader("Accept",type);
		getRequest.setHeader("Content-type",type);

		// Get the responses
		response = client.execute(getRequest);
		return response;
	}
	
	public Map<String, String> extractData(String jobId, String jobName)
	{
		Map<String, String> urlMap = new HashMap<String, String>();
		
		try
		{
			Properties prop = new Properties();
			prop.load(JobIdExtractor.class.getResourceAsStream("/application.properties"));
			
			String username = prop.getProperty("sauce_username");
			String automate_key = prop.getProperty("sauce_automate_key");
			//String jobname = prop.getProperty("sauce_job");
			String type = prop.getProperty("sauce_data_type");
			
			String url = "https://" + username + ":" + automate_key + "@saucelabs.com/rest/v1/" + username + "/jobs/" + jobId;
			response=makeGetRequest(url,type); 
			
			if(response!=null)
			{
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer sb = new StringBuffer();
				String line = "";

				while ((line = rd.readLine()) != null) 
					sb.append(line);
				
				System.out.println(sb.toString());
				
				urlMap = parseJson(sb.toString(), jobName);
				//System.out.println(jobId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return urlMap;
	}
	
	public Map<String, String> parseJson(String data, String jobname)
	{
		Map<String, String> urlMap = new HashMap<String, String>();
		String name;
		
		try
		{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(data);
			
			JSONObject jsonObj = (JSONObject)obj;
			name = (String)jsonObj.get("name");
			
			if(name != null && name.equalsIgnoreCase(jobname))
			{
				urlMap.put("name", (String)jsonObj.get("name"));
				urlMap.put("video_url", (String)jsonObj.get("video_url"));
				urlMap.put("log_url", (String)jsonObj.get("log_url"));
				urlMap.put("status", (String)jsonObj.get("consolidated_status"));
			}
			
			else
				System.out.println("Job Name is not matching. Got job name - " + name + ", while looking for - " + jobname);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urlMap;
	}

	public static void main(String []args)
	{
		JobInfoExtractor obj = new JobInfoExtractor();
		System.out.println(obj.extractData("6902e8ecdb0643a1bfd500a0ea50281c", "Jenkin-SauceLab Test"));
	}
}
