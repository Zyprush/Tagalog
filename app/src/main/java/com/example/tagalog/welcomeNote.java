package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class welcomeNote extends AppCompatActivity {

    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_note);

        welcome = findViewById(R.id.welcome_text);

        welcome.setText("Welcome to Tag-A-Log!\nThis is a sample list");


    }
}