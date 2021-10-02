package com.example.memorylosscompanion;

public class Chat {

    private String sender;
    private String message;
    private String uName;

    public Chat(String sender, String message, String uName) {
        this.sender = sender;
        this.message = message;
        this.uName = uName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Chat(){

    }
}
