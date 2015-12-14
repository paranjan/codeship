package com.ibm.cio.saucelab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JobIdExtractor
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
	
	public String extractData()
	{
		String jobId = null;
		
		try
		{
			Properties prop = new Properties();
			prop.load(JobIdExtractor.class.getResourceAsStream("/application.properties"));
			
			String username = prop.getProperty("sauce_username");
			String automate_key = prop.getProperty("sauce_automate_key");
			String type = prop.getProperty("sauce_data_type");
			
			String url = "https://" + username + ":" + automate_key + "@saucelabs.com/rest/v1/" + username + "/jobs?limit=1";
			response=makeGetRequest(url,type); 
			
			if(response!=null)
			{
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer sb = new StringBuffer();
				String line = "";

				while ((line = rd.readLine()) != null) 
					sb.append(line);
				
				//System.out.println(sb.toString());
				
				jobId = parseJson(sb.toString());
				//System.out.println(jobId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return jobId;
	}
	
	public String parseJson(String data)
	{
		String jobId = null;
		
		try
		{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(data);
			
			JSONArray jsonArr = (JSONArray) obj;
			JSONObject jsonObj = (JSONObject)jsonArr.get(0);
			//System.out.println(jsonObj.get("id"));
			
			jobId = (String)jsonObj.get("id");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jobId;
	}

	public static void main(String []args)
	{
		JobIdExtractor obj = new JobIdExtractor();
		System.out.println(obj.extractData());
	}
}
