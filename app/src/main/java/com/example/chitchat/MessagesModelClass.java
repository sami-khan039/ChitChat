package com.example.chitchat;

public class MessagesModelClass {


    private  String From,Message,Type;
   public MessagesModelClass(){}
    public MessagesModelClass(String from, String message, String type) {
        From = from;
        Message = message;
        Type = type;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
