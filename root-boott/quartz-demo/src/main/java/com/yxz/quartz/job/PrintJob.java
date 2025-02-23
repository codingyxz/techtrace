package com.yxz.quartz.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class PrintJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new Date() + ": 任务[PrintJob]被执行");
    }


    public static void main(String[] args) throws SchedulerException {
        // 1、创建调度器 Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2、创建JobDetails实例，并与PrintJob类绑定（Job执行内容）
        JobDetail jobDetail = JobBuilder
                .newJob(PrintJob.class)
                .withIdentity("jobPrint", "printGroup")
                .build();

        // 3、构建Trigger实例，每隔1s执行一次
        SimpleTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("triggerPrint", "printGroup")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever())
                .build();

        // 4、Scheduler绑定Job和Trigger，并执行
        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println("----------------------- print scheduler start ---------------------");
        scheduler.start();

    }
}
