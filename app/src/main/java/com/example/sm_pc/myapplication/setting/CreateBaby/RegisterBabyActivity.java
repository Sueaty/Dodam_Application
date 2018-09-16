package com.example.sm_pc.myapplication.setting.CreateBaby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.sm_pc.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterBabyActivity extends AppCompatActivity {

    private ListView listView;
    DatabaseReference databaseReference;
    List<Baby> babyList;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_baby);

        listView = (ListView) findViewById(R.id.list_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Baby").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        babyList = new ArrayList<>();
        registerButton = (Button) findViewById(R.id.buttonRegisterBaby);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterBabyActivity.this, CreateBabyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot babySnapshot : dataSnapshot.getChildren()){
                    Baby baby = babySnapshot.getValue(Baby.class);
                    babyList.add(baby);
                }
                BabyInfoAdapter babyInfoAdapter = new BabyInfoAdapter(RegisterBabyActivity.this, babyList);
                listView.setAdapter(babyInfoAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

