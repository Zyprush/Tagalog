package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class    attendanceStart extends AppCompatActivity {

    //list
    ListView listView;
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayAdapter myAdapter;
    private Set<String> savedDates;

    Integer indexVal;
    String item;

    //NFC
    private PendingIntent pendingIntent;
    private IntentFilter[] readFilters;

    //FAB button
    FloatingActionButton saveFab, deleteFab, addFab;
    ExtendedFloatingActionButton menuFab;
    TextView saveText, deleteText, addText;

    boolean isAllFABVisible;
    boolean listSaved = false;

    //save export variable
    private Object fileName1="";
    private String strTime;
    private String formattedDate;
    //
    private String text1;
    private String text2;
    private String text3;
    private String attendanceShared;
    private String status = "Present";
    private String finalSubject;
    private String finalCourse;
    private String uniqueFileName;
    private TextView listCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_start);

        //this is for the input
        Intent iiintent = getIntent();

        getDate();

        text1 = iiintent.getStringExtra(newClassHome.EXTRA_TEXT1);
        text2 = iiintent.getStringExtra(newClassHome.EXTRA_TEXT2);
        text3 = iiintent.getStringExtra(newClassHome.EXTRA_TEXT3);
        TextView cysTextView = (TextView) findViewById(R.id.cysTextView);
        listCounter = (TextView) findViewById(R.id.counter);
        if (text2!=null) {
            cysTextView.setText(text1+", "+text2);
            attendanceShared = text2+"\n"+text1;
            finalSubject = text2;
            finalCourse = text1;
        } else {
            String subjTxt = iiintent.getStringExtra("subject");
            String cysTxt = iiintent.getStringExtra("cys");
            attendanceShared = subjTxt+"\n"+cysTxt;
            cysTextView.setText(cysTxt+", "+subjTxt);
            finalSubject = subjTxt;
            finalCourse = cysTxt;
        }
        //name list
        listView = (ListView) findViewById(R.id.listView);
        //addName = (EditText) findViewById(R.id.addName);

        //Timer
        TextView timerTextView = findViewById(R.id.timer);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status = "Late";
            }
        }, TimeUnit.MINUTES.toMillis(5));*/
        //Timer
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status = "Late";
            }
        }, TimeUnit.MINUTES.toMillis(5) - 1000); // Subtract 1 second to account for delay in handler

        // Countdown timer
        new CountDownTimer(TimeUnit.MINUTES.toMillis(5), 1000) {

            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minutes);

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timerTextView.setText(timeLeftFormatted);
            }

            public void onFinish() {
                timerTextView.setText("00:00");
            }
        }.start();


        Toast.makeText(this, "Tap your NFC Tag!", Toast.LENGTH_SHORT).show();
        //array loader...
        loadData();
        listView.setAdapter(myAdapter);

        //function
        saveFab = findViewById(R.id.save_fab);
        deleteFab = findViewById(R.id.delete_fab);
        addFab = findViewById(R.id.add_fab);
        menuFab = findViewById(R.id.menu_fab);

        saveText = findViewById(R.id.save_text);
        deleteText = findViewById(R.id.delete_text);
        addText = findViewById(R.id.add_text);

        saveFab.setVisibility(View.GONE);
        saveText.setVisibility(View.GONE);
        deleteFab.setVisibility(View.GONE);
        deleteText.setVisibility(View.GONE);
        addFab.setVisibility(View.GONE);
        addText.setVisibility(View.GONE);

        isAllFABVisible=false;

        menuFab.shrink();

        menuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFABVisible) {
                    isAllFABVisible=true;
                    menuFab.extend();
                    saveFab.show();
                    deleteFab.show();
                    addFab.show();
                    saveText.setVisibility(View.VISIBLE);
                    deleteText.setVisibility(View.VISIBLE);
                    addText.setVisibility(View.VISIBLE);


                } else {
                    saveFab.hide();
                    deleteFab.hide();
                    addFab.hide();
                    saveText.setVisibility(View.GONE);
                    deleteText.setVisibility(View.GONE);
                    addText.setVisibility(View.GONE);
                    menuFab.shrink();
                    isAllFABVisible=false;
                }
            }
        });

        //actually save button
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myAdapter.isEmpty()){
                    new AlertDialog.Builder(attendanceStart.this)
                            .setTitle("Warning!")
                            .setMessage("You cannot save empty list!")
                            .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    listSaved = true;
                    //date saver
                    setSavedDate();

                    saveData();
                    Intent intent =new Intent(attendanceStart.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(attendanceStart.this, "Saved!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //close FAB
                saveFab.hide();
                deleteFab.hide();
                addFab.hide();
                saveText.setVisibility(View.GONE);
                deleteText.setVisibility(View.GONE);
                addText.setVisibility(View.GONE);
                menuFab.shrink();

                //open dialog
                openDialog();
            }
        });
        //Save / Export ...

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        //NFC
        try {
            Intent intent = new Intent(this, getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //pending intent version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            } else{
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            //pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        saveData();
        //date saver
        setSavedDate();
        disableRead();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNFC(intent);
    }

    private void processNFC(Intent intent) {
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (messages != null) {
            for(Parcelable message : messages) {
                NdefMessage ndefMessage = (NdefMessage) message;
                for (NdefRecord record : ndefMessage.getRecords()) {
                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                        if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                            getDate();
                            String nfcData = new String(record.getPayload());
                            String nameOnly [] = nfcData.split("\n");
                            String fnlTagAdd = nameOnly[1]+"\n"+formattedDate+", "+status;

                            int index = -1;
                            for (int i = 0; i < names.size(); i++) {
                                String[] parts = names.get(i).split("\n");
                                if (parts[0].equals(nameOnly[1])) {
                                    index = i;
                                    String newLine = "\n" + formattedDate + ", " + status;
                                    boolean found = false;
                                    for (int j = 1; j < parts.length; j++) {
                                        String[] dateParts = parts[j].split(", ");
                                        if (dateParts[0].equals(formattedDate)) {
                                            Toast.makeText(attendanceStart.this, nameOnly[1]+" is recorded already!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    names.set(index, names.get(index) + newLine);
                                    myAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            if (index == -1) {
                                names.add(fnlTagAdd);
                                listCounter.setText(String.valueOf(myAdapter.getCount()));
                                myAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(this, "Tag was not written on this app!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
//NFC end*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] parts = names.get(position).split("\n");
                String name = parts[0];
                List<String> datesAndStatuses = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    datesAndStatuses.add(parts[i]);
                }
                Intent intent = new Intent(attendanceStart.this, DetailActivity.class);
                intent.putExtra("name", name);
                intent.putStringArrayListExtra("datesAndStatuses", (ArrayList<String>) datesAndStatuses);
                startActivity(intent);
            }
        });


        //Delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                String newItem [] = item.split("\n");
                new AlertDialog.Builder(attendanceStart.this)
                        .setTitle("Do you want to delete "+ newItem [0] +" from the list?")
                        .setPositiveButton("Yes",
                                (dialog, which) -> {
                                    names.remove(position);
                                    myAdapter.notifyDataSetChanged();
                                })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                return true;
            }
        });
    }
    //add Name function
    private void openDialog() {
        View view = getLayoutInflater().inflate(R.layout.attendance_layout_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student's Name");
        builder.setView(view);
        builder.setCancelable(false);

        Dialog dialog = builder.create();

        EditText fllName = view.findViewById(R.id.attendance_full_name);
        Button confirm = view.findViewById(R.id.confirm_add_btn);
        Button cancel = view.findViewById(R.id.cancel_add_btn);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
                String fnlText = fllName.getText().toString();
                String fnlText1 = "\"" + fnlText + "\"";
                String fnlNameCntnr = fnlText1+"\n"+formattedDate+", "+status;

                if (fllName.getText().toString().equals("")) {
                    Toast.makeText(attendanceStart.this, "Please fill up the Name!", Toast.LENGTH_SHORT).show();
                } else {
                    int index = -1;
                    for (int i = 0; i < names.size(); i++) {
                        String[] parts = names.get(i).split("\n");
                        if (parts[0].equals(fnlText1)) {
                            index = i;
                            String newLine = "\n" + formattedDate + ", " + status;
                            boolean found = false;
                            for (int j = 1; j < parts.length; j++) {
                                String[] dateParts = parts[j].split(", ");
                                if (dateParts[0].equals(formattedDate)) {
                                    Toast.makeText(attendanceStart.this, fnlText + " is recorded already!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            names.set(index, names.get(index) + newLine);
                            myAdapter.notifyDataSetChanged();
                            dialog.cancel();
                            return;
                        }
                    }
                    if (index == -1) {
                        names.add(fnlNameCntnr);
                        listCounter.setText(String.valueOf(myAdapter.getCount()));
                        myAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    //save & export
    public void processCSV(View view) {
        try {

            boolean writePermissionStatus = checkStoragePermission(false);
            //Check for permission
            if (!writePermissionStatus) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            } else {
                boolean writePermissionStatusAgain = checkStoragePermission(true);
                if (!writePermissionStatusAgain) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (myAdapter.isEmpty()) {
                        new AlertDialog.Builder(attendanceStart.this)
                                .setTitle("Warning!")
                                .setMessage("You cannot Export empty list!")
                                .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                                .create().show();
                    } else {
                        exportDataToCSV();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String toCSV(String[] array) {
        String result = "";
        if (array.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                sb.append(s.trim()).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }
    /*
    private void exportDataToCSV() throws IOException {
        String csvData = "";
        List<String> headers = new ArrayList<>();
        headers.add("Student Name");

        // Get saved dates as an array
        String savedDatesString = setSavedDate();
        String[] savedDatesArray = savedDatesString.split(",");

        headers.addAll(Arrays.asList(savedDatesArray));
        int numHeaders = headers.size();

        int numStudents = names.size();
        String[][] data = new String[numStudents][numHeaders];

        // Fill data with "Absent" by default
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numHeaders; j++) {
                data[i][j] = "Absent";
            }
        }

        for (int i = 0; i < numStudents; i++) {
            String currentLine = names.get(i);
            String[] cells = currentLine.split("\n");
            data[i][0] = cells[0];

            for (int k = 1; k < cells.length; k++) {
                String[] dateAndStatus = cells[k].split(",");
                String date = dateAndStatus[0];
                String status = dateAndStatus.length > 1 ? dateAndStatus[1] : "Present";

                for (int j = 1; j < numHeaders; j++) {
                    String header = headers.get(j);
                    if (header.equals(date)) {
                        data[i][j] = status;
                    }
                }
            }
        }

        StringBuilder headerBuilder = new StringBuilder(headers.get(0));
        for (int i = 1; i < numHeaders; i++) {
            headerBuilder.append(",").append(headers.get(i));
        }
        csvData += headerBuilder.toString() + "\n";

        for (int i = 0; i < numStudents; i++) {
            StringBuilder rowBuilder = new StringBuilder(data[i][0]);
            for (int j = 1; j < numHeaders; j++) {
                rowBuilder.append(",").append(data[i][j]);
            }
            csvData += rowBuilder.toString() + "\n";
        }

        // Add footer lines
        StringBuilder footerBuilder = new StringBuilder();
        footerBuilder.append("\n");
        footerBuilder.append("Course, Subject, Date Modified\n");

        footerBuilder.append(finalCourse);
        footerBuilder.append(",");
        footerBuilder.append(finalSubject);
        footerBuilder.append(",");
        footerBuilder.append(formattedDate);
        footerBuilder.append("\n");

        String footer = footerBuilder.toString();
        csvData += footer;

        File directory = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        TextView cysTextView1 = (TextView) findViewById(R.id.cysTextView);
        uniqueFileName = cysTextView1.getText().toString();
        File file = new File(directory, uniqueFileName + ".csv");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(csvData);

        fileWriter.flush();
        fileWriter.close();
        Toast.makeText(attendanceStart.this, "Saved! " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        //back to home
        Intent intent = new Intent(attendanceStart.this, MainActivity.class);
        startActivity(intent);
    }
     */
    private void exportDataToCSV() throws IOException {
        String csvData = "";
        List<String> headers = new ArrayList<>();
        headers.add("Student Name");

        // Get saved dates as an array
        String savedDatesString = setSavedDate();
        String[] savedDatesArray = savedDatesString.split(",");

        headers.addAll(Arrays.asList(savedDatesArray));
        int numHeaders = headers.size();

        int numStudents = names.size();
        String[][] data = new String[numStudents][numHeaders];

        // Fill data with "Absent" by default
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numHeaders; j++) {
                data[i][j] = "Absent";
            }
        }

        for (int i = 0; i < numStudents; i++) {
            String currentLine = names.get(i);
            String[] cells = currentLine.split("\n");
            data[i][0] = cells[0];

            for (int k = 1; k < cells.length; k++) {
                String[] dateAndStatus = cells[k].split(",");
                String date = dateAndStatus[0];
                String status = dateAndStatus.length > 1 ? dateAndStatus[1] : "Present";

                for (int j = 1; j < numHeaders; j++) {
                    String header = headers.get(j);
                    if (header.equals(date)) {
                        data[i][j] = status;
                        if (status.equals("Late")) {
                            data[i][j] = "Late";
                        }
                    }
                }
            }
        }

        StringBuilder headerBuilder = new StringBuilder(headers.get(0));
        for (int i = 1; i < numHeaders; i++) {
            headerBuilder.append(",").append(headers.get(i));
        }
        csvData += headerBuilder.toString() + "\n";

        for (int i = 0; i < numStudents; i++) {
            StringBuilder rowBuilder = new StringBuilder(data[i][0]);
            for (int j = 1; j < numHeaders; j++) {
                rowBuilder.append(",").append(data[i][j]);
            }
            csvData += rowBuilder.toString() + "\n";
        }

        // Add footer lines
        StringBuilder footerBuilder = new StringBuilder();
        footerBuilder.append("\n");
        footerBuilder.append("Course, Subject, Date Modified\n");

        footerBuilder.append(finalCourse);
        footerBuilder.append(",");
        footerBuilder.append(finalSubject);
        footerBuilder.append(",");
        footerBuilder.append(formattedDate);
        footerBuilder.append("\n");

        String footer = footerBuilder.toString();
        csvData += footer;

        File directory = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        TextView cysTextView1 = (TextView) findViewById(R.id.cysTextView);
        uniqueFileName = cysTextView1.getText().toString();
        File file = new File(directory, uniqueFileName + ".csv");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(csvData);

        fileWriter.flush();
        fileWriter.close();
        Toast.makeText(attendanceStart.this, "Saved! " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        //back to home
        Intent intent = new Intent(attendanceStart.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkStoragePermission(boolean showNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (showNotification) showNotificationAlertToAllowPermission();
                return false;
            }
        } else {
            return true;
        }
    }
    private void showNotificationAlertToAllowPermission() {
        new AlertDialog.Builder(this).setMessage("Please allow Storage Read/Write permission for this app to function properly.").setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).setNegativeButton("Cancel", null).show();
    }
    public String getDate(){
        //Save / Export ...
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy", Locale.getDefault());
        formattedDate = df.format(c);
        return null;
    }
    public String setSavedDate() {
        //save dates
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        savedDates = sharedPreferences.getStringSet(attendanceShared+"5", new HashSet<String>());
        savedDates.add(formattedDate);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(attendanceShared+"5", savedDates);
        editor.apply();

        // Construct a comma-separated string of all saved dates for this class
        StringBuilder datesString = new StringBuilder();
        for (String date : savedDates) {
            if (datesString.length() > 0) {
                datesString.append(",");
            }
            datesString.append(date);
        }

        // Show a Toast message indicating the saved dates for this class
        String message = "Saved dates for " + attendanceShared + ": " + datesString.toString();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Return the datesString
        return datesString.toString();
    }
    //show all the dates list
    public List<String> getSavedDates() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Set<String> savedDatesSet = sharedPreferences.getStringSet("dates", new HashSet<>());
        List<String> savedDatesList = new ArrayList<>(savedDatesSet);
        Collections.sort(savedDatesList); // Sort the dates in ascending order
        return savedDatesList;
    }
    //save data preferences
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //save variable
        editor.putString("text", attendanceShared);
        editor.apply();

        //arrayadapter to list
        Gson gson = new Gson();
        String json = gson.toJson(names);
        editor.putString(attendanceShared, json); //(Variable,file)
        editor.apply();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(attendanceShared, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        names = gson.fromJson(json, type);

        if (names == null) {
            names = new ArrayList<>();
        }
        myAdapter = new ArrayAdapter<String>(this, R.layout.activitylistcard, R.id.tagList, names);
    }
    @Override
    public void onBackPressed() {
        String message = "Are you sure you want to exit?";
        //boolean listSaved = false;  // Assume list is not saved

        if (!listSaved) {
            message = "Save the list before exiting?";
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //date saver
                            setSavedDate();
                            saveData();
                            attendanceStart.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            attendanceStart.this.finish();
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            attendanceStart.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}
