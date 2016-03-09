package com.skyperobit.command.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.samczsun.skype4j.user.User;
import com.samczsun.skype4j.user.User.Role;
import com.skyperobit.App;
import com.skyperobit.command.Command;
import com.skyperobit.model.ChatModel;

public abstract class ChatAdminCommand extends Command
{
	private static final Logger LOG = Logger.getLogger(ChatAdminCommand.class);
	
	@Override
	public void execute(String argString, ReceivedMessage message, Chat chat)
	{
		Session session = App.getSessionFactory().openSession();
		String id = chat.getIdentity();
		if(StringUtils.isNotEmpty(id))
		{
			if(!hasPermission(message, session))
			{
				sendMessage(chat, "You must be an admin to use this command.", "Chat Admin");
				return;
			}
			
			doChatAction(argString, chat, session);
		}
		else
		{
			LOG.error("Chat id is empty! What the heck?");
			sendErrorMessage(chat);
		}
		
		session.close();
	}
	
	//TODO: Eventually add database version of admin to this check
	private boolean hasPermission(ReceivedMessage message, Session session)
	{
		User user = message.getSender();
		return user.getRole().equals(Role.ADMIN);
	}

	public void sendErrorMessage(Chat chat)
	{
		sendMessage(chat, "Could not execute the command... I'm so sorry.", "Chat Admin");
	}
	
	public boolean isChatRegistered(String id, Session session)
	{
		ChatModel chat = getChat(id, session);
		
		if(chat!=null)
		{
			return chat.getEnableNotifications();
		}
		
		return false;
	}
	
	public ChatModel getChat(String id, Session session)
	{
		@SuppressWarnings("unchecked")
		List<ChatModel> chats = session.createQuery("FROM ChatModel c WHERE c.id = :chatId")
				.setParameter("chatId", id).list();
		
		if(CollectionUtils.isNotEmpty(chats))
		{
			return chats.get(0);
		}
		
		return null;
	}
	
	public abstract void doChatAction(String argString, Chat chat, Session session);
}
