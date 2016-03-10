package com.skyperobit.command.impl;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.skyperobit.App;
import com.skyperobit.command.Command;

public class UptimeCommand extends Command {

	@Override
	public void execute(String argString, ReceivedMessage message, Chat chat)
	{
		String returnMessage = DurationFormatUtils.formatDuration(System.currentTimeMillis() - App.startTime, 
				"d 'days', H 'hours', m 'minutes', s 'seconds'");
		
		sendMessage(chat, returnMessage, "Uptime");
	}

}
