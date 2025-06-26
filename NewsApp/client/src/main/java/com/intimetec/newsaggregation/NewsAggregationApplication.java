package com.intimetec.newsaggregation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.AuthClient;
import com.intimetec.newsaggregation.app.ConsoleMenu;

public class NewsAggregationApplication {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AuthClient authClient = new AuthClient(mapper);
        new ConsoleMenu().start();
    }
}
