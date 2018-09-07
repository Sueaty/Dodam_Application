package com.example.sm_pc.myapplication;

public class UserInformation {
    private String email;
    private String height;
    private String weight;
    private String registerDate;

    public UserInformation(){

    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getHeight(){
        return height;
    }
    public void setHeight(String height){
        this.height = height;
    }
    public String getWeight(){
        return weight;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getRegisterDate(){
        return registerDate;
    }
    public void setRegisterDate(String registerDate){
        this.registerDate = registerDate;
    }
}
