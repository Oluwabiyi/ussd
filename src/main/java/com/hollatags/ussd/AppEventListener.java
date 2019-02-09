package com.hollatags.ussd;

import com.hollatags.ussd.domain.Connection;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class AppEventListener {

    private Connection connection;

    @PostConstruct
    public void onInit(){
        System.out.println("Starting app");
        connection = new Connection();
    }


    public Connection getConnection(){
        return this.connection;
    }

    @PreDestroy
    public void onDestry(){
        connection.disconnect();
        System.out.println("App Stopped");
    }
}
