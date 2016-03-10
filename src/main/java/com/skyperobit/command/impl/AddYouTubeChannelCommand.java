package com.skyperobit.command.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.App;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubeChannelModel;

public class AddYouTubeChannelCommand extends ChatAdminCommand
{
	private static final Logger LOG = Logger.getLogger(AddYouTubeChannelCommand.class);
	
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		
		if(StringUtils.isEmpty(argString))
		{
			sendMessage(chat, "Please enter a YouTube username to add a channel. (!addYTChannel <username>)", "AddYTChannel");
			return;
		}
		
		String username = argString.trim();
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{		
			ChatModel chatModel = getChat(id, session);
			if(chatHasChannel(username, chatModel))
			{
				sendMessage(chat, username + " has already been added to this chat.", "AddYTChannel");
				return;
			}
			
			YouTubeChannelModel channel = getChannel(username, session, chat);
			
			if(channel!=null)
			{
				session.beginTransaction();
				chatModel.addYoutubeChannel(channel);	
				session.save(chatModel);
				session.getTransaction().commit();
				
				sendMessage(chat, username + " added to list of youtube channels!", "AddYTChannel");
			}
		}
		else
		{
			sendMessage(chat, "This chat isn't registered. Register the chat with !register before adding a YouTube channel.", "AddYTChannel");
		}
	}
	
	private YouTubeChannelModel getChannel(String username, Session session, Chat chat)
	{
		@SuppressWarnings("unchecked")
		List<YouTubeChannelModel> channels = session.createQuery("FROM YouTubeChannelModel as c WHERE c.username = :username")
			.setParameter("username", username).list();
		
		if(CollectionUtils.isNotEmpty(channels))
		{
			return channels.get(0);
		}
		else
		{
			String channelId = getIdForChannel(username, session);
			if(StringUtils.isNotEmpty(channelId))
			{
				session.beginTransaction();
				
				YouTubeChannelModel channel = new YouTubeChannelModel();
				channel.setUsername(username);
				channel.setId(channelId);
				session.save(channel);
				session.getTransaction().commit();
				
				return channel;
			}
			else
			{
				sendMessage(chat, username + " is an invalid username. Checking if it matches a channel id...", "AddYTChannel");
				return getChannelForId(username, session, chat);
			}
		}
	}
	
	private YouTubeChannelModel getChannelForId(String id, Session session, Chat chat)
	{
		try
		{
			ChannelListResponse channelListResponse = App.getYoutube().channels()
					.list("snippet").setId(id).execute();
			if(CollectionUtils.isNotEmpty(channelListResponse.getItems()))
			{
				Channel channel = channelListResponse.getItems().get(0); 
				
				session.beginTransaction();
				
				YouTubeChannelModel channelModel = new YouTubeChannelModel();
				channelModel.setUsername(channel.getSnippet().getTitle() + id);
				channelModel.setId(id);
				session.save(channelModel);
				session.getTransaction().commit();
				
				return channelModel;
			}
			else
			{
				sendMessage(chat, id + " is an invalid channel id.", "AddYTChannel");
			}
		}
		catch(IOException e)
		{
			LOG.info("Failed to retrieve channel with id: " + id, e);
		}
		
		return null;
	}

	private boolean chatHasChannel(String username, ChatModel chat)
	{
		Set<YouTubeChannelModel> channels = chat.getYoutubeChannels();
		if(channels!=null)
		{
			for(YouTubeChannelModel channel : channels)
			{
				//Warning: on the off chance that a channel's username is the same as another channel's id, there will be a false positive
				if(channel.getUsername().equals(username) || channel.getId().equals(username))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private String getIdForChannel(String username, Session session)
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
}
