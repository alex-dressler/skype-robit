package com.skyperobit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ytplaylist")
public class YouTubePlaylistModel
{
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "lastvideoid")
	private String lastVideoId;

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
}
