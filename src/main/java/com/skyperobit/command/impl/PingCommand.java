package com.skyperobit.command.impl;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.skyperobit.command.Command;

public class PingCommand implements Command
{
	private static final Logger LOG = Logger.getLogger(PingCommand.class);
	
	@Override
	public void execute(String argString, Chat chat)
	{
		try
		{
			LOG.info("Sending message: pong!");
			chat.sendMessage("pong!");
		} 
		catch (ConnectionException e)
		{
			LOG.error("Ping command failed: ", e);
		}
	}
}
