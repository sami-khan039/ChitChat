package com.example.chitchat;

public class Users {

    private String Name,Status,Image;
    public Users(){}

    public Users(String name, String status, String image) {
        Name = name;
        Status = status;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
