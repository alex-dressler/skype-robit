package com.skyperobit.command.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.skyperobit.command.Command;

public class ToMOnlineCommand extends Command
{
	private static final Logger LOG = Logger.getLogger(ToMOnlineCommand.class);

	@Override
	public void execute(String argString, ReceivedMessage message, Chat chat)
	{
		URL tom = null;
		
		try
		{
			tom = new URL("http://talesofmoonsea.net");
		}
		catch (MalformedURLException e)
		{
			LOG.error("Could not create ToM url", e);
			return;
		}
		
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(tom.openConnection().getInputStream()));
			String line;
			while((line = in.readLine()) != null)
			{
				String regex = "Players:\\s+<class='r'>(?<result>[0-9]+)\\s+/\\s+<class='r'>30";

				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(line);
					
				if(matcher.matches() && StringUtils.isNotBlank(matcher.group("result")))
				{
					sendMessage(chat, matcher.group("result"), "ToMOnline");
					return;
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("Could not connect to ToM", e);
			sendMessage(chat, "Could not connect to ToM!", "ToMOnline");
		}
	}

}
