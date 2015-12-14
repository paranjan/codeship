package com.ibm.cio.jenkins;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.cio.saucelab.JobIdExtractor;
import com.ibm.cio.saucelab.JobInfoExtractor;
import com.ibm.cio.slack.SlackChannelPostMsg;

@Path("/jenkins/notifications")
public class JenkinsReqHandler
{
	private String jobName, jobStatus;
	
	public JenkinsReqHandler()
	{
		jobName = null;
		jobStatus = null;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getData()
	{
		return "This is a webhook to accept notifications from Jenkins";
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void postData(String json)
	{
			System.out.println("==============================================\n");
			System.out.println("Recieved Data : " + json);
			System.out.println("\n==============================================\n");
			
			setJobData(json);
			
			if(jobName != null && jobStatus != null && jobStatus.equalsIgnoreCase("SUCCESS"))
			{
				JobIdExtractor idExtractor = new JobIdExtractor();
				String jobId = idExtractor.extractData();
				
				JobInfoExtractor infoExtractor = new JobInfoExtractor();
				Map<String, String> jobData = infoExtractor.extractData(jobId, jobName);
				
				StringBuffer response = new StringBuffer();
				response.append("Test Name :  " + jobName);
				response.append("\nTest Video :  " + jobData.get("video_url"));
				response.append("\nTest Status :  " + jobData.get("status"));
				response.append("\nTest Log :  " + jobData.get("log_url"));
				
				SlackChannelPostMsg scPostMsg = new SlackChannelPostMsg();
				scPostMsg.postMsg("C05258G3T", response.toString());
			}
	}
	
	public void setJobData(String json)
	{
		try
		{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(json);
			
			JSONObject jsonObj = (JSONObject)obj;
			jobName = (String)jsonObj.get("name");
			
			jsonObj = (JSONObject)jsonObj.get("build");
			
			//System.out.println("Status : " + jsonObj.get("status"));
			if(((String)jsonObj.get("phase")).equalsIgnoreCase("FINALIZED"))
				jobStatus = (String)jsonObj.get("status");
			
			System.out.println("Name : " + jobName + "\tPhase : " + jsonObj.get("phase") + "\tStatus : " + jobStatus);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
