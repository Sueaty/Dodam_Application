package com.example.sm_pc.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth, Auth;
    private Button dateButton, saveButton;
    private RadioButton boyButton, girlButton, undecidedButton;
    private TextView ddayText, babyName;
    private ListView listView;
    private String userID;
    private static final String TAG = "SettingsActivity";

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;
    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dateButton = (Button) findViewById(R.id.dateButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        boyButton = (RadioButton) findViewById(R.id.buttonBoy);
        girlButton = (RadioButton) findViewById(R.id.buttonGirl);
        undecidedButton = (RadioButton) findViewById(R.id.buttonUndecided);
        ddayText = (TextView) findViewById(R.id.ddaydate);
        babyName = (TextView) findViewById(R.id.textBabyName);
        listView = (ListView) findViewById(R.id.listView);

        auth = FirebaseAuth.getInstance();
        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentUser.getUid();


        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ddaydate = ddayText.getText().toString().trim();
                final String textBabyName = babyName.getText().toString().trim();
                final String genderB = "Boy";
                final String genderG = "Girl";
                final String genderU = "Undecided";
                DatabaseReference Baby = mRootRef.child("Baby");
                DatabaseReference user = Baby.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference name = user.child("name");
                name.setValue(textBabyName);
                DatabaseReference date = user.child("date");
                date.setValue(ddaydate);
                DatabaseReference gender = user.child("gender");
                if(boyButton.isChecked()){gender.setValue(genderB);}
                else if(girlButton.isChecked()){gender.setValue(genderG);}
                else if(undecidedButton.isChecked()){gender.setValue(genderU);}
            }
        });


        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createDialog(DATE_DIALOG_ID).show();
            }
        });



        Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(dYear, dMonth, dDay);
        updateDisplay();
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserInformation uInfo = new UserInformation();
            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());
            uInfo.setHeight(ds.child(userID).getValue(UserInformation.class).getHeight());
            uInfo.setWeight(ds.child(userID).getValue(UserInformation.class).getWeight());
            uInfo.setRegisterDate(ds.child(userID).getValue(UserInformation.class).getRegisterDate());

            Log.d(TAG, uInfo.getEmail());
            Log.d(TAG, uInfo.getHeight());
            Log.d(TAG, uInfo.getWeight());
            Log.d(TAG, uInfo.getRegisterDate());

            ArrayList<String> array = new ArrayList<>();
            array.add(uInfo.getEmail());
            array.add(uInfo.getHeight());
            array.add(uInfo.getWeight());
            array.add(uInfo.getRegisterDate());
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            listView.setAdapter(adapter);
        }
    }



    private void updateDisplay() {
        if(dYear == 1){ ddayText.setText("출산예정일");}
        else{ ddayText.setText(String.format("%d년 %d월 %d일", dYear, dMonth + 1, dDay));}
    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);
            updateDisplay();
        }

    };

    protected Dialog createDialog(int id){
        if(id==DATE_DIALOG_ID){
            return new DatePickerDialog(this, dDateSetListener,tYear,tMonth, tDay);
        }
        return null;
    }

}
