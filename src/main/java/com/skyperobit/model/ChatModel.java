package com.skyperobit.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
public class ChatModel
{	
	@Id
	@Column(name = "id")
	private String id;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, targetEntity=YouTubeChannelModel.class)  
    @JoinTable(name="chat2ytchannel", joinColumns=@JoinColumn(name="chat_pk", nullable=false), 
    	inverseJoinColumns=@JoinColumn(name="ytchannel_pk", nullable=false))
	private Set<YouTubeChannelModel> youtubeChannels;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, targetEntity=YouTubePlaylistModel.class)  
    @JoinTable(name="chat2ytplaylist", joinColumns=@JoinColumn(name="chat_pk", nullable=false), 
    	inverseJoinColumns=@JoinColumn(name="ytplaylist_pk", nullable=false))
	private Set<YouTubePlaylistModel> youtubePlaylists;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "chat")
	private Set<UserModel> users;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "chat")
	private Set<CustomCommandModel> customCommands;
	
	@Column(name = "enable_notifications")
	private boolean enableNotifications;
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	public Set<YouTubeChannelModel> getYoutubeChannels()
	{
		return youtubeChannels;
	}

	public void addYoutubeChannel(YouTubeChannelModel youtubeChannel)
	{
		if(youtubeChannels==null)
		{
			youtubeChannels = new HashSet<YouTubeChannelModel>();
		}
		
		youtubeChannels.add(youtubeChannel);
	}
	
	public Set<YouTubePlaylistModel> getYoutubePlaylists()
	{
		return youtubePlaylists;
	}

	public void addYoutubePlaylist(YouTubePlaylistModel youtubePlaylist)
	{
		if(youtubePlaylists==null)
		{
			youtubePlaylists = new HashSet<YouTubePlaylistModel>();
		}
		
		youtubePlaylists.add(youtubePlaylist);
	}

	public boolean getEnableNotifications()
	{
		return enableNotifications;
	}

	public void setEnableNotifications(boolean enableNotifications)
	{
		this.enableNotifications = enableNotifications;
	}

	public Set<UserModel> getUsers() {
		return users;
	}

	public void setUsers(Set<UserModel> users) {
		this.users = users;
	}

	public Set<CustomCommandModel> getCustomCommands() {
		return customCommands;
	}

	public void setCustomCommands(Set<CustomCommandModel> customCommands) {
		this.customCommands = customCommands;
	}
}
