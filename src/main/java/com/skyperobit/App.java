package com.skyperobit;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.skyperobit.dao.ChatDao;
import com.skyperobit.event.MessageReceivedEventListener;
import com.skyperobit.task.youtube.CheckYouTubeChannelTask;

public class App
{	
	private static final Logger LOG = Logger.getLogger(App.class);
	private static final ChatDao chatDao = new ChatDao();
	
	public static long startTime;
	
	private static SessionFactory sessionFactory;
	private static Skype skype;
	private static YouTube youtube;
	
    public static void main(String[] args)
    {
    	startTime = System.currentTimeMillis();
    	
    	createShutdownHook();
    	
    	initializeHibernate();
    	
    	//initialize APIs
    	initializeSkype();
    	initializeYoutube();
    	
    	//keep this last, it needs all services to be started
    	initializeTimerJobs();
    }

	private static void initializeHibernate()
	{
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	private static void initializeSkype()
    {
		if(skype!=null)
		{
			try
			{
				skype.logout();
			}
			catch (ConnectionException e)
			{
				LOG.error("Exception when logging out of skype", e);
			}
		}
		
    	LOG.info("Starting Skype listener...");
    	
    	skype = new SkypeBuilder(Config.getString("skype.username", null), Config.getString("skype.password", null))
				.withAllResources().build();
    	
    	try
    	{
			skype.login();
		} 
    	catch (ConnectionException | InvalidCredentialsException | NotParticipatingException e) 
    	{
    		LOG.error("Failed to log in to Skype: ", e);
		}
    	
    	skype.getEventDispatcher().registerListener(new MessageReceivedEventListener());
    	
    	try
    	{
			skype.subscribe();
			LOG.info("Skype started successfully!");
		} 
    	catch (ConnectionException e) 
    	{
    		LOG.error("Failed to subscribe to skype service: ", e);
		}
    }
    
    private static void initializeYoutube()
    {
    	try
    	{
    		LOG.info("Initializing YouTube service...");
			youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
		        @Override
		        public void initialize(HttpRequest httpRequest) throws IOException {

		        }
		    }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(Config.getString("google.api.key", null))).setApplicationName("skype-robit").build();
			LOG.info("YouTube started!");
		} 
    	catch (Exception e)
    	{
			LOG.error("Failed to create YouTube instance", e);
		}
    }
    
    private static void initializeTimerJobs()
	{
		ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);
		//55 minute delay to ensure the login doesn't happen at the same time as other jobs
		timer.scheduleAtFixedRate(() -> initializeSkype(), 55, 60, TimeUnit.MINUTES);
		timer.scheduleAtFixedRate(new CheckYouTubeChannelTask(), 0, Config.getLong("youtube.notification.interval", -1), TimeUnit.MINUTES);
	}
    
    /*
     * Flushes the log when the JVM exits
     */
    private static void createShutdownHook()
    {
    	Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LogManager.shutdown();
            }        
        });
    }
    
    public static SessionFactory getSessionFactory()
    {
    	return sessionFactory;
    }
    
    public static Skype getSkype()
    {
    	return skype;
    }
    
    public static YouTube getYoutube()
    {
    	return youtube;
    }
    
    public static ChatDao getChatDao()
    {
    	return chatDao;
    }
}
