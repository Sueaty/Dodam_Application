package com.example.sm_pc.myapplication.setting.CreateBaby;

public class Baby {
    private String babyName;
    private String babyDate;

    public Baby(){

    }


    public Baby(String babyName, String babyDate){
        this.babyName = babyName;
        this.babyDate = babyDate;
    }

    public String getBabyName() {
        return babyName;
    }

    public String getBabyDate() {
        return babyDate;
    }


}
