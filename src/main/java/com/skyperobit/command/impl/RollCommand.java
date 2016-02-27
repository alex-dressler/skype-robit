package com.skyperobit.command.impl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.samczsun.skype4j.chat.Chat;
import com.skyperobit.command.Command;

public class RollCommand implements Command
{	
	@Override
	public void execute(String argString, Chat chat)
	{
		ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("JavaScript");
		try
		{
			chat.sendMessage(engine.eval(rollDice(argString)).toString());
		} 
		catch (Exception e) {
			//TODO replace with Log4J
			e.printStackTrace();
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
