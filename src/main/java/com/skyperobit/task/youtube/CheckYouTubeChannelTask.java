package com.skyperobit.task.youtube;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.formatting.Message;
import com.skyperobit.App;
import com.skyperobit.Config;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.YouTubeChannelModel;
import com.skyperobit.model.YouTubePlaylistModel;

public class CheckYouTubeChannelTask implements Runnable
{
	private static final Logger LOG = Logger.getLogger(CheckYouTubeChannelTask.class);
	
	//these fields store all videos across all chats for channels and playlists
	private Map<String, Boolean> channelVideoCache;
	private Map<String, Boolean> playlistVideoCache;
	
	@Override
	public void run()
	{
		Session session = App.getSessionFactory().openSession();
		channelVideoCache = new HashMap<>();
		playlistVideoCache = new HashMap<>();
		
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
					
					Set<Object> videoLists = new HashSet<>();
					videoLists.addAll(chatModel.getYoutubeChannels());
					videoLists.addAll(chatModel.getYoutubePlaylists());
					
					String message = buildChatMessage(videoLists, session);
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
	
	private String buildChatMessage(Set<Object> videoLists, Session session)
	{
		StringBuilder message = new StringBuilder();
		
		boolean first = true;
		for(Object videoList : videoLists)
		{	
			String response = null;
			if(videoList instanceof YouTubeChannelModel)
			{
				response = getLatestVideoMessage((YouTubeChannelModel)videoList, session);
			}
			else if(videoList instanceof YouTubePlaylistModel)
			{
				response = getLatestVideoMessage((YouTubePlaylistModel)videoList, session);
			}
			
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
				
				Date date = new Date(snippet.getPublishedAt().getValue());
				
				//make sure videos that have already been processed for other chats are included in the message.
				//videos which are older than the latest video are not processed
				if(StringUtils.isEmpty(id) || (id.equals(channel.getLastVideoId()) && !Boolean.TRUE.equals(channelVideoCache.get(id)))
						|| (channel.getLastVideoDate()!=null && date.before(channel.getLastVideoDate())))
				{
					return null;
				}
				else if(!Boolean.TRUE.equals(channelVideoCache.get(id)))
				{
					channel.setLastVideoId(id);
					session.save(channel);
					
					channelVideoCache.put(id, true);
				}
				
				channel.setLastVideoDate(new java.sql.Date(snippet.getPublishedAt().getValue()));
				session.save(channel);
				
				String title = snippet.getTitle();
				String channelTitle = StringUtils.isEmpty(snippet.getChannelTitle()) ? channel.getUsername().replace(channel.getId(), "") : snippet.getChannelTitle();
				
				String dateString = getDateString(date);
				return buildVideoString(channelTitle, dateString, id, title);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to perform search on channel", e);
		}
		
		return null;
	}
	
	private String getLatestVideoMessage(YouTubePlaylistModel playlist, Session session)
	{
		try
		{
			PlaylistItemListResponse playlistResponse = App.getYoutube().playlistItems().list("snippet")
					.setPlaylistId(playlist.getId()).execute();
			
			PlaylistItem playlistItem = getLatestVideo(playlistResponse, playlist.getId());
			
			if(playlistItem!=null)
			{
				PlaylistItemSnippet snippet = playlistItem.getSnippet();
				String id = snippet.getResourceId().getVideoId();
				
				//Even if the last video id
				if(StringUtils.isEmpty(id) || (id.equals(playlist.getLastVideoId()) && !Boolean.TRUE.equals(playlistVideoCache.get(id))))
				{
					return null;
				}
				else if(!Boolean.TRUE.equals(playlistVideoCache.get(id)))
				{
					playlist.setLastVideoId(id);
					session.save(playlist);
					
					playlistVideoCache.put(id, true);
				}
				
				String title = snippet.getTitle();
				String channelTitle = snippet.getChannelTitle();
				
				String dateString = getDateString(new Date(snippet.getPublishedAt().getValue()));
				return buildVideoString(channelTitle, dateString, id, title);
			}
		}
		catch (IOException e)
		{
			LOG.error("Failed to perform search on playlist",e);
		}
		
		return null;
	}
	
	private String getDateString(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("h:mm a z, MM/dd/YY");
		dateFormat.setTimeZone(TimeZone.getTimeZone(Config.getString("youtube.notification.timezone", "UTC")));
		
		return dateFormat.format(date);
	}
	
	private String buildVideoString(String channelTitle, String dateString, String videoId, String videoTitle)
	{
		return new StringBuilder().append(channelTitle).append(", published ").append(dateString)
				.append(":\n<a href=\"https://www.youtube.com/watch?v=").append(videoId).append("\">")
				.append(videoTitle).append("</a>").toString();
	}

	private PlaylistItem getLatestVideo(PlaylistItemListResponse playlistResponse, String id)
	{
		String nextPageToken = playlistResponse.getNextPageToken();
		PlaylistItemListResponse response = playlistResponse;
		PlaylistItem result = null;
		long lastTimeStamp = -1;
		
		while(true)
		{
			if(response.getItems()!=null)
			{
				for(PlaylistItem item : response.getItems())
				{
					long timeStamp = item.getSnippet().getPublishedAt().getValue();
					if(timeStamp > lastTimeStamp)
					{
						lastTimeStamp = timeStamp;
						result = item;
					}
				}
			}
			
			if(nextPageToken==null)
			{
				break;
			}
			
			try
			{
				response = App.getYoutube().playlistItems().list("snippet")
						.setPlaylistId(id).setPageToken(nextPageToken).execute();
				nextPageToken = response.getNextPageToken();
			}
			catch (IOException e)
			{
				LOG.error("Failed to perform search on playlist",e);
			}
		}
		
		return result;
	}
}
