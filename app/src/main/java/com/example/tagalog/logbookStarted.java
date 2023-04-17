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
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class logbookStarted extends AppCompatActivity {

    //shared
    public static final String SHARED_PREFERENCES = "shared preferences";
    public static final String TEXT = "text";

    //list
    ListView listView;
    EditText addName;
    Button addButton, updateButton, saveButton;

    ArrayList<String> names = new ArrayList<>();
    ArrayAdapter myAdapter;
    Integer indexVal;
    String item;

    int position;
    boolean listSaved = false;


    //NFC
    private PendingIntent pendingIntent;
    private IntentFilter[] readFilters;

    public static String pic;
    //private TextView logbookTextView;

    //FAB
    FloatingActionButton saveFab, deleteFab, addFab;
    ExtendedFloatingActionButton menuFab;
    TextView saveText, deleteText, addText;

    Boolean isAllFABVisible;

    //Save/Export
    Object fileName1="";
    String finalOffice;
    Object finalOffice1="";
    String fileNameFinal;
    //String fileName1;
    public String officeName;
    public String p_i_c;
    public String date_date;
    public String isChecked;
    //time and date
    private String strTime;
    private String formattedDate;
    private String formattedTime;

    String fnlofficeName;
    String saveOfficeName;
    String uniqueFileName;

    private String fnlText;
    private TextView listCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_started);


        Intent iintent = getIntent();
        officeName = iintent.getStringExtra(logbookHome.EXTRA_TEXT3);
        date_date = iintent.getStringExtra(logbookHome.EXTRA_TEXT5);
        isChecked = iintent.getStringExtra(logbookHome.EXTRA_TEXT2);

        pic = p_i_c;
        TextView logbookTextView = (TextView) findViewById(R.id.cysTextView);
        listCounter = (TextView) findViewById(R.id.counter);

        getDate();
        if (officeName!=null) {
            logbookTextView.setText(officeName+" "+date_date);
            saveOfficeName = officeName+" "+date_date+"\n"+"Logbook";
            finalOffice = officeName;
            fileNameFinal = officeName+" Logbook "+formattedDate;
            //fileName1 = officeName+" Logbook "+formattedDate;
        } else {
            getDate();
            String office = iintent.getStringExtra("office");
            saveOfficeName = office+"\n"+"Logbook";
            logbookTextView.setText(office);
            finalOffice = office;
            fileNameFinal = finalOffice+" Logbook "+formattedDate;
            //fileName1 = office+" Logbook "+formattedDate;
        }

        listView = (ListView) findViewById(R.id.listView);

        Toast.makeText(this, "Tap your NFC Tag!", Toast.LENGTH_SHORT).show();

        loadData();
        //myAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_logbook, R.id.tagList, names);
        listView.setAdapter(myAdapter);
        //names.add(nfcData);
        //names.add("Jake");

        //FAB menu
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
        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapter.isEmpty()){
                    new AlertDialog.Builder(logbookStarted.this)
                            .setTitle("Warning!")
                            .setMessage("You cannot save empty list!")
                            .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    Toast.makeText(logbookStarted.this, "Saved!", Toast.LENGTH_SHORT).show();
                    listSaved = true;
                    saveData();
                    Intent intent = new Intent(logbookStarted.this, MainActivity.class);
                    startActivity(intent);
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

        //NFC
        //NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //if (!nfcAdapter.isEnabled())
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
                            String nfcData = new String(record.getPayload());
                            String nfcData1 [] = nfcData.split("\n");
                            getDate();
                            getTime();
                            fnlText = nfcData;
                            if (names.indexOf(nfcData+"\n"+formattedDate+"\n"+strTime) > -1){
                                Toast.makeText(logbookStarted.this, nfcData1[1]+", your Tag is recorded already!", Toast.LENGTH_SHORT).show();
                            }else {
                                if (isChecked==null) {
                                    openDialogPurpose();
                                } else if (isChecked.equals("-")){
                                    names.add(nfcData+"\n"+formattedDate+"\n"+strTime);
                                    listCounter.setText(String.valueOf(myAdapter.getCount()));
                                    myAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(this, "Error while reading Tags", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(this, "Tag was not written on this app!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        //Delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                String newItem [] = item.split("\n");
                new AlertDialog.Builder(logbookStarted.this)
                        .setTitle("Do you want to delete "+"\""+ newItem [1] +"\""+" from the list?")
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
    //add Name dialog
    private void openDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student's Data");
        builder.setView(view);
        builder.setCancelable(false);

        Dialog dialog = builder.create();

        EditText idText = view.findViewById(R.id.id_number_add);
        EditText nameText = view.findViewById(R.id.name_add);
        EditText ysText = view.findViewById(R.id.ys_add);
        EditText secText = view.findViewById(R.id.sec_add);
        EditText courseText = view.findViewById(R.id.course_add);
        Button confirm = view.findViewById(R.id.confirm_add_btn);
        Button cancel = view.findViewById(R.id.cancel_add_btn);



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sectionTextViewData = ysText.getText().toString()+"-"+secText.getText().toString();
                String finalTextName = idText.getText().toString()+"\n"+"\""+nameText.getText().toString()+"\""+"\n"+courseText.getText().toString()+"\n"+sectionTextViewData;

                //add name
                if (idText.getText().toString().equals("")) {
                    Toast.makeText(logbookStarted.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                }else if (nameText.getText().toString().equals("")) {
                    Toast.makeText(logbookStarted.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                } else if (courseText.getText().toString().equals("")) {
                    Toast.makeText(logbookStarted.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                } else if (ysText.getText().toString().equals("")) {
                    Toast.makeText(logbookStarted.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                } else if (secText.getText().toString().equals("")) {
                    Toast.makeText(logbookStarted.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                } else {
                    getDate();
                    getTime();
                    String fnlAddName = finalTextName+"\n"+formattedDate+"\n"+strTime;
                    String fnlName [] = fnlAddName.split("\n");


                    if (names.indexOf(fnlAddName) > -1){
                        //int index = names.indexOf(fnlAddName);
                        //names.set(index, names.get(index) + "\n" + strTime);
                        //myAdapter.notifyDataSetChanged();
                        Toast.makeText(logbookStarted.this, fnlName[1]+" is already added!", Toast.LENGTH_SHORT).show();
                    }else{
                        names.add(fnlAddName);
                        myAdapter.notifyDataSetChanged();
                        dialog.cancel();
                        //Toast.makeText(logbookStarted.this, "Position "+position, Toast.LENGTH_SHORT).show();
                        //position = position+1;
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
    //dialog of purpose
    private void openDialogPurpose() {
        View view = getLayoutInflater().inflate(R.layout.purpose_layout_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please state the purpose of your visit.");
        builder.setView(view);
        builder.setCancelable(false);

        Dialog dialog = builder.create();

        TextInputLayout purposeTxt = view.findViewById(R.id.text_input_purpose);
        Button confirm = view.findViewById(R.id.add_purpose);
        Button cancel = view.findViewById(R.id.cancel_purpose);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String purpose = purposeTxt.getEditText().getText().toString().trim();
                if (purpose.isEmpty()) {
                        purposeTxt.setError("Purpose can't be empty");
                }  else {
                        purposeTxt.setError(null);
                        //
                        String purposeData = purposeTxt.getEditText().getText().toString().trim();
                        names.add(fnlText+"\n"+purposeData+"\n"+formattedDate+"\n"+strTime);
                        listCounter.setText(String.valueOf(myAdapter.getCount()));
                        myAdapter.notifyDataSetChanged();
                        dialog.cancel();
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
                        new AlertDialog.Builder(logbookStarted.this)
                                .setTitle("Warning!")
                                .setMessage("You cannot Export empty list!")
                                .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                                .create().show();
                    } else {
                        //author
                        names.add("");
                        getDate();
                        names.add("Name of Office"+"\n"+"Date");
                        names.add(finalOffice+"\n"+formattedDate);
                        //Permission Granted. Export
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



    private void exportDataToCSV() throws IOException {


        String csvData = "";

        for (int i = 0; i < names.size(); i++) {

            String currentLIne = names.get(i);
            String[] cells = currentLIne.split("\n");
            csvData += toCSV(cells) + "\n";

        }

        File directory = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        TextView office = (TextView) findViewById(R.id.cysTextView);
        uniqueFileName =  office.getText().toString();
        File file = new File(directory, uniqueFileName+".csv");
            FileWriter fileWriter = new FileWriter(file);
            //headers
            if (isChecked==null){
                fileWriter.append("Student ID,Name,Course,Year & Section,Purpose,Date,Time-In");
            } else if (isChecked.equals("-")) {
                fileWriter.append("Student ID,Name,Course,Year & Section,Date,Time-In");
        }

            fileWriter.append("\n");
            //write start
            fileWriter.write(csvData);
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(logbookStarted.this, "Exported to C://Internal Storage/Documents/"+uniqueFileName+".csv", Toast.LENGTH_LONG).show();
            //back to home
            Intent intent = new Intent(logbookStarted.this,MainActivity.class);
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
    public void getTime(){
        //time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdFormat = new SimpleDateFormat("hh:mm a");
        strTime = mdFormat.format(calendar.getTime());
    }
    public void getDate(){
        //Save / Export ...
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
    }
    //save data preferences
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //save variable
        editor.putString("text", saveOfficeName);
        editor.apply();

        //arrayadapter to list
        Gson gson = new Gson();
        String json = gson.toJson(names);
        editor.putString(saveOfficeName, json); //where "officeName" was a variable holding data
        editor.apply();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(saveOfficeName, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        names = gson.fromJson(json, type);

        if (names == null) {
            names = new ArrayList<>();
        }
        myAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_logbook, R.id.tagList, names);
    }
    // for back saving
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
                            saveData();
                            logbookStarted.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            logbookStarted.this.finish();
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            logbookStarted.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}