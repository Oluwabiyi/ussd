package com.hollatags.ussd.domain;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.*;

import java.util.Objects;

public class Connection {

    private BindService bindService;

    public Connection() {
        bindService = new BindService();
        bindService.bind(new MessageNotifier() {
            @Override
            public void onMessage(String sender, String receiver, String message) {
                System.out.println("Sender: "+sender+", Receiver: "+receiver+", Message: "+message);
            }
        });
    }

    public boolean sendSms(String sender, String receiver, String sms) {
        try {
            byte[] textBytes = CharsetUtil.encode(sms, CharsetUtil.CHARSET_GSM);
            SubmitSm submit = new SubmitSm();
            submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
            submit.setSourceAddress(new Address((byte) 0x03, (byte) 0x00, sender));
            submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, receiver));
            submit.setShortMessage(textBytes);
            SubmitSmResp submitResp = bindService.getSession().submit(submit, 10000);
            System.out.println(submitResp);
            return true;
        } catch (SmppInvalidArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RecoverablePduException e) {
            e.printStackTrace();
        } catch (SmppChannelException e) {
            e.printStackTrace();
        } catch (SmppTimeoutException e) {
            e.printStackTrace();
        } catch (UnrecoverablePduException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean sendUssd(String sender, String receiver, String message, Integer messageType) {
        try {
            byte[] textBytes = CharsetUtil.encode(message, CharsetUtil.CHARSET_GSM);
            SubmitSm submit = new SubmitSm();
            submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);

            submit.setSourceAddress(new Address((byte) 0x03, (byte) 0x00, sender));
            submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, receiver));
            submit.setShortMessage(textBytes);
            Tlv tlv;
            if (messageType == 1) {
                tlv = new Tlv(SmppConstants.TAG_USSD_SERVICE_OP, new byte[]{0x02}, "ussd_service_op");
            } else {
                tlv = new Tlv(SmppConstants.TAG_USSD_SERVICE_OP, new byte[]{0x11}, "ussd_service_op");
            }
            submit.setOptionalParameter(tlv);
            submit.setServiceType("USSD");
            bindService.getSession().sendRequestPdu(submit, 10000, false);
            return true;
        } catch (SmppInvalidArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RecoverablePduException e) {
            e.printStackTrace();
        } catch (SmppChannelException e) {
            e.printStackTrace();
        } catch (SmppTimeoutException e) {
            e.printStackTrace();
        } catch (UnrecoverablePduException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void disconnect() {
        if (Objects.nonNull(bindService)) {
            bindService.unbind();
            bindService.shutdown();
        }

    }
}
