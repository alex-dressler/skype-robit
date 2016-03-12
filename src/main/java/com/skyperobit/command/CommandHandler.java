package com.skyperobit.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.skyperobit.App;
import com.skyperobit.Config;
import com.skyperobit.command.general.CreateCommandCommand;
import com.skyperobit.command.general.EightBallCommand;
import com.skyperobit.command.general.PingCommand;
import com.skyperobit.command.general.RegisterChatCommand;
import com.skyperobit.command.general.RollCommand;
import com.skyperobit.command.general.ToMOnlineCommand;
import com.skyperobit.command.general.UptimeCommand;
import com.skyperobit.command.youtube.AddYouTubeChannelCommand;
import com.skyperobit.command.youtube.ListYouTubeChannelsCommand;
import com.skyperobit.command.youtube.RemoveYouTubeChannelCommand;
import com.skyperobit.model.ChatModel;
import com.skyperobit.model.CustomCommandModel;

public class CommandHandler
{
	private static final Logger LOG = Logger.getLogger(CommandHandler.class);
	private Map<String, Command> commands;
	
	public CommandHandler()
	{
		commands = new HashMap<>();
		
		//Call & response commands
		commands.put("ping", new PingCommand("pong!"));
		commands.put("ding", new PingCommand("dong!"));
		commands.put("ching", new PingCommand("chong!"));
		commands.put("bing", new PingCommand("bong!"));
		commands.put("king", new PingCommand("kong!"));
		commands.put("sing", new PingCommand("song!"));
		commands.put("ting", new PingCommand("tong!"));
		commands.put("yin", new PingCommand("yang!"));
		commands.put("shoot", new PingCommand("bang!"));
		
		commands.put("createcommand", new CreateCommandCommand());
		
		//misc commands
		commands.put("roll", new RollCommand());
		commands.put("8ball", new EightBallCommand());
		commands.put("uptime", new UptimeCommand());
		commands.put("tomonline", new ToMOnlineCommand());
		
		//YouTube commands
		commands.put("register", new RegisterChatCommand());
		commands.put("addytchannel", new AddYouTubeChannelCommand());
		commands.put("removeytchannel", new RemoveYouTubeChannelCommand());
		commands.put("listytchannels", new ListYouTubeChannelsCommand());
	}
	
	public void handleCommand(ReceivedMessage message, Chat chat)
	{
		String commandString = message.getContent().asPlaintext().substring(1);
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
				commands.get(commandName.toLowerCase()).execute(argString, message, chat);
			}
			else
			{
				processCustomCommand(commandString, chat);
			}
		}
	}
	
	private boolean processCustomCommand(String commandString, Chat chat)
	{
		Session session = App.getSessionFactory().openSession();
		ChatModel chatModel = App.getChatDao().getChat(chat.getIdentity(), session);
		
		if(chatModel!=null)
		{
			Set<CustomCommandModel> customCommands = chatModel.getCustomCommands();
			if(customCommands!=null)
			{
				for(CustomCommandModel customCommand : customCommands)
				{
					if(commandString.equals(customCommand.getCode()))
					{
						try
						{
							chat.sendMessage(customCommand.getValue());
						}
						catch (ConnectionException e)
						{
							LOG.error("Failed to send message", e);
						}
					}
				}
			}
		}
		
		return false;
	}
}
