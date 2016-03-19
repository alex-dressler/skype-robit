package com.skyperobit.task.youtube;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.formatting.Message;
import com.skyperobit.App;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubeChannelModel;

public class CheckYouTubeChannelTask implements Runnable
{
	private static final Logger LOG = Logger.getLogger(CheckYouTubeChannelTask.class);
	private Map<String, Boolean> videoCache;
	
	@Override
	public void run()
	{
		Session session = App.getSessionFactory().openSession();
		videoCache = new HashMap<>();
		
		List<ChatModel> chats = App.getChatDao().getAllChats(session);
		
		if(chats == null)
		{
			return;
		}
		
		for(ChatModel chatModel : chats)
		{
			if(chatModel.getEnableNotifications())
			{
				try
				{
					Chat chat = App.getSkype().getOrLoadChat(chatModel.getId());
					Set<YouTubeChannelModel> channels = chatModel.getYoutubeChannels();
					String message = buildChatMessage(channels, session);
					if(StringUtils.isNotBlank(message))
					{
						chat.sendMessage(Message.fromHtml(message));
					}
				}
				catch (ConnectionException | ChatNotFoundException e)
				{
					LOG.error("Unable to connect to chat with id '" + chatModel.getId() + "'", e);
				}
			}
		}
		
		session.flush();
		session.close();
	}
	
	private String buildChatMessage(Set<YouTubeChannelModel> channels, Session session)
	{
		StringBuilder message = new StringBuilder();
		
		boolean first = true;
		for(YouTubeChannelModel channel : channels)
		{	
			String response = getLatestVideoMessage(channel, session);
			if(StringUtils.isNotEmpty(response))
			{
				if(!first)
				{
					message.append("\n------------------------------\n");
				}
				message.append(response);
				first = false;
			}
		}
		
		if(StringUtils.isNotEmpty(message))
		{
			message.insert(0, "Recently uploaded video(s):\n");
		}
		
		return message.toString();
	}
	
	private String getLatestVideoMessage(YouTubeChannelModel channel, Session session)
	{
		try
		{
			SearchListResponse searchListResponse = App.getYoutube().search().list("snippet").setChannelId(channel.getId())
					.setType("video").setOrder("date").execute();
			if(CollectionUtils.isNotEmpty(searchListResponse.getItems()))
			{
				SearchResult searchResult = searchListResponse.getItems().get(0);
				String id = searchResult.getId().getVideoId();
				SearchResultSnippet snippet = searchResult.getSnippet();
				
				if(StringUtils.isEmpty(id) || (id.equals(channel.getLastVideoId()) && !Boolean.TRUE.equals(videoCache.get(id))))
				{
					return null;
				}
				else if(!Boolean.TRUE.equals(videoCache.get(id)))
				{
					channel.setLastVideoId(id);
					session.save(channel);
				}
				
				String title = snippet.getTitle();
				String channelTitle = StringUtils.isEmpty(snippet.getChannelTitle()) ? channel.getUsername().replace(channel.getId(), "") : snippet.getChannelTitle();
				
				DateFormat dateFormat = new SimpleDateFormat("h:mm a z, MM/dd/YY");
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
