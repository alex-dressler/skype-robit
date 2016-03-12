package com.skyperobit.command.youtube;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.YouTubeChannelModel;

public class ListYouTubeChannelsCommand extends ChatAdminCommand
{

	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String chatId = chat.getIdentity();
		if(isChatRegistered(chatId, session))
		{
			Set<YouTubeChannelModel> channels = getChat(chatId, session).getYoutubeChannels();
			if(channels!=null)
			{
				StringBuilder message = new StringBuilder();
				boolean first = true;
				for(YouTubeChannelModel channel : channels)
				{
					if(!first)
					{
						message.append("\n");
					}
					
					message.append(channel.getId()).append(" (").append(channel.getUsername().replace(channel.getId(), "")).append(")");
					
					first = false;
				}
				
				if(StringUtils.isNotEmpty(message))
				{
					message.insert(0, "YouTube channels for this chat:\n id (username)\n-----------------------------------------------\n");
					sendMessage(chat, message.toString(), "ListYTChannels");
					return;
				}
			}
			
			sendMessage(chat, "No YouTube channels found for this chat.", "ListYTChannels");
		}
		else
		{
			sendMessage(chat, "This chat isn't registered. You can register with !register.", "ListYTChannel");
		}
	}

}
