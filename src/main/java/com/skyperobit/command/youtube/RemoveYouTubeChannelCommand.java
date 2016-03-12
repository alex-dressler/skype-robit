package com.skyperobit.command.youtube;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubeChannelModel;

public class RemoveYouTubeChannelCommand extends ChatAdminCommand {

	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		if(StringUtils.isEmpty(argString))
		{
			sendMessage(chat, "Please enter a YouTube username remove the channel. (!removeYtChannel <username>)", "RemoveYTChannel");
			return;
		}
		
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{
			ChatModel chatModel = getChat(id, session);
			Set<YouTubeChannelModel> channels = chatModel.getYoutubeChannels();
			if(channels!=null)
			{
				YouTubeChannelModel target = null;
				for(YouTubeChannelModel channel : channels)
				{
					if(channel.getUsername().equals(argString) || channel.getId().equals(argString))
					{
						target = channel;
						break;
					}
				}
				
				if(target!=null)
				{
					channels.remove(target);
					session.beginTransaction();
					session.save(chatModel);
					session.getTransaction().commit();
					
					sendMessage(chat, argString + " removed from list of youtube channels for this chat!", "RemoveYTChannel");
				}
				else
				{
					sendMessage(chat, argString + " did not match any channels.", "RemoveYTChannel");
				}
			}
		}
		else
		{
			sendMessage(chat, "This chat isn't registered.", "RemoveYTChannel");
		}
	}

}
