package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

public class logbookHome extends AppCompatActivity {

    public static final String EXTRA_TEXT3 = "com.example.tagalog.EXTRA_TEXT1";
    public static final String EXTRA_TEXT5 = "com.example.tagalog.EXTRA_TEXT3";
    public static final String EXTRA_TEXT2 = "com.example.tagalog.EXTRA_TEXT2";

    private Button logbookStart;

    private TextInputLayout officeName;
    private TextInputLayout datePickersParent;

    AutoCompleteTextView datePickers;

    CheckBox recordPurpose;
    String isChecked;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_home);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        officeName = findViewById(R.id.text_input_office);
        datePickersParent = findViewById(R.id.text_input_date_parent);
        datePickers = findViewById(R.id.text_input_date);

        recordPurpose = findViewById(R.id.checkBox);

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
        datePickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "Material_Date_Picker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        datePickers.setText(datePicker.getHeaderText());
                    }
                });
            }
        });
        //
        logbookStart = findViewById(R.id.logbookStart);
        //checked
        recordPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordPurpose.isChecked()){
                } isChecked = "-";
            }
        });
        logbookStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nfcAdapter==null) {
                    new AlertDialog.Builder(logbookHome.this)
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
                    if (!validateOffice() | !validateDate()){
                        return;
                    }
                        openLogbook();
                }
            }
        });
    }
    public boolean validateOffice() {
        String input = officeName.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            officeName.setError("Logbook Name can't be empty");
            return false;
        }  else {
            officeName.setError(null);
            return true;
        }
    }
    public boolean validateDate () {
        String input = datePickersParent.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            datePickersParent.setError("Logbook's Date can't be empty");
            return false;
        }  else {
            datePickersParent.setError(null);
            return true;
        }
    }
    public void openLogbook(){
        Intent intent = new Intent(logbookHome.this,logbookStarted.class);
        String text3 = officeName.getEditText().getText().toString().trim();
        String text2 = datePickers.getText().toString().trim();
        String text1 = isChecked;
        intent.putExtra(EXTRA_TEXT3, text3);
        intent.putExtra(EXTRA_TEXT5, text2);
        intent.putExtra(EXTRA_TEXT2, text1);
        startActivity(intent);
    }
}