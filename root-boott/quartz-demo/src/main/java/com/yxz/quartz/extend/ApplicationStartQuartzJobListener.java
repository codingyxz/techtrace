package com.yxz.quartz.extend;

import com.yxz.quartz.job.BaseJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private QuartzDemoScheduler quartzDemoScheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        try {
            ApplicationContext applicationContext = event.getApplicationContext();

            String[] namesForType = applicationContext.getBeanNamesForType(QuartzJobBean.class);
            for (String beanName : namesForType) {
                JobModelBuild.QuartzJobModel quartzJobModel = applicationContext.getBean(beanName, BaseJob.class).buildJobModel();
                quartzDemoScheduler.addJob(quartzJobModel);
            }

            quartzDemoScheduler.start();
        } catch (Exception ex) {
            System.out.println("-----------------");
            System.out.println(ex.getStackTrace());
        }

    }
}
