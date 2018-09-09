package com.example.sm_pc.myapplication.setting;

public class LoginInfo {
    //make the field name same with Firebase
    private String email;
    private String registerDate;

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getRegisterDate(){
        return registerDate;
    }
    public void setRegisterDate(String registerDate){
        this.registerDate = registerDate;
    }
}
