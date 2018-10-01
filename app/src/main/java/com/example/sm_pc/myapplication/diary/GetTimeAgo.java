package com.example.sm_pc.myapplication.diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetTimeAgo {

    public static CharSequence createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(d);
    }

}
