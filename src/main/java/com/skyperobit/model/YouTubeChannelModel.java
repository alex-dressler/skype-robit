package com.skyperobit.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ytchannel")
public class YouTubeChannelModel
{
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "lastvideoid")
	private String lastVideoId;
	
	@Column(name = "lastvideodate")
	private Timestamp lastVideoDate;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getLastVideoId()
	{
		return lastVideoId;
	}

	public void setLastVideoId(String lastVideoId)
	{
		this.lastVideoId = lastVideoId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public Timestamp getLastVideoDate()
	{
		return lastVideoDate;
	}

	public void setLastVideoDate(Timestamp lastVideoDate)
	{
		this.lastVideoDate = lastVideoDate;
	}
}
