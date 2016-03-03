package com.skyperobit.command.impl;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.Command;

public class PingCommand extends Command
{	
	private String response;
	
	public PingCommand(String response)
	{
		this.response = response;
	}
	
	@Override
	public void execute(String argString, Chat chat)
	{	
		sendMessage(chat, response, "Ping");
	}
}
