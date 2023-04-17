package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class attendanceHome extends AppCompatActivity {

    private Button newClassButton;
    private Button ExistingClassButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_home);

        newClassButton = findViewById(R.id.newClassButton);
        newClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(attendanceHome.this,newClassHome.class);
                startActivity(intent);
            }
        });
        ExistingClassButton = findViewById(R.id.ExistingClassButton);
        ExistingClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(attendanceHome.this,existingClassHome.class);
                startActivity(intent);


            }
        });
    }
}