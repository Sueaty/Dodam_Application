package com.example.sm_pc.myapplication.model;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ChatModel{
   /*public String uid;
    public String destinationUid;*/
    public Map<String, Boolean> users = new HashMap<>(); //채팅방의 유저들
    public Map<String, Comment> comments = new HashMap<>();//채팅방의 대화내용

    public static class Comment{
        public String id;
        public String uid;
        public String message;
        public Map<String,Object> readUsers = new HashMap<>();

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

}
