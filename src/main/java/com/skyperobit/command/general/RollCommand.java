package com.skyperobit.command.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
			String expression = rollDice(argString);
			String result = engine.eval(expression).toString();
			sendMessage(chat, expression + "\n\t\t=" + result, "Roll");
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
				else if(numRollsIndex == null && Character.isDigit(c))
				{
					numRollsIndex = i;
				}
				else if(!Character.isDigit(c))
				{
					numRollsIndex = null;
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
	
	private void generateNumber(StringBuilder expression, Integer numRollsIndex, int dIndex, int endIndexPlusOne)
	{
		int numRolls = numRollsIndex == null ? 1 : Integer.parseInt(expression.substring(numRollsIndex, dIndex));
		
		if(endIndexPlusOne>dIndex+1)
		{
			int numSides = Integer.parseInt(expression.substring(dIndex+1, endIndexPlusOne));
			
			List<String> rolls = new ArrayList<>();
			for(int i=0; i<numRolls; i++)
			{
				Random random = new Random();
				int roll = (int)(random.nextDouble()*numSides) + 1;
				rolls.add(Integer.toString(roll));
			}
			
			expression.replace(numRollsIndex == null ? dIndex : numRollsIndex, endIndexPlusOne, sumString(rolls));
		}
	}
	
	private String sumString(List<String> rolls)
	{
		if(rolls.size()>0)
		{
			StringBuilder sum = new StringBuilder("( ");
			boolean first = true;
			for(String roll : rolls)
			{
				if(!first)
				{
					sum.append(" + ");
				}
				sum.append(roll);
				first = false;
			}
			sum.append(" )");
			
			return sum.toString();
		}
		
		return "";
	}
}
