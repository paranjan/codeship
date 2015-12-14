package com.ibm.cio.saucelab;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

@Path("/sauce/data")
public class SauceReqHandler
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getData()
	{
		JobIdExtractor idExtractor = new JobIdExtractor();
		String jobId = idExtractor.extractData();
		
		JobInfoExtractor infoExtractor = new JobInfoExtractor();
		Map<String, String> jobData = infoExtractor.extractData(jobId, "Jenkin-SauceLab Test");
		
		Gson gson = new Gson();
		return gson.toJson(jobData);
	}
}