package com.example.sm_pc.myapplication.Hospital.DatePicker;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;

public interface DateRangeLimiter extends Parcelable {
    int getMinYear();

    int getMaxYear();

    @NonNull
    Calendar getStartDate();

    @NonNull Calendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    @NonNull Calendar setToNearestDate(@NonNull Calendar day);
}
