package com.skyperobit.command.general;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.ChatModel;

public class RegisterChatCommand extends ChatAdminCommand
{
	private static final Logger LOG = Logger.getLogger(RegisterChatCommand.class);
	
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String id = chat.getIdentity();
		ChatModel chatModel = getChat(id, session);
		
		if(isChatRegistered(id, session))
		{
			sendMessage(chat, "This chat is already registered!", "Register");
		}
		else
		{
			if(chatModel!=null) //then notifications aren't enabled for this chat
			{
				chatModel.setEnableNotifications(true);
			}
			else
			{
				chatModel = new ChatModel();
				chatModel.setId(id);
				chatModel.setEnableNotifications(true);
			}
			
			try
			{
				session.save(chatModel);
				session.flush();
				sendMessage(chat, "This chat is now registered.", "Register");
			}
			catch(Exception e)
			{
				LOG.error("Failed to save ChatModel, id = " + id, e);
				sendErrorMessage(chat);
			}
		}
	}

}
