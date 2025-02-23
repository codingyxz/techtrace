package com.yxz.quartz.job;

import com.yxz.quartz.extend.JobModelBuild;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class BaseJob extends QuartzJobBean implements JobModelBuild {


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        executeInternalExpand(context);
    }

    @Override
    public QuartzJobModel generateDefaultJobModel() {
        return QuartzJobModel.create()
                .withJobClass(getClass())
                .withJobName(getClass().getName())
                .withJobGroup(getClass().getName());
    }

    /**
     * 返回当前jobKey，必须通过实现了buildJobModel()的子类对象调用
     *
     * @return
     */
    public JobKey getJobKey() {
        try {
            QuartzJobModel jobModel = buildJobModel();
            return jobModel.getJobKey();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
