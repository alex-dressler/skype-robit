package com.skyperobit.command.youtube;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubePlaylistModel;

public class RemoveYouTubePlaylistCommand extends ChatAdminCommand
{
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		if(StringUtils.isEmpty(argString))
		{
			sendMessage(chat, "Please enter a YouTube playlist id remove the playlist. (!removeYTPlaylist <playlist id>)", "RemoveYTPlaylist");
			return;
		}
		
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{
			ChatModel chatModel = getChat(id, session);
			Set<YouTubePlaylistModel> playlists = chatModel.getYoutubePlaylists();
			if(playlists!=null)
			{
				YouTubePlaylistModel target = null;
				for(YouTubePlaylistModel playlist : playlists)
				{
					if(playlist.getId().equals(argString))
					{
						target = playlist;
						break;
					}
				}
				
				if(target!=null)
				{
					playlists.remove(target);
					session.beginTransaction();
					session.save(chatModel);
					session.getTransaction().commit();
					
					sendMessage(chat, argString + " removed from list of youtube playlists for this chat!", "RemoveYTPlaylist");
				}
				else
				{
					sendMessage(chat, argString + " did not match any playlists.", "RemoveYTPlaylist");
				}
			}
		}
		else
		{
			sendMessage(chat, "This chat isn't registered.", "RemoveYTPlaylist");
		}
	}

}
