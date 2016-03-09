package com.skyperobit.command.impl;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.model.ChatModel;

public class RegisterChatCommand extends ChatAdminCommand
{
	private static final Logger LOG = Logger.getLogger(RegisterChatCommand.class);
	
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String id = chat.getIdentity();
		
		if(isChatRegistered(id, session))
		{
			sendMessage(chat, "This chat is already registered!", "Register");
		}
		else
		{
			ChatModel chatModel = new ChatModel();
			chatModel.setId(id);
			chatModel.setEnableNotifications(true);
			
			try
			{
				session.save(chatModel);
				session.flush();
				sendMessage(chat, "This chat is now registered.", "Register");
			}
			catch(Exception e)
			{
				LOG.error("Failed to save ChatModel, id = " + id);
				sendErrorMessage(chat);
			}
		}
	}

}
