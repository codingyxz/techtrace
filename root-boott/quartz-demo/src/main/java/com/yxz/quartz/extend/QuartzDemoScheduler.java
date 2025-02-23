package com.yxz.quartz.extend;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class QuartzDemoScheduler {
    private static Pattern pattern = Pattern.compile("^[0-9]*$");

    @Autowired
    private Scheduler demoScheduler;

    /**
     * 加入任务到调度器
     *
     * @param jobModel 任务所需参数
     * @throws SchedulerException 调度异常
     */
    public void addJob(JobModelBuild.QuartzJobModel jobModel) throws SchedulerException {
        // 1、cron为空时，跳过该任务
        if (jobModel == null || "-".equalsIgnoreCase(jobModel.getCron())) {
            return;
        }
        // 数据库有该任务不用重复创建
        if (demoScheduler.checkExists(jobModel.getJobKey())) {
            return;
        }
        // 2、构建job信息,并填入附加参数
        JobDetail jobDetail = JobBuilder.newJob(jobModel.getJobClass())
                .withIdentity(jobModel.getJobName(), jobModel.getJobGroup())
                .build();
        if (jobModel.getJobParams() != null) {
            jobDetail.getJobDataMap().putAll(jobModel.getJobParams());
        }
        // 3、按新的cronExpression表达式构建一个新的trigger,并填入附加信息
        Trigger trigger = getJobTrigger(jobModel);
        if (jobModel.getTriggerParams() != null) {
            trigger.getJobDataMap().putAll(jobModel.getTriggerParams());
        }
        // 4、加入调度器执行
        demoScheduler.scheduleJob(jobDetail, trigger);
    }

    private Trigger getJobTrigger(JobModelBuild.QuartzJobModel jobModel) {
        if (StringUtils.isBlank(jobModel.getCron())) {
            return getSimpleTrigger(jobModel);
        }
        return getCronTrigger(jobModel);
    }

    private CronTrigger getCronTrigger(JobModelBuild.QuartzJobModel jobModel) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(compatibleCron(jobModel.getCron())).withMisfireHandlingInstructionDoNothing();
        return TriggerBuilder.newTrigger()
                .withIdentity(jobModel.getTriggerName(), jobModel.getTriggerGroup())
                .withSchedule(scheduleBuilder)
                .build();
    }

    private SimpleTrigger getSimpleTrigger(JobModelBuild.QuartzJobModel jobModel) {
        JobModelBuild.SimpleConfig simpleConfig = jobModel.getSimpleConfig();
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(simpleConfig.getIntervalSeconds()).repeatForever().withMisfireHandlingInstructionNextWithExistingCount();
        return TriggerBuilder.newTrigger()
                .withIdentity(jobModel.getTriggerName(), jobModel.getTriggerGroup())
                .withSchedule(simpleScheduleBuilder)
                .startAt(simpleConfig.getTaskStartTime())
                .build();
    }

    /**
     * 删除任务(沿用老删除逻辑)
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组名
     * @throws SchedulerException 调度异常
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (demoScheduler.getJobDetail(jobKey) == null) {
            return;
        }
        demoScheduler.deleteJob(jobKey);
    }

    /**
     * 停止任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务所属组名
     * @throws SchedulerException 调度异常
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        demoScheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 恢复任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务所属组名
     * @throws SchedulerException 调度异常
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        demoScheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 手动触发任务
     *
     * @param jobKey
     */
    public void manualTrigger(JobKey jobKey) throws SchedulerException {
        demoScheduler.triggerJob(jobKey);
    }

    /**
     * 启动任务调度器
     *
     * @throws SchedulerException 调度异常
     */
    public void start() throws SchedulerException {
        demoScheduler.start();
    }

    /**
     * quartz 兼容 @scheduled 的cron表达式
     *
     * @param cron
     * @return
     */
    private String compatibleCron(String cron) {
        if (StringUtils.isEmpty(cron) || "-".equalsIgnoreCase(cron)) {
            return null;
        }
        List<String> splitCron = Arrays.asList(cron.split(" "));
        // ? 字符仅被用于天（月）和天（星期）两个子表达式，表示不指定值,当2个子表达式其中之一被指定了值以后，为了避免冲突，需要将另一个子表达式的值设为 ?
        // 例 */5 * * * * ?
        if (pattern.matcher(splitCron.get(3)).matches()) {
            splitCron.set(5, "?");
        } else {
            if ("?".equals(splitCron.get(5))) {
                splitCron.set(3, "*");
            } else {
                splitCron.set(3, "?");
            }
        }
        return StringUtils.join(splitCron, " ");
    }


}
