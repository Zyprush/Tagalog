package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class new_write extends AppCompatActivity {

    //textinputs
    private TextInputLayout textInputId;
    private TextInputLayout textInputName;
    private TextInputLayout textInputCourse;
    private TextInputLayout textInputYear;
    private TextInputLayout textInputSection;

    private AutoCompleteTextView courses;
    private AutoCompleteTextView years;
    private AutoCompleteTextView sections;

    private String kurso, taon, seksyon;

    ArrayAdapter<CharSequence> s1, s2, s3;

    //NFC
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter[] writeTagFilters;
    boolean writeMode;
    Tag myTag;
    Context ctx;

    String fnlInput;

    private Button writeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_write);

        textInputId = findViewById(R.id.text_input_id);
        textInputName = findViewById(R.id.text_input_name);

        textInputCourse = findViewById(R.id.autoCompleteCourseParent);
        textInputYear = findViewById(R.id.autoCompleteYearParent);
        textInputSection = findViewById(R.id.autoCompleteSectionParent);
        writeButton = findViewById(R.id.my_button);

        courses = findViewById(R.id.autoCompleteCourse);
        years = findViewById(R.id.autoCompleteYear);
        sections = findViewById(R.id.autoCompleteSection);



        s1 = ArrayAdapter.createFromResource(this, R.array.courses, R.layout.dropdown_item);
        courses.setAdapter(s1);

        courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kurso = parent.getItemAtPosition(position).toString();
            }
        });
        //
        s2 = ArrayAdapter.createFromResource(this, R.array.year, R.layout.dropdown_item);
        years.setAdapter(s2);

        years.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                taon = parent.getItemAtPosition(position).toString();
            }
        });
        s3 = ArrayAdapter.createFromResource(this, R.array.section, R.layout.dropdown_item);
        sections.setAdapter(s3);

        sections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seksyon = parent.getItemAtPosition(position).toString();
            }
        });
        //NFC
        adapter = NfcAdapter.getDefaultAdapter(this);
        //pending intent version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        } else{
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }
    //validation if empty
    public boolean validateId () {
        String idInput = textInputId.getEditText().getText().toString().trim();
        if (idInput.isEmpty()) {
            textInputId.setError("Student's ID can't be empty");
            return false;
        } else {
            textInputId.setError(null);
            return true;
        }
    }
    public boolean validateName () {
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if (nameInput.isEmpty()) {
            textInputName.setError("Student's Name can't be empty");
            return false;
        }  else {
            textInputName.setError(null);
            return true;
        }
    }
    public boolean validateCourse () {
        if (kurso==null) {
            textInputCourse.setError("Student's Course can't be empty");
            return false;
        }  else {
            textInputCourse.setError(null);
            return true;
        }
    }
    public boolean validateYear () {
        if (taon==null) {
            textInputYear.setError("Student's Year can't be empty");
            return false;
        }  else {
            textInputYear.setError(null);
            return true;
        }
    }
    public boolean validateSection () {
        if (seksyon==null) {
            textInputSection.setError("Student's Section can't be empty");
            return false;
        }  else {
            textInputSection.setError(null);
            return true;
        }
    }
    public void confirmInput (View v){
        if (!validateId() | !validateName() | !validateSection() | !validateYear() | !validateCourse()){
            return;
        }
        writeNFC();
    }
    public void writeNFC() {
        fnlInput = textInputId.getEditText().getText().toString().trim();
        fnlInput += "\n";
        fnlInput += "\"";
        fnlInput += textInputName.getEditText().getText().toString().trim();
        fnlInput += "\"";
        fnlInput += "\n";
        fnlInput += kurso;
        fnlInput += "\n";
        fnlInput += taon+"-"+seksyon;
        try {
            if(myTag==null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please hold your NFC tag and press the write button at the same time to write the data.")
                        .setTitle("TAP your TAG!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                write(fnlInput,myTag);
                Intent ii = new Intent(this, MainActivity.class);
                startActivity(ii);
                Toast.makeText(this, "Tag Written!", Toast.LENGTH_SHORT ).show();
            }
        } catch (IOException e) {
            //Toast.makeText(this, "Error Writing!", Toast.LENGTH_SHORT ).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please hold your NFC tag and press the write button at the same time to write the data.")
                    .setTitle("Don't Remove your TAG!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, "NFC Format Error!", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }

    }
    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
        } else {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable != null) {
                ndefFormatable.connect();
                ndefFormatable.format(message);
                ndefFormatable.close();
            } else {
                Toast.makeText(this, "Tag cannot be written!", Toast.LENGTH_LONG).show();
            }
        }
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }
    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }
    private void WriteModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    private void WriteModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
}