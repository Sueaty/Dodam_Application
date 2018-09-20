package com.example.sm_pc.myapplication.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.sm_pc.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInfoActivity extends AppCompatActivity {

    private TextView email, name, expectDate, dDate;
    private Button logOut, changePw, quitDodam;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //write the id of loginned user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = (TextView) findViewById(R.id.textUserShowId);
        email.setText(user.getEmail());

        //write the name of the baby
        name = (TextView) findViewById(R.id.textUserBabyName);

        //write baby expected date
        expectDate = (TextView) findViewById(R.id.textUserExpectedDate);

        String FILENAME = "BABY_NAME_FILE";
        String FILEDATE = "BABY_DATE_FILE";
        FileInputStream nameRead = null;
        FileInputStream dateRead = null;

        try {
            String textNameRead, textExpectDateRead;
            nameRead = openFileInput(FILENAME);
            dateRead = openFileInput(FILEDATE);
            InputStreamReader isr_name = new InputStreamReader(nameRead);
            InputStreamReader isr_date = new InputStreamReader(dateRead);
            BufferedReader br_name = new BufferedReader(isr_name);
            BufferedReader br_date = new BufferedReader(isr_date);
            StringBuilder sb_name = new StringBuilder();
            StringBuilder sb_date = new StringBuilder();

            while((textNameRead = br_name.readLine()) != null){
                sb_name.append(textNameRead);
            }
            name.setText(sb_name.toString());

            while((textExpectDateRead = br_date.readLine()) != null){
                sb_date.append(textExpectDateRead);
            }
            expectDate.setText(sb_date.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //calculate and write remaining days (D-day)
        dDate = (TextView) findViewById(R.id.textUserDdate);



    }
}



