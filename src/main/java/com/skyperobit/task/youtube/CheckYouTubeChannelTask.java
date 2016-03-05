package com.skyperobit.task.youtube;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.formatting.Message;
import com.skyperobit.App;
import com.skyperobit.Config;

public class CheckYouTubeChannelTask extends TimerTask
{
	private static final Logger LOG = Logger.getLogger(CheckYouTubeChannelTask.class);
	
	@Override
	public void run()
	{
		//TODO: change to loop through all skype chats and get "subscribed" channels from database
		List<String> usernames = Config.getList("youtube.channels", new ArrayList<>());
		StringBuilder message = new StringBuilder();
		
		boolean first = true;
		for(String username : usernames)
		{
			//TODO: check database for stored channel id
			String response = getLatestVideoMessage(getIdForChannel(username));
			if(StringUtils.isNotEmpty(response))
			{
				if(!first)
				{
					message.append("------------------------------\n");
				}
				message.append(response);
				first = false;
			}
		}
		
		if(StringUtils.isNotEmpty(message))
		{
			message.insert(0, "Recently uploaded video(s):\n");
		}
		
		try
		{
			App.getSkype().getOrLoadChat("").sendMessage(Message.fromHtml(message.toString()));
		}
		catch (ConnectionException | ChatNotFoundException e)
		{
			LOG.error("Failed to send message", e);
		}
	}
	
	private String getIdForChannel(String username)
	{
		try
		{
			LOG.info("Getting id for username: " + username);
			ChannelListResponse channelListResponse = App.getYoutube().channels()
					.list("snippet").setForUsername(username).execute();
			if(CollectionUtils.isNotEmpty(channelListResponse.getItems()))
			{
				return channelListResponse.getItems().get(0).getId();
			}
		}
		catch(IOException e)
		{
			LOG.error("Failed to retrieve channel: " + username, e);
		}
		
		return null;
	}
	
	private String getLatestVideoMessage(String channelId)
	{
		try
		{
			SearchListResponse searchListResponse = App.getYoutube().search().list("snippet").setChannelId(channelId)
					.setOrder("date").execute();
			if(CollectionUtils.isNotEmpty(searchListResponse.getItems()))
			{
				SearchResult searchResult = searchListResponse.getItems().get(0);
				String id = searchResult.getId().getVideoId();
				SearchResultSnippet snippet = searchResult.getSnippet();
				//TODO: add logic to return null if this video was already posted
				String title = snippet.getTitle();
				String channelTitle = snippet.getChannelTitle();
				
				DateFormat dateFormat = new SimpleDateFormat("h:m a z, MM/dd/YY");
				String timeString = dateFormat.format(new Date(snippet.getPublishedAt().getValue()));
				return new StringBuilder().append(channelTitle).append(", published ").append(timeString)
						.append(":\n<a href=\"https://www.youtube.com/watch?v=").append(id).append("\">")
						.append(title).append("</a>").toString();
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to perform search on channel: ");
		}
		
		return null;
	}
}
