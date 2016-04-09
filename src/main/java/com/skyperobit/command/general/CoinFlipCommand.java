package com.skyperobit.command.general;

import java.util.Random;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.skyperobit.command.Command;

public class CoinFlipCommand extends Command {

	@Override
	public void execute(String argString, ReceivedMessage message, Chat chat)
	{
		Random random = new Random();
		String result = random.nextBoolean() ? "Heads!" : "Tails!";
		sendMessage(chat, result, "CoinFlip");
	}

}
