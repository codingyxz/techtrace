package com.yxz.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemoJob extends BaseJob {

    @Value("${demo.cron:*/5 * * * * ?}")
    private String cron;

    @Override
    public QuartzJobModel buildJobModel() throws SchedulerException {
        return generateDefaultJobModel().withCron(cron);
    }

    @Override
    public void executeInternalExpand(JobExecutionContext context) throws JobExecutionException {
        System.out.println("----------------------- demo scheduler start ---------------------");
    }

}
