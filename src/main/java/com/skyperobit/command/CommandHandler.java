package com.skyperobit.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.Config;
import com.skyperobit.command.impl.PingCommand;
import com.skyperobit.command.impl.RollCommand;

public class CommandHandler
{
	private static final Logger LOG = Logger.getLogger(CommandHandler.class);
	private Map<String, Command> commands;
	
	public CommandHandler()
	{
		commands = new HashMap<>();
		commands.put("ping", new PingCommand());
		commands.put("roll", new RollCommand());
	}
	
	public void handleCommand(String commandString, Chat chat)
	{
		LOG.info("Received command: " + commandString);
		
		Pattern regex = Pattern.compile("(?<commandName>\\S+)\\s*(?<argString>.*)");
		Matcher matcher = regex.matcher(commandString);
		
		if(!matcher.matches())
		{
			LOG.info("'" + commandString + "' is not a valid command. Ignoring.");
		}
		else
		{
			String commandName = matcher.group("commandName");
			String argString = matcher.group("argString");
			
			if(commandName!=null && commands.containsKey(commandName) && Config.getBoolean(commandName + ".command.enabled", true))
			{
				commands.get(commandName).execute(argString, chat);
			}
		}
	}
}
