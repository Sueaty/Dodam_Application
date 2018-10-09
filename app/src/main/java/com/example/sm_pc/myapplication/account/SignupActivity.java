package com.example.sm_pc.myapplication.account;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.sm_pc.myapplication.DodamBot.BotActivity;
import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, momHeight, momWeight, findMomEmail;
    private Button btnSignUp, btnFind;
    private RadioButton btnMom, btnDad;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        mRootref = FirebaseDatabase.getInstance().getReference();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnFind = (Button) findViewById(R.id.findButton);
        btnMom = (RadioButton) findViewById(R.id.momButton);
        btnDad = (RadioButton) findViewById(R.id.dadButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        momHeight = (EditText) findViewById(R.id.textMomHeight);
        momWeight = (EditText) findViewById(R.id.textMomWeight);
        findMomEmail = (EditText) findViewById(R.id.findMomEmail);

        findMomEmail.setVisibility(View.GONE);
        btnFind.setVisibility(View.GONE);
        momHeight.setVisibility(View.GONE);
        momWeight.setVisibility(View.GONE);

        ifMomChecked();
        ifDadChecked();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Calendar calendar = Calendar.getInstance();
                                String registerDate = DateFormat.getDateInstance().format(calendar.getTime());
                                String userUID = mAuth.getCurrentUser().getUid();

                                if (task.isSuccessful() && btnMom.isChecked()){

                                    String textMomHeight = momHeight.getText().toString().trim();
                                    String textMomWeight = momWeight.getText().toString().trim();

                                    if(TextUtils.isEmpty(textMomHeight) || TextUtils.isEmpty(textMomWeight)){
                                        Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    HashMap<String, String> momProfile = new HashMap<>();
                                        momProfile.put("uid", userUID);
                                        momProfile.put("Email", email);
                                        momProfile.put("RegisterDate", registerDate);
                                        momProfile.put("Height", textMomHeight);
                                        momProfile.put("Weight", textMomWeight);

                                        mRootref.child("MomSetting").child(userUID).setValue(momProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SignupActivity.this, "엄마 안녕?", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else{
                                                    Toast.makeText(SignupActivity.this, "가입 오류!\n다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                                }

                                else if (task.isSuccessful() && btnDad.isChecked()) {
                                    String momEmail = findMomEmail.getText().toString().trim();

                                    HashMap<String, String> dadProfile = new HashMap<>();
                                        dadProfile.put("uid", userUID);
                                    dadProfile.put("Email", email);
                                    dadProfile.put("RegisterDate", registerDate);
                                    dadProfile.put("momEmail", momEmail);

                                    mRootref.child("DadSetting").child(userUID).setValue(dadProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                Toast.makeText(SignupActivity.this, "아빠 안녕?", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else{
                                                Toast.makeText(SignupActivity.this, "가입 오류!\n다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });
                                }

                                else{
                                    Toast.makeText(SignupActivity.this, "회원가입 오류" + task.getException(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void ifMomChecked() {
        btnMom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btnFind.setVisibility(View.GONE);
                findMomEmail.setVisibility(View.GONE);
                momHeight.setVisibility(View.VISIBLE);
                momWeight.setVisibility(View.VISIBLE);

            }
        });

    }
    private void ifDadChecked() {
        btnDad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                ifBtnFindChecked();
                btnFind.setVisibility(View.VISIBLE);
                momHeight.setVisibility(View.GONE);
                momWeight.setVisibility(View.GONE);
            }
        });
    }
    public void ifBtnFindChecked() {
        btnFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                findMomEmail.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

