package com.test.rabbit.component;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling  //开启定时任务
@Component
public class TestScheduling {

    //5秒后每隔30秒，执行该方法
    @Scheduled(cron = "5/30 * * * * ? ")  //http://cron.qqe2.com/  cron解析在线生成器
    public void schedulingTest(){
        System.out.println("执行了schedulingTest方法====================");
    }

}
