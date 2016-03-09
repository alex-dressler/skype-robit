package com.skyperobit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class UserModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4273811258556145330L;

	@Id
	@Column(name = "username")
	private String username;
	
	@Id
	@ManyToOne(targetEntity = ChatModel.class)
	@JoinColumn(name = "chatId")
	private ChatModel chat;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public ChatModel getChat()
	{
		return chat;
	}

	public void setChat(ChatModel chat)
	{
		this.chat = chat;
	}
}
