package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class readHome extends AppCompatActivity {

    //private TextView textView;
    private PendingIntent pendingIntent;
    private IntentFilter[] readFilters;

    AutoCompleteTextView stdntId;
    AutoCompleteTextView stdntName;
    AutoCompleteTextView stdntSection;
    AutoCompleteTextView stdntYs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_home);
        stdntId = findViewById(R.id.id);
        stdntName = findViewById(R.id.name);
        stdntSection = findViewById(R.id.section);
        stdntYs = findViewById(R.id.cy);
        //textView = (TextView) findViewById(R.id.tagOutput);

        Toast.makeText(this, "Tap your NFC Tag!", Toast.LENGTH_LONG).show();

        try {
            Intent intent = new Intent(this, getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //pending intent version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            } else{
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            IntentFilter textFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain");

            readFilters = new IntentFilter[] {textFilter};

        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

        processNFC(getIntent());
    }
    private void enableRead() {
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, readFilters, null);
    }
    private void disableRead() {
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableRead();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableRead();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNFC(intent);
    }

    private void processNFC(Intent intent) {
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        //textView.setText("");
        if (messages != null) {
            for(Parcelable message : messages) {
                NdefMessage ndefMessage = (NdefMessage) message;
                for (NdefRecord record : ndefMessage.getRecords()) {
                    switch (record.getTnf()) {
                        case NdefRecord.TNF_WELL_KNOWN:
                            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                //Tag view

                                String str = new String(record.getPayload());

                                String tagData [] = str.split("\n");

                                stdntId.setText(tagData[0]);
                                stdntName.setText(tagData[1]);
                                stdntSection.setText(tagData[2]);
                                stdntYs.setText(tagData[3]);

                            } else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                                return;
                            } else {
                                Toast.makeText(this, "Tag was not written on this app!", Toast.LENGTH_SHORT).show();
                            }
                    }
                }
            }
        }

    }


}