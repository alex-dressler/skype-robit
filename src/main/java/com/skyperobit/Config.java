package com.skyperobit;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config
{
	private static final Logger LOG = Logger.getLogger(Config.class);
	private static Properties botProperties = new Properties();
	private static boolean isInitialized = false;
	
	public static String getString(String s, String defaultResult)
	{
		initialize();
		
		try
		{
			return botProperties.getProperty(s);
		}
		catch(Exception e)
		{
			LOG.error("Exception when retrieving property: ", e);
			return defaultResult;
		}
	}
	
	public static List<String> getList(String s, List<String> defaultResult)
	{
		initialize();
		
		try
		{
			return Arrays.asList(botProperties.getProperty(s).split(","));
		}
		catch(Exception e)
		{
			LOG.error("Exception when retrieving list property: ", e);
			return defaultResult;
		}
	}
	
	public static boolean getBoolean(String s, boolean defaultResult)
	{
		initialize();
		
		if(!botProperties.containsKey(s))
		{
			return defaultResult;
		}
		
		try
		{
			
			return Boolean.parseBoolean(botProperties.getProperty(s));
		}
		catch(Exception e)
		{
			LOG.error("Exception when retrieving boolean property: ", e);
			return defaultResult;
		}
	}
	
	private static void initialize()
	{
		if(isInitialized)
		{
			return;
		}
		
		try
		{
			botProperties.load(App.class.getResourceAsStream("/bot.properties"));
			isInitialized = true;
		} 
		catch (IOException e) 
		{
			LOG.error("Exception initializing bot.properties: ", e);
		}
	}
}
