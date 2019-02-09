package com.hollatags.ussd.domain;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnquireLinkService implements Runnable {

    private SmppSession session;
    private ScheduledExecutorService executorService;

    public EnquireLinkService() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        executorService
                .scheduleAtFixedRate(this, 10, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }

    public SmppSession getSession() {
        return this.session;
    }

    public void setSession(SmppSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        if (Objects.nonNull(session)) {
            if (session.isBound()) {
                EnquireLinkResp enquireLinkResp1 = null;
                try {
                    enquireLinkResp1 = session.enquireLink(new EnquireLink(), 10000);
                    System.out.println("enquire_link_resp #1: commandStatus ["
                            + enquireLinkResp1.getCommandStatus() + "="
                            + enquireLinkResp1.getResultMessage() + "]");
                } catch (RecoverablePduException e) {
                    e.printStackTrace();
                } catch (UnrecoverablePduException e) {
                    e.printStackTrace();
                } catch (SmppTimeoutException e) {
                    e.printStackTrace();
                } catch (SmppChannelException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
