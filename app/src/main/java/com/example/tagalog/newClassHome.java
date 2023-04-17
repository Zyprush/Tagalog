package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class newClassHome extends AppCompatActivity {

    public static final String EXTRA_TEXT1 = "com.example.tagalog.EXTRA_TEXT1";
    public static final String EXTRA_TEXT2 = "com.example.tagalog.EXTRA_TEXT2";
    public static final String EXTRA_TEXT3 = "com.example.tagalog.EXTRA_TEXT3";

    private Button createClass;
    private TextInputLayout attendanceSubject;
    private TextInputLayout textInputCourse;
    private TextInputLayout textInputYear;
    private TextInputLayout textInputSection;

    private AutoCompleteTextView courses;
    private AutoCompleteTextView years;
    private AutoCompleteTextView sections;

    private String kurso, taon, seksyon;

    ArrayAdapter<CharSequence> s1, s2, s3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class_home);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        createClass = findViewById(R.id.create_class_button);
        attendanceSubject = findViewById(R.id.text_input_subject);

        textInputCourse = findViewById(R.id.autoCompleteCourseParent);
        textInputYear = findViewById(R.id.autoCompleteYearParent);
        textInputSection = findViewById(R.id.autoCompleteSectionParent);

        courses = findViewById(R.id.autoCompleteCourse);
        years = findViewById(R.id.autoCompleteYear);
        sections = findViewById(R.id.autoCompleteSection);

        s1 = ArrayAdapter.createFromResource(this, R.array.courses, R.layout.dropdown_item);
        courses.setAdapter(s1);

        courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kurso = parent.getItemAtPosition(position).toString();
                //Toast.makeText(new_write.this, kurso, Toast.LENGTH_SHORT).show();
            }
        });
        //
        s2 = ArrayAdapter.createFromResource(this, R.array.year, R.layout.dropdown_item);
        years.setAdapter(s2);

        years.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taon = parent.getItemAtPosition(position).toString();
                //Toast.makeText(new_write.this, taon, Toast.LENGTH_SHORT).show();
            }
        });
        s3 = ArrayAdapter.createFromResource(this, R.array.section, R.layout.dropdown_item);
        sections.setAdapter(s3);

        sections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seksyon = parent.getItemAtPosition(position).toString();
                //Toast.makeText(new_write.this, seksyon, Toast.LENGTH_SHORT).show();
            }
        });

        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nfcAdapter==null) {
                    new AlertDialog.Builder(newClassHome.this)
                            .setTitle("Warning!")
                            .setMessage("NFC is not available on this device!")
                            .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                            .create().show();
                } else if (!nfcAdapter.isEnabled()) {
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    }
                } else {

                    if (!validateSubject() | !validateCourse() | !validateYear() | !validateSection()){
                        return;
                    }
                    openCreatedClass();
                }
            }
        });
    }
    public boolean validateSubject () {
        String subjInput = attendanceSubject.getEditText().getText().toString().trim();
        if (subjInput.isEmpty()) {
            attendanceSubject.setError("Subject can't be empty");
            return false;
        }  else {
            attendanceSubject.setError(null);
            return true;
        }
    }
    public boolean validateCourse () {
        if (kurso==null) {
            textInputCourse.setError("Subject's Course can't be empty");
            return false;
        }  else {
            textInputCourse.setError(null);
            return true;
        }
    }
    public boolean validateYear () {
        if (taon==null) {
            textInputYear.setError("Subject's Year can't be empty");
            return false;
        }  else {
            textInputYear.setError(null);
            return true;
        }
    }
    public boolean validateSection () {
        if (seksyon==null) {
            textInputSection.setError("Subject's Section can't be empty");
            return false;
        }  else {
            textInputSection.setError(null);
            return true;
        }
    }
    public void openCreatedClass () {
        Intent intent = new Intent(newClassHome.this, attendanceStart.class);
        String text1 = attendanceSubject.getEditText().getText().toString().trim();
        String text2 = kurso+" "+taon+"-"+seksyon;
        intent.putExtra(EXTRA_TEXT1, text1);
        intent.putExtra(EXTRA_TEXT2, text2);
        startActivity(intent);
    }
}

