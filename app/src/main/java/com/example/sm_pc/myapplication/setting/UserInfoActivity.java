package com.example.sm_pc.myapplication.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.MainActivity;
import com.example.sm_pc.myapplication.R;
import com.example.sm_pc.myapplication.account.LoginActivity;
import com.example.sm_pc.myapplication.account.SignupActivity;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInfoActivity extends AppCompatActivity {

    ImageView img;
    EditText newPw, newPwCheck;
    private Button signOut, changePw, enableChangePw, quitDodam;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference mRootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        img = (ImageView) findViewById(R.id.backgroundImage);
        newPw = (EditText) findViewById(R.id.textNewPwd);
        newPwCheck = (EditText) findViewById(R.id.textCheckNewPwd);
        signOut = (Button) findViewById(R.id.buttonLogOut);
        changePw = (Button) findViewById(R.id.buttonChangePwd);
        enableChangePw = (Button) findViewById(R.id.changePasswordButton);
        quitDodam = (Button) findViewById(R.id.buttonQuitDodam);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mRootref = FirebaseDatabase.getInstance().getReference();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutListener();
            }
        });

        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setVisibility(View.INVISIBLE);
                newPw.setVisibility(View.VISIBLE);
                newPwCheck.setVisibility(View.VISIBLE);
                enableChangePw.setVisibility(View.VISIBLE);
                changePwClick();
            }
        });

        quitDodam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitDodamClick();
            }
        });
    }

    private void signOutListener() {
        mAuth.signOut();
        Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(UserInfoActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void changePwClick() {
        enableChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null && newPw.getText().toString().trim().equals("")) {
                    Toast.makeText(UserInfoActivity.this, "Re-enter", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (newPw.getText().toString().trim().equals(newPwCheck.getText().toString().trim())) {
                        currentUser.updatePassword(newPw.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserInfoActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                    signOutListener();
                                } else {
                                    Toast.makeText(UserInfoActivity.this, "Failed to Update Password.\nCheck Again", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserInfoActivity.this, "Two Passwords Different.\nCheck Again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    private void quitDodamClick(){
        img.setVisibility(View.INVISIBLE);
        newPw.setVisibility(View.INVISIBLE);
        newPwCheck.setVisibility(View.INVISIBLE);
        if(currentUser != null) {
            mRootref.child("Setting").child(currentUser.getUid()).removeValue();
            mRootref.child("Baby").child(currentUser.getUid()).removeValue();
            mRootref.child("Notes").child(currentUser.getUid()).removeValue();
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UserInfoActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else{
                        Toast.makeText(UserInfoActivity.this, "Failed to delete.\nTry Again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }
    }
}

