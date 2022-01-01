package com.bootdo.common.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class WelcomeJob implements Job{
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
    	//System.out.println("任务,,,,,,,,,,");
    }
}