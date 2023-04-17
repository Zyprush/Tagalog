package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String name = getIntent().getStringExtra("name");
        List<String> datesAndStatuses = getIntent().getStringArrayListExtra("datesAndStatuses");

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(name);

        listView = (ListView) findViewById(R.id.listView123);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_list_view ,R.id.tagList, datesAndStatuses);
        listView.setAdapter(adapter);
    }
}