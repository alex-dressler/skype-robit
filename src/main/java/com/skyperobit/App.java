package com.skyperobit;

import org.apache.log4j.Logger;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.skyperobit.event.MessageReceivedEventListener;

public class App
{	
	private static final Logger LOG = Logger.getLogger(App.class);
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
    		LOG.error("Failed to log in to skype: ", e);
		} 
    	skype.getEventDispatcher().registerListener(new MessageReceivedEventListener());
    	try
    	{
			skype.subscribe();
		} 
    	catch (ConnectionException e) 
    	{
    		LOG.error("Failed to subscribe to skype service: ", e);
		}
    }
    
    public static Skype getSkype()
    {
    	return skype;
    }
}
