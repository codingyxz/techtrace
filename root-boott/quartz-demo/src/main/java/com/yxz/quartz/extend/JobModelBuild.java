package com.yxz.quartz.extend;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface JobModelBuild {

    /**
     * 生成单个jobModel，适用于单个job构建
     *
     * @return
     */
    QuartzJobModel buildJobModel() throws SchedulerException;

    /**
     * 构建默认jobModel
     *
     * @return
     */
    QuartzJobModel generateDefaultJobModel();

    /**
     * 定时任务真正执行逻辑的方法
     *
     * @param context
     * @throws JobExecutionException
     */
    void executeInternalExpand(JobExecutionContext context) throws JobExecutionException;


    @Getter
    final class QuartzJobModel {
        /**
         * 任务名称
         */
        private String jobName;
        /**
         * 任务组名
         */
        private String jobGroup;
        /**
         * 任务执行类
         */
        private Class<? extends Job> jobClass;
        /**
         * cron表达式，与简单任务二选一，cron优先
         */
        private String cron;
        /**
         * 简单任务配置
         */
        private SimpleConfig simpleConfig;
        /**
         * 触发器名称
         */
        private String triggerName;
        /**
         * 触发器组名
         */
        private String triggerGroup;

        /**
         * job附加参数
         */
        private Map<String, Object> jobParams;

        /**
         * trigger附加参数
         */
        private Map<String, Object> triggerParams;

        public String getCron() {
            if (StringUtils.isBlank(cron)) {
                return cron;
            }
            // 兼容spring定时任务cron表达式
            String[] s = cron.split(" ");
            if (s.length == 6 && "*".equals(s[3])) {
                s[5] = "?";
                return String.join(" ", s);
            }
            return cron;
        }

        public static QuartzJobModel create() {
            return new QuartzJobModel();
        }

        public QuartzJobModel withJobClass(Class<? extends Job> jobClass) {
            this.jobClass = jobClass;
            return this;
        }

        public QuartzJobModel withJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public QuartzJobModel withJobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
            return this;
        }

        public QuartzJobModel withCron(String cron) {
            this.cron = cron;
            return this;
        }

        public QuartzJobModel withSimpleConfig(Date taskStartTime, int taskInterval, String intervalUnit) {
            this.simpleConfig = new SimpleConfig(taskStartTime, taskInterval, intervalUnit);
            return this;
        }

        public QuartzJobModel withTriggerName(String triggerName) {
            this.triggerName = triggerName;
            return this;
        }

        public QuartzJobModel withTriggerGroup(String triggerGroup) {
            this.triggerGroup = triggerGroup;
            return this;
        }

        public QuartzJobModel withJobParams(String key, Object value) {
            if (jobParams == null) {
                jobParams = new HashMap<>();
            }
            jobParams.put(key, value);
            return this;
        }

        public QuartzJobModel withTriggerParams(String key, Object value) {
            if (triggerParams == null) {
                triggerParams = new HashMap<>();
            }
            triggerParams.put(key, value);
            return this;
        }

        /**
         * triggerName默认使用jobName
         *
         * @return
         */
        public String getTriggerName() {
            return triggerName == null ? jobName : triggerName;
        }

        /**
         * triggerGroup默认使用jobGroup
         *
         * @return
         */
        public String getTriggerGroup() {
            return triggerGroup == null ? jobGroup : triggerGroup;
        }

        public JobKey getJobKey() {
            return JobKey.jobKey(jobName, jobGroup);
        }

        public TriggerKey getTriggerKey() {
            return TriggerKey.triggerKey(getTriggerName(), getTriggerGroup());
        }
    }

    @Getter
    class SimpleConfig {

        private static final int ONE_MINUTE = 60;
        private static final int ONE_HOUR = ONE_MINUTE * 60;
        private static final int ONE_DAY = ONE_HOUR * 24;
        private static final int ONE_WEEK = ONE_DAY * 7;

        public SimpleConfig(Date taskStartTime, int taskInterval, String intervalUnit) {
            this.taskStartTime = taskStartTime;
            this.taskInterval = taskInterval;
            this.intervalUnit = intervalUnit;
        }

        /**
         * 任务开始时间
         */
        private final Date taskStartTime;
        /**
         * 任务间隔
         */
        private final int taskInterval;
        /**
         * 任务间隔单位：day:天，week:周，hour:小时
         */
        private final String intervalUnit;

        public int getIntervalSeconds() {
            if (taskInterval <= 0) {
                throw new RuntimeException("任务间隔不能小于0");
            }
            if ("hour".equalsIgnoreCase(intervalUnit)) {
                return taskInterval * ONE_HOUR;
            } else if ("day".equalsIgnoreCase(intervalUnit)) {
                return taskInterval * ONE_DAY;
            } else if ("week".equalsIgnoreCase(intervalUnit)) {
                return taskInterval * ONE_WEEK;
            } else {
                throw new RuntimeException("不支持的间隔单位：" + intervalUnit);
            }
        }
    }

}
