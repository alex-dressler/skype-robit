package com.skyperobit;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.skyperobit.event.MessageReceivedEventListener;

public class App
{	
	private static Skype skype;
	
    public static void main(String[] args)
    {
		skype = new SkypeBuilder(Config.getString("skype.username", null), Config.getString("skype.password", null))
				.withAllResources().build();
    	
    	try
    	{
			skype.login();
		} 
    	catch (ConnectionException | InvalidCredentialsException | NotParticipatingException e) 
    	{
    		//TODO: replace with Log4J and add useful message
			e.printStackTrace();
		} 
    	skype.getEventDispatcher().registerListener(new MessageReceivedEventListener());
    	try
    	{
			skype.subscribe();
		} 
    	catch (ConnectionException e) 
    	{
    		//TODO: replace with Log4J and add useful message
    		e.printStackTrace();
		}
    }
    
    public static Skype getSkype()
    {
    	return skype;
    }
}
