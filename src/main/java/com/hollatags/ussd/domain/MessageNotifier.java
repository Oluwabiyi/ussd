package com.hollatags.ussd.domain;

public interface MessageNotifier {

    public void onMessage(String sender, String receiver, String message);
}
