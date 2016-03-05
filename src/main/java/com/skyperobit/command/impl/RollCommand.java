package com.skyperobit.command.impl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.skyperobit.command.Command;

public class RollCommand extends Command
{
	private static final Logger LOG = Logger.getLogger(RollCommand.class);
	
	@Override
	public void execute(String argString, ReceivedMessage message, Chat chat)
	{
		ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("JavaScript");
		
		try
		{
			String resultMessage = engine.eval(rollDice(argString)).toString();
			sendMessage(chat, resultMessage, "Roll");
		} 
		catch (ScriptException e)
		{
			LOG.error("Could not evaluate expression: " + argString, e);
		}
		
	}
	
	private String rollDice(String argString)
	{
		StringBuilder expression = new StringBuilder(argString);
		Integer dIndex = null;
		Integer numRollsIndex = null;
		for(int i=0; i<expression.length(); i++)
		{
			char c = expression.charAt(i);
			if(dIndex == null)
			{
				if(c == 'd')
				{
					dIndex = i;
				}
				if(numRollsIndex == null)
				{
					if(Character.isDigit(c))
					{
						numRollsIndex = i;
					}
					else
					{
						numRollsIndex = null;
					}
				}
			}
			else if(!Character.isDigit(c))
			{
				generateNumber(expression, numRollsIndex, dIndex, i);
				dIndex = null;
				numRollsIndex = null;
			}
		}
		
		if(dIndex!=null)
		{
			generateNumber(expression, numRollsIndex, dIndex, expression.length());
		}
		
		return expression.toString();
	}
	
	private void generateNumber(StringBuilder expression, int numRollsIndex, int dIndex, int endIndexPlusOne)
	{
		int numRolls = Integer.parseInt(expression.substring(numRollsIndex, dIndex));
		
		if(endIndexPlusOne>dIndex+1)
		{
			int numSides = Integer.parseInt(expression.substring(dIndex+1, endIndexPlusOne));
			
			int sum = 0;
			for(int i=0; i<numRolls; i++)
			{
				sum += (int)(Math.random()*numSides) + 1;
			}
			
			expression.replace(numRollsIndex, endIndexPlusOne, Integer.toString(sum));
		}
	}
}
