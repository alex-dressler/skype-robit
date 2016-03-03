package com.skyperobit.task.youtube;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.skyperobit.Config;

public class CheckYouTubeChannelTask extends TimerTask
{
	@Override
	public void run()
	{
		List<String> usernames = Config.getList("youtube.channels", new ArrayList<>());
		StringBuilder message = new StringBuilder();
		
		for(String username : usernames)
		{
			
		}
	}
}
