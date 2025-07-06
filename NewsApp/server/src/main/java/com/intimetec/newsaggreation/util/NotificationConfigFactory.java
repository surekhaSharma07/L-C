package com.intimetec.newsaggreation.util;

import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;

public class NotificationConfigFactory {

    public static NotificationConfig createDefault(User user) {
        NotificationConfig config = new NotificationConfig();
        config.setUser(user);
        config.setBusiness(false);
        config.setEntertainment(false);
        config.setSports(false);
        config.setTechnology(false);
        config.setEnabled(true);
        config.setChannel("email");
        return config;
    }
}
