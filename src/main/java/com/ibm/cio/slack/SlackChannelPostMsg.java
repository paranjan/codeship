package com.ibm.cio.slack;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpStatus;

public class SlackChannelPostMsg
{
	public static void main(String[] args)
	{
		SlackChannelPostMsg scPostMsg = new SlackChannelPostMsg();
		scPostMsg.postMsg("C05258G3T", "Hi .. hw r u ?\nI m in pune");
	}
	
	public void postMsg(String channelId, String msg)
	{
		try
		{
			URL url = new URL("https://slack.com/api/chat.postMessage?token=xoxp-5073287987-5225379851-5226770115-833d64"
								+ "&channel=" + channelId +"&text=" + URLEncoder.encode(msg));
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			
			System.out.println("Channel Post Msg Response : " + conn.getResponseCode() + " - " + conn.getResponseMessage());

			if(conn.getResponseCode() == HttpStatus.SC_OK)
				System.out.println("Posted " + msg + " in channel : " + channelId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
