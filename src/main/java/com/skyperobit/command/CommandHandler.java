package com.skyperobit.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.Config;
import com.skyperobit.command.impl.PingCommand;
import com.skyperobit.command.impl.RollCommand;

public class CommandHandler
{
	private Map<String, Command> commands;
	
	public CommandHandler()
	{
		commands = new HashMap<>();
		commands.put("ping", new PingCommand());
		commands.put("roll", new RollCommand());
	}
	
	public void handleCommand(String commandString, Chat chat)
	{
		Pattern regex = Pattern.compile("(?<commandName>\\S+)\\s*(?<argString>.*)");
		Matcher matcher = regex.matcher(commandString);
		
		if(!matcher.matches())
		{
			//TODO: replace with Log4J
			System.out.println("'" + commandString + "' is not a valid command. Ignoring.");
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
