package com.skyperobit.command.youtube;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.api.services.youtube.model.PlaylistListResponse;
import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.App;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubePlaylistModel;

public class AddYouTubePlaylistCommand extends ChatAdminCommand
{
	private static final Logger LOG = Logger.getLogger(AddYouTubePlaylistCommand.class);
	
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		
		if(StringUtils.isEmpty(argString))
		{
			sendMessage(chat, "Please enter a playlist id. (!addYTPlaylist <playlist id>)", "AddYTPlaylist");
			return;
		}
		
		String playlistId = argString.trim();
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{		
			ChatModel chatModel = getChat(id, session);
			if(chatHasPlaylist(playlistId, chatModel))
			{
				sendMessage(chat, "playlist has already been added to this chat.", "AddYTPlaylist");
				return;
			}
			
			YouTubePlaylistModel playlist = getPlaylist(playlistId, session, chat);
			
			if(playlist!=null)
			{
				session.beginTransaction();
				chatModel.addYoutubePlaylist(playlist);	
				session.save(chatModel);
				session.getTransaction().commit();
				
				sendMessage(chat, playlistId + " added to list of youtube channels!", "AddYTChannel");
			}
		}
		else
		{
			sendMessage(chat, "This chat isn't registered. Register the chat with !register before adding a YouTube channel.", "AddYTChannel");
		}
	}
	
	private YouTubePlaylistModel getPlaylist(String playlistId, Session session, Chat chat)
	{
		@SuppressWarnings("unchecked")
		List<YouTubePlaylistModel> playlists = session.createQuery("FROM YouTubePlaylistModel as c WHERE c.id = :id")
			.setParameter("id", playlistId).list();
		
		if(CollectionUtils.isNotEmpty(playlists))
		{
			return playlists.get(0);
		}
		else if(playlistExists(playlistId))
		{
			session.beginTransaction();
				
			YouTubePlaylistModel playlist = new YouTubePlaylistModel();
			playlist.setId(playlistId);
			session.save(playlist);
			session.getTransaction().commit();
				
			return playlist;
		}
		else
		{
			sendMessage(chat, playlistId + " is an invalid playlist id.", "AddYTPlaylist");
			return null;
		}
	}

	private boolean chatHasPlaylist(String playlistId, ChatModel chat)
	{
		Set<YouTubePlaylistModel> playlists = chat.getYoutubePlaylists();
		if(playlists!=null)
		{
			for(YouTubePlaylistModel playlist : playlists)
			{
				if(playlistId.equals(playlist.getId()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean playlistExists(String id)
	{
		try
		{
			LOG.info("Checking if playlist '" + id + "' exists");
			PlaylistListResponse playlistResponse = App.getYoutube().playlists().list("snippet").setId(id).execute();
			if(CollectionUtils.isNotEmpty(playlistResponse.getItems()))
			{
				return true;
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to get playlist for id: " + id, e);
		}
		
		return false;
	}
}
