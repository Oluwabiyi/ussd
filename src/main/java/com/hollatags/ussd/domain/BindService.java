package com.hollatags.ussd.domain;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BindService {

    private SmppSession session;

    private ThreadPoolExecutor executor;

    private ScheduledThreadPoolExecutor monitorExecutor;

    private SmppSessionConfiguration config;

    private DefaultSmppClient clientBootstrap;

    private ScheduledExecutorService scheduledExecutorService;

    private EnquireLinkService enquireLinkService;

    public BindService() {
        enquireLinkService = new EnquireLinkService();
        config = new SmppSessionConfiguration();
        config.setWindowSize(1);
        config.setName("Glo.Bind");
        config.setSystemType(null);
        config.setType(SmppBindType.TRANSCEIVER);
        config.setHost("41.203.65.15");
        config.setPort(2101);
        config.setConnectTimeout(10000);
        config.setSystemId("holat");
        config.setPassword("holat2");
        config.getLoggingOptions().setLogBytes(true);
        // to enable monitoring (request expiration)
        config.setRequestExpiryTimeout(30000);
        config.setWindowMonitorInterval(15000);
        config.setCountersEnabled(true);

        clientBootstrap = new DefaultSmppClient(Executors.newCachedThreadPool(), 1, monitorExecutor);


        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        monitorExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private AtomicInteger sequence = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("SmppClientSessionWindowMonitorPool-" + sequence.getAndIncrement());
                return t;
            }
        });
    }

    public SmppSession getSession() {
        return this.session;
    }

    public void setSession(SmppSession session) {
        this.session = session;
    }

    private boolean bindSmpp(MessageNotifier messageNotifier) {
        try {
            session = clientBootstrap.bind(config, new ClientSmppSessionHandler(messageNotifier));
            System.out.println("System connected to Smpp Server");
            return true;
        } catch (SmppTimeoutException e) {
            e.printStackTrace();
        } catch (SmppChannelException e) {
            e.printStackTrace();
        } catch (UnrecoverablePduException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
return false;
    }

    public void bind(MessageNotifier messageNotifier){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                unbind();
                boolean status = bindSmpp(messageNotifier);
                if(status){
                    enquireLinkService.start();
                }
            }
        }, 0, 20, TimeUnit.MINUTES);
    }

    public void unbind() {

        if (Objects.nonNull(session)) {
            if (session.isBound()) {
                session.unbind(5000);
            }
        }

        System.out.println("System Disconnected from Smpp Server");
    }

    public void shutdown(){

        if (Objects.nonNull(clientBootstrap)) {
            clientBootstrap.destroy();
        }

        if(Objects.nonNull(executor)){
            executor.shutdown();
        }

        if(Objects.nonNull(monitorExecutor)){
            monitorExecutor.shutdown();
        }

        if(Objects.nonNull(scheduledExecutorService)){
            scheduledExecutorService.shutdown();
        }

        if(Objects.nonNull(enquireLinkService)){
            enquireLinkService.stop();
        }
    }



    public static class ClientSmppSessionHandler extends DefaultSmppSessionHandler {

        private MessageNotifier mn;

        public ClientSmppSessionHandler(MessageNotifier messageNotifier){
            this.mn = messageNotifier;
        }

        @Override
        public void firePduRequestExpired(PduRequest pduRequest) {
            System.out.println("PDU request expired: "+ pduRequest);
        }

        @Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            PduResponse response = pduRequest.createResponse();

            if(pduRequest.getCommandId() == SmppConstants.CMD_ID_DELIVER_SM){
                DeliverSm deliverSm = (DeliverSm) pduRequest;
                String sender = deliverSm.getSourceAddress().getAddress();
                String receiver = deliverSm.getDestAddress().getAddress();

                String message = new String(deliverSm.getShortMessage());
                if(Objects.isNull(message) || message.isEmpty()){
                    for(Tlv tlv: deliverSm.getOptionalParameters()){
                        if(tlv.getTag() == SmppConstants.TAG_MESSAGE_PAYLOAD){
                            message = new String(tlv.getValue());
                        }
                    }
                }

                if(Objects.nonNull(mn)){
                    mn.onMessage(sender, receiver, message);
                }
            }

            // do any logic here

            return response;
        }

    }

}
