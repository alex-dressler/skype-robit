package com.skyperobit.event;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.skyperobit.command.CommandHandler;

public class MessageReceivedEventListener implements Listener
{
	private static final Logger LOG = Logger.getLogger(MessageReceivedEventListener.class);
	private CommandHandler commandHandler;
	
	public MessageReceivedEventListener()
	{
		commandHandler = new CommandHandler();
	}
	
	@EventHandler
	public void onMessage(MessageReceivedEvent event)
	{
		try
		{
			String message = event.getMessage().getContent().asPlaintext();
			Chat chat = event.getChat();
			if(message.startsWith("!"))
			{
				commandHandler.handleCommand(message.substring(1), chat);
			}
		}
		catch(Exception e)
		{
			LOG.error("Exception handling message: ", e);
		}
	}
}
