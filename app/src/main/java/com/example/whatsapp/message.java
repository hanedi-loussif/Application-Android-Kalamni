package com.example.whatsapp;

public class message {
    private String from, messages , type ;

    public message() {

    }

    public message(String from, String messages, String type) {
        this.from = from;
        this.messages = messages;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
