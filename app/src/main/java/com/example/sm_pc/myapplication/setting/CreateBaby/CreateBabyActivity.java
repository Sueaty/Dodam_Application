package com.example.sm_pc.myapplication.setting.CreateBaby;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateBabyActivity extends AppCompatActivity {

    DatabaseReference databaseReference, currentUser;
    private Button buttonDate, buttonSave;
    private RadioButton buttonBoy, buttonGirl, buttonUndecided;
    private EditText textBabyName;
    private TextView textDate;

    private int tYear, tMonth, tDay;
    private int dYear = 1, dMonth = 1, dDay = 1;

    static final int DATE_DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_baby);

        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonBoy = (RadioButton) findViewById(R.id.buttonBoy);
        buttonGirl = (RadioButton) findViewById(R.id.buttonGirl);
        buttonUndecided = (RadioButton) findViewById(R.id.buttonUndecided);
        textBabyName = (EditText) findViewById(R.id.textBabyName);
        textDate = (TextView) findViewById(R.id.textDate);

        databaseReference = FirebaseDatabase.getInstance().getReference("Baby");
        currentUser = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addChild();
            }
        });
    }

    public void addChild(){
        String babyName = textBabyName.getText().toString();
        String genderB = "Boy";
        String genderG = "Girl";
        String genderUn = "Undecided";
        String babyDate = textDate.getText().toString();

        if(!TextUtils.isEmpty(babyName) && !TextUtils.isEmpty(babyDate)){
            String id = currentUser.push().getKey();
            Baby baby = new Baby(babyName, babyDate);
            currentUser.child(id).setValue(baby);
            textBabyName.setText("");
            textDate.setText("");

        }else{
            Toast.makeText(CreateBabyActivity.this, "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}
