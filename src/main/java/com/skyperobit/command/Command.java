package com.skyperobit.command;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.samczsun.skype4j.exceptions.ConnectionException;

public abstract class Command
{
	private static Logger LOG = Logger.getLogger(Command.class);
	public abstract void execute(String argString, ReceivedMessage message, Chat chat);
	
	public void sendMessage(Chat chat, String message, String commandName)
	{
		try
		{
			LOG.info("Sending message: " + message);
			chat.sendMessage(message);
		} 
		catch (ConnectionException e)
		{
			LOG.error(commandName + " command failed: ", e);
		}
	}
}
