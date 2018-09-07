package com.example.sm_pc.myapplication.account;


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

import com.example.sm_pc.myapplication.MainActivity;
import com.example.sm_pc.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, momHeight, momWeight;
    private Button btnSignUp, btnFind;
    private RadioButton btnMom, btnDad;
    private ProgressBar progressBar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnFind = (Button) findViewById(R.id.findButton);
        btnMom = (RadioButton) findViewById(R.id.momButton);
        btnDad = (RadioButton) findViewById(R.id.dadButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        momHeight = (EditText) findViewById(R.id.textMomHeight);
        momWeight = (EditText) findViewById(R.id.textMomWeight);

        btnFind.setVisibility(View.GONE);
        momHeight.setVisibility(View.GONE);
        momWeight.setVisibility(View.GONE);



        btnMom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btnFind.setVisibility(View.GONE);
                momHeight.setVisibility(View.VISIBLE);
                momWeight.setVisibility(View.VISIBLE);
            }
        });

        btnDad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btnFind.setVisibility(View.VISIBLE);
                momHeight.setVisibility(View.GONE);
                momWeight.setVisibility(View.GONE);
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                final String registerDate = DateFormat.getDateInstance().format(calendar.getTime());
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String textMomHeight = momHeight.getText().toString().trim();
                final String textMomWeight = momWeight.getText().toString().trim();


                if(btnMom.isChecked()){
                    if(TextUtils.isEmpty(textMomHeight) || TextUtils.isEmpty(textMomWeight)){
                        Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "빠짐없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 5) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 5글자 이상으로 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final DatabaseReference Setting = mRootRef.child("Setting");
                                DatabaseReference user = Setting.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                final DatabaseReference parent = user.child("parent");
                                if (task.isSuccessful() && btnMom.isChecked()) {
                                    String parentMom = "mom";
                                    parent.setValue(parentMom);
                                    mUser muser = new mUser(
                                            registerDate,
                                            email,
                                            textMomHeight,
                                            textMomWeight
                                    );

                                    user.setValue(muser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "엄마 안녕?", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(SignupActivity.this, "가입 오류!\n다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });
                                }
                                else if (task.isSuccessful() && btnDad.isChecked()){
                                    String parentDad = "dad";
                                    parent.setValue(parentDad);
                                    dUser duser = new dUser(
                                            registerDate,
                                            email
                                    );

                                    user.setValue(duser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "아빠 안녕?", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
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

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

