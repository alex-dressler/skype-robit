package com.skyperobit.command.impl;

import java.io.IOException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.api.services.youtube.model.ChannelListResponse;
import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.App;

public class AddYouTubeChannelCommand extends ChatAdminCommand
{
	private static final Logger LOG = Logger.getLogger(AddYouTubeChannelCommand.class);
	
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{
			
		}
		else
		{
			sendMessage(chat, "This chat isn't registered. Register the chat with !register before adding a YouTube channel.", "AddYTChannel");
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
}
