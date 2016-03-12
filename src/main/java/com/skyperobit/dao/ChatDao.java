package com.skyperobit.dao;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Session;

import com.skyperobit.model.ChatModel;

public class ChatDao
{
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
	
	@SuppressWarnings("unchecked")
	public List<ChatModel> getAllChats(Session session)
	{
		return session.createQuery("FROM ChatModel").list();
	}
}
