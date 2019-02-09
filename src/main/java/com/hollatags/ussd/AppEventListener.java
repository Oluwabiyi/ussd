package com.hollatags.ussd;

import com.cloudhopper.smpp.SmppBindType;
import com.hollatags.ussd.domain.Connection;
import ng.digitalpulse.smpp.module.Session;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class AppEventListener {

    private Session session;


    @PostConstruct
    public void onInit(){
        System.out.println("Starting app");
        session = new Session("Glo.Bind", "Glo.Bind", "holat","holat2", null, SmppBindType.TRANSCEIVER, "41.203.65.15", 2101);
        session.setSmsReceiver(new Session.SmsListener() {
            @Override
            public void onMessage(String sender, String receiver, String message) {

            }
        });
        session.bindSession();
    }


    public Session getConnection(){
        return this.session;
    }

    @PreDestroy
    public void onDestry(){
        session.unBindSession();
        System.out.println("App Stopped");
    }
}
