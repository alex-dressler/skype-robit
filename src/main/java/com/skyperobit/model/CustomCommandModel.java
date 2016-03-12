package com.skyperobit.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "custom_command")
public class CustomCommandModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2925373009717079963L;

	@Id
	@Column(name = "code")
	private String code;
	
	@Id
	@ManyToOne
	@JoinColumn(name = "chat_id")
	private ChatModel chat;
	
	@Column(name = "value")
	private String value;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public ChatModel getChat()
	{
		return chat;
	}

	public void setChat(ChatModel chat)
	{
		this.chat = chat;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}