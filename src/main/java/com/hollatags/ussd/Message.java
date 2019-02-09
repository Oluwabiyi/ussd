package com.hollatags.ussd;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {

    private String sender;
    private String receiver;
    private String message;
    private Integer type;

    public Message() {
    }

    public Message(String sender, String receiver, String message, Integer type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(sender, message1.sender) &&
                Objects.equals(receiver, message1.receiver) &&
                Objects.equals(message, message1.message) &&
                Objects.equals(type, message1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, message, type);
    }
}
