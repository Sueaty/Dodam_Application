package com.example.sm_pc.myapplication.DodamBot;

import java.io.Serializable;

public class Message1 implements Serializable {
    String id, message;

    public Message1() {
    }

    public Message1(String id, String message, String createdAt) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
