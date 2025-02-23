package com.yxz.quartz.config;

import com.yxz.quartz.extend.AutowiringSpringBeanJobFactory;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 * @Author zhxy
 * @Date 2023/2/24 10:28 上午
 * @Version V1.0
 **/

@Configuration
public class QuartzConfig {

    @Value("${spring.datasource.driver-class-name}")
    public String driver;
    //JDBC连接串
    @Value("${spring.datasource.url}")
    public String url;
    //数据库用户名
    @Value("${spring.datasource.username}")
    public String user;
    //数据库用户密码
    @Value("${spring.datasource.password}")
    public String password;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory = new AutowiringSpringBeanJobFactory();
        autowiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        return autowiringSpringBeanJobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, Properties quartzProperties) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setStartupDelay(30);
        schedulerFactoryBean.setQuartzProperties(quartzProperties);
        return schedulerFactoryBean;
    }

    @Bean
    public Properties quartzProperties() {
        Properties quartzProps = new Properties();
        /**
         * Configure Main Scheduler Properties
         */
        quartzProps.setProperty("org.quartz.scheduler.instanceName", "ClusteredQuartzDemo");
        quartzProps.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        /**
         * Configure JobStore
         */
        quartzProps.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        quartzProps.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        quartzProps.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        quartzProps.setProperty("org.quartz.jobStore.useProperties", "true");
        quartzProps.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        quartzProps.setProperty("org.quartz.jobStore.dataSource", "myDS");
        quartzProps.setProperty("org.quartz.jobStore.isClustered", "true");
        quartzProps.setProperty("org.quartz.jobStore.clusterCheckinInterval", "15000");

        /**
         * Configure ThreadPool
         */
        quartzProps.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        quartzProps.setProperty("org.quartz.threadPool.threadCount", "10");
        quartzProps.setProperty("org.quartz.threadPool.threadPriority", "5");
        quartzProps.setProperty("org.quartz.threadPool.threadNamePrefix", "scheduler");
        quartzProps.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        /**
         * Configure Datasources
         */
        quartzProps.setProperty("org.quartz.dataSource.myDS.connectionProvider.class", "com.yxz.quartz.config.DruidConnectionProvider");
        quartzProps.setProperty("org.quartz.dataSource.myDS.driver", driver);
        quartzProps.setProperty("org.quartz.dataSource.myDS.URL", url);
        quartzProps.setProperty("org.quartz.dataSource.myDS.user", user);
        quartzProps.setProperty("org.quartz.dataSource.myDS.password", password);
        quartzProps.setProperty("org.quartz.dataSource.myDS.maxConnection", "5");
        return quartzProps;
    }

}
