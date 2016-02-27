package com.skyperobit.command;

import com.samczsun.skype4j.chat.Chat;

public interface Command
{
	public void execute(String argString, Chat chat);
}
