package com.skyperobit.command.general;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.ChatAdminCommand;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.CustomCommandModel;

public class CreateCommandCommand extends ChatAdminCommand
{
	@Override
	public void doChatAction(String argString, Chat chat, Session session)
	{
		String id = chat.getIdentity();
		if(isChatRegistered(id, session))
		{
			ChatModel chatModel = getChat(id, session);
			
			String regex = "(?<commandName>\\S+)\\s+(?<value>.+)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(argString);
			
			if(matcher.matches())
			{
				String commandName = matcher.group("commandName");
				String value = matcher.group("value").replaceAll("https?://\\S+", "<a href=\"$0\">$0</a>");
				
				Set<CustomCommandModel> customCommands = chatModel.getCustomCommands();
				CustomCommandModel resultCommand = null;
				for(CustomCommandModel command : customCommands)
				{
					if(command.getCode().equals(commandName))
					{
						resultCommand = command;
						break;
					}
				}
				
				if(resultCommand == null)
				{
					resultCommand = new CustomCommandModel();
					resultCommand.setCode(commandName);
					resultCommand.setChat(chatModel);
					customCommands.add(resultCommand);
				}
				
				resultCommand.setValue(value);
				
				session.save(chatModel);
				session.flush();
				
				sendMessage(chat, commandName + " command created!", "CreateCommand");
			}
			else
			{
				sendMessage(chat, "To create a command, type !createcommand <your command name> <message that the command returns>", "CreateCommand");
			}
		}
		else
		{
			sendMessage(chat, "This chat is not registered. Please register with !register.", "CreateCommand");
		}
	}
}
