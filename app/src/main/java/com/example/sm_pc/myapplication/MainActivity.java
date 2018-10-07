
package com.example.sm_pc.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.BabyBot.BabyActivity;
import com.example.sm_pc.myapplication.DodamBot.BotActivity;
import com.example.sm_pc.myapplication.Hospital.AddReminderActivity;
import com.example.sm_pc.myapplication.Hospital.AlarmCursorAdapter;
import com.example.sm_pc.myapplication.Hospital.Data.AlarmReminderContract;
import com.example.sm_pc.myapplication.Hospital.Data.AlarmReminderDbHelper;
import com.example.sm_pc.myapplication.Hospital.HospitalMainActivity;
import com.example.sm_pc.myapplication.account.LoginActivity;
import com.example.sm_pc.myapplication.diary.DiaryMainActivity;
import com.example.sm_pc.myapplication.setting.Baby.BabyCreateActivity;
import com.example.sm_pc.myapplication.setting.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DayOfWeek;

import java.text.DateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.sm_pc.myapplication.Hospital.HospitalMainActivity.CHANNEL_ID;

public class MainActivity extends AppCompatActivity{
    /*
1. 1주~2주
2. 3주~4주
3. 5주
4. 6주
5. 7주~9주
6. 10주~13주
7. 14주~18주
8. 19주~22주
9. 23주~28주
10. 29주~34주
11. 35주~40주
 */
    private TextView textName,textDdate, textToday;
    private ImageButton buttonSetting;
    private Button signOut, buttonDodam, buttonBaby, buttonDiary, buttonHospital;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootref;
        private FloatingActionButton mAddReminderButton;
        private Toolbar mToolbar;
        //public static final String CHANNEL_ID="channel";

    AlarmCursorAdapter mCursorAdapter;
        AlarmReminderDbHelper alarmReminderDbHelper = new AlarmReminderDbHelper(this);
        ListView reminderListView;
        TextView reminderText;

        private String alarmTitle = "";

        private static final int VEHICLE_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();

        textName = (TextView) findViewById(R.id.mainBabyName);
        textDdate = (TextView) findViewById(R.id.mainBabyDday);
        textToday = (TextView) findViewById(R.id.todayDate);
        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        signOut = (Button) findViewById(R.id.logOut);
        buttonDodam = (Button) findViewById(R.id.buttonMainDodam);
        buttonBaby = (Button) findViewById(R.id.buttonMainBaby);
        buttonDiary = (Button) findViewById(R.id.buttonMainDiary);
        buttonHospital =(Button)findViewById(R.id.buttonMainHospital);
        String userUID = mAuth.getCurrentUser().getUid();
        mRootref.child("Baby").child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( (dataSnapshot.exists()) && (dataSnapshot.hasChild("Name")) && (dataSnapshot.hasChild("LeftDate")) ){
                    String getBabyName = dataSnapshot.child("Name").getValue().toString();
                    String getBabyDate = dataSnapshot.child("LeftDate").getValue().toString();

                    textDdate.setText(getBabyDate);
                    textName.setText(getBabyName);
                }
                else{ Toast.makeText(MainActivity.this, "Failed to Retrieve Data from Database", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dodamClick();
        babyClick();
        diaryClick();
        settingClick();
        signOutClick();
        hospitalClick();
        updateTodaydate();
    }


    public void updateTodaydate(){
        Calendar calendar = Calendar.getInstance();
        int todayDay, todayMonth, todayYear;
        String updateDate;

        todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        todayMonth = calendar.get(Calendar.MONTH);
        todayYear = calendar.get(Calendar.YEAR);

        if((todayMonth + 1)  < 10 && todayDay < 10)
        { updateDate = String.format(Locale.getDefault(), "%d년" + 0 + "%d월" + 0 + "%d일", todayYear, todayMonth + 1, todayDay); }
        else if ( (todayMonth + 1)  < 10 )
        { updateDate = String.format(Locale.getDefault(), "%d년" + 0 + "%d월%d일", todayYear, todayMonth + 1, todayDay); }
        else if ( todayDay < 10 )
        { updateDate = String.format(Locale.getDefault(), "%d년%d월" + 0 + "%d일", todayYear, todayMonth + 1, todayDay); }
        else
        { updateDate = String.format(Locale.getDefault(), "%d년%d월%d일", todayYear, todayMonth + 1, todayDay); }
        textToday.setText(updateDate);
    }

    // On CLICK ACTIVITIES //
    //=======================================================================================================================================================//
    private void dodamClick(){
        buttonDodam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BotActivity.class);
                startActivity(intent);
            }
        });
    }

    private void babyClick(){
        buttonBaby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, BabyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void hospitalClick(){
        buttonHospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, HospitalMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void diaryClick(){
        buttonDiary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, DiaryMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void settingClick(){
        buttonSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signOutClick(){
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    //=======================================================================================================================================================//

}
