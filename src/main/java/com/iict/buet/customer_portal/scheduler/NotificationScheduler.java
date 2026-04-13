package com.iict.buet.customer_portal.scheduler;

import com.iict.buet.customer_portal.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class NotificationScheduler {

//    private static final Logger logger = LogManager.getLogger(com.iict.buet.customer_portal.scheduler.NotificationScheduler.class.getName());
    private final UserService userService;

    @Autowired
    public NotificationScheduler(UserService userService){
        this.userService = userService;
    }

    @Scheduled(fixedRate = 40000)
    public void sendNotification() {
//        userService.updateAllUserPassword();
        userService.deleteAllExpiredToken();
    }
}