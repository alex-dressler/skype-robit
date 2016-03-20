package com.skyperobit.command.youtube;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.YouTubePlaylistModel;

public class ListYouTubePlaylistsCommand extends ChatAdminCommand
{
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String chatId = chat.getIdentity();
		if(isChatRegistered(chatId, session))
		{
			Set<YouTubePlaylistModel> playlists = getChat(chatId, session).getYoutubePlaylists();
			if(playlists!=null)
			{
				StringBuilder message = new StringBuilder();
				boolean first = true;
				for(YouTubePlaylistModel playlist : playlists)
				{
					if(!first)
					{
						message.append("\n");
					}
					
					message.append(playlist.getId());
					
					first = false;
				}
				
				if(StringUtils.isNotEmpty(message))
				{
					message.insert(0, "YouTube playlists for this chat:\n-----------------------------------------------\n");
					sendMessage(chat, message.toString(), "ListYTPlaylists");
					return;
				}
			}
			
			sendMessage(chat, "No YouTube playlists found for this chat.", "ListYTPlaylists");
		}
		else
		{
			sendMessage(chat, "This chat isn't registered. You can register with !register.", "ListYTPlaylists");
		}
	}
}
