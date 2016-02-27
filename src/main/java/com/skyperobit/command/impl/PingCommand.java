package com.skyperobit.command.impl;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.skyperobit.command.Command;

public class PingCommand implements Command
{
	@Override
	public void execute(String argString, Chat chat)
	{
		try
		{
			chat.sendMessage("pong!");
		} 
		catch (ConnectionException e)
		{
			//TODO: replace with Log4J
			e.printStackTrace();
		}
	}
}
