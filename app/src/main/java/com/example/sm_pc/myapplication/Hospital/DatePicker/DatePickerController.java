package com.example.sm_pc.myapplication.Hospital.DatePicker;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public interface DatePickerController {

    void onYearSelected(int year);

    void onDayOfMonthSelected(int year, int month, int day);

    void registerOnDateChangedListener(com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.OnDateChangedListener listener);

    @SuppressWarnings("unused")
    void unregisterOnDateChangedListener(com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.OnDateChangedListener listener);

    MonthAdapter.CalendarDay getSelectedDay();

    boolean isThemeDark();

    int getAccentColor();

    boolean isHighlighted(int year, int month, int day);

    int getFirstDayOfWeek();

    int getMinYear();

    int getMaxYear();

    Calendar getStartDate();

    Calendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    void tryVibrate();

    TimeZone getTimeZone();

    Locale getLocale();

    com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.Version getVersion();

    com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.ScrollOrientation getScrollOrientation();
}
