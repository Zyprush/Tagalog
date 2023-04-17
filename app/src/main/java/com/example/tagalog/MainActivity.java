package com.example.tagalog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //floating
    ExtendedFloatingActionButton attendanceBtn, logbookBtn, writeBtn, readBtn;
    FloatingActionButton base;
    private boolean isAllFABVisible;

    //listView
    ListView listView;
    ArrayList<String> names = new ArrayList<String>();
    String dateText;
    ArrayAdapter myAdapter;
    private String formattedDate;
    Integer position;
    Integer indexVal;
    String item;
    //String newItem;
    TextView tagIsEmpty;

    String sharedVariable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check if the device is available
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        tagIsEmpty = findViewById(R.id.listEmpty);

        if(nfcAdapter==null){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Warning!")
                    .setMessage("NFC is not available on this device!")
                    .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                    .create().show();
        } else if (!nfcAdapter.isEnabled()) {
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            } else {
                Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            }
            startActivity(intent);
        }

        //ListView

        listView = (ListView) findViewById(R.id.saved_data);
        loadData();
        getDate();
        if (!sharedVariable.equals("")) {
            if (names.indexOf(sharedVariable) > -1) {
                Toast.makeText(MainActivity.this, "Load successfully!", Toast.LENGTH_SHORT).show();
                indexVal = names.indexOf(sharedVariable);
                names.set(indexVal, sharedVariable);
                //names.remove(combined);
            } else {
                names.add(sharedVariable);//+"\n"+formattedDate
                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                sharedPreferences.edit().remove("text").commit();
            }
        }
        saveData();
        //myAdapter = new ArrayAdapter<String>(this, R.layout.listview_layout_cardview, R.id.tagList, names);
        listView.setAdapter(myAdapter);

        //select item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                String newItem [] = item.split("\n");
                indexVal = position;
                //Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
                //startActivity(attendance);
                Intent i;
                if (newItem[1].equals("Logbook")) {
                    i = new Intent(MainActivity.this, logbookStarted.class);
                    i.putExtra("office",newItem[0]);
                    startActivity(i);
                }  else if (newItem[0].equals("Sample List")) {
                    Toast.makeText(MainActivity.this, "This is Sample list!", Toast.LENGTH_SHORT).show();
                } else {
                    i = new Intent(MainActivity.this, attendanceStart.class);
                    i.putExtra("subject", newItem[0]);
                    i.putExtra("cys",newItem[1]);
                    startActivity(i);
                }
            }
        });
                //Delete
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        item = parent.getItemAtPosition(position).toString();
                        String newItem [] = item.split("\n");
                        String sharedData = newItem[0]+"\n"+newItem[1];
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Do you want to delete "+"\""+ newItem [0] +"\""+" from the list?")
                                .setPositiveButton("Yes",
                                        (dialog, which) -> {
                                            //add
                                            names.remove(position);
                                            myAdapter.notifyDataSetChanged();
                                            saveData();
                                            loadData();
                                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                            sharedPreferences.edit().remove(sharedData).commit();
                                        })
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                        return true;
                    }
                });


        //FAB menu
        base = findViewById(R.id.menu_base);
        attendanceBtn = findViewById(R.id.attendance_btn);
        logbookBtn = findViewById(R.id.logbook_btn);
        writeBtn = findViewById(R.id.write_btn);
        readBtn = findViewById(R.id.read_btn);

        attendanceBtn.shrink();
        attendanceBtn.setVisibility(View.GONE);
        logbookBtn.shrink();
        logbookBtn.setVisibility(View.GONE);
        writeBtn.shrink();
        writeBtn.setVisibility(View.GONE);
        readBtn.shrink();
        readBtn.setVisibility(View.GONE);

        isAllFABVisible=false;



        base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllFABVisible){
                    isAllFABVisible=true;
                    attendanceBtn.show();
                    logbookBtn.show();
                    writeBtn.show();
                    readBtn.show();
                } else {
                    attendanceBtn.hide();
                    logbookBtn.hide();
                    writeBtn.hide();
                    readBtn.hide();
                    attendanceBtn.shrink();
                    logbookBtn.shrink();
                    writeBtn.shrink();
                    readBtn.shrink();
                    isAllFABVisible=false;
                }
            }
        });
        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attendanceBtn.isExtended()) {
                    Intent intent = new Intent(MainActivity.this,newClassHome.class);
                    startActivity(intent);
                    //Toast.makeText(savedListHome.this, "Attendance is clicked!", Toast.LENGTH_SHORT).show();
                } else {
                    attendanceBtn.extend();
                    logbookBtn.shrink();
                    writeBtn.shrink();
                    readBtn.shrink();
                }
            }
        });
        logbookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logbookBtn.isExtended()) {
                    Intent intent = new Intent(MainActivity.this,logbookHome.class);
                    startActivity(intent);
                    //Toast.makeText(savedListHome.this, "Logbook is clicked!", Toast.LENGTH_SHORT).show();
                } else {
                    attendanceBtn.shrink();
                    logbookBtn.extend();
                    writeBtn.shrink();
                    readBtn.shrink();
                }
            }
        });
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeBtn.isExtended()) {

                    if (nfcAdapter==null) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Warning!")
                                .setMessage("NFC is not available on this device!")
                                .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                                .create().show();
                    } else if (!nfcAdapter.isEnabled()) {
                        Intent intent = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        } else {
                            Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        }
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this,new_write.class);
                        startActivity(intent);
                        //Toast.makeText(savedListHome.this, "Write is clicked!", Toast.LENGTH_SHORT).show();
                    }
                    //Intent intent = new Intent(MainActivity.this,new_write.class);
                    //startActivity(intent);
                } else {
                    attendanceBtn.shrink();
                    logbookBtn.shrink();
                    writeBtn.extend();
                    readBtn.shrink();
                }
            }
        });
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readBtn.isExtended()) {
                    if (nfcAdapter==null) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Warning!")
                                .setMessage("NFC is not available on this device!")
                                .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                                .create().show();
                    } else if (!nfcAdapter.isEnabled()) {
                        Intent intent = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        } else {
                            Intent intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        }
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this,readHome.class);
                        startActivity(intent);
                        //Toast.makeText(savedListHome.this, "Read is clicked!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    attendanceBtn.shrink();
                    logbookBtn.shrink();
                    writeBtn.shrink();
                    readBtn.extend();
                }
            }
        });
    }
    public void getDate(){
        //Save / Export ...
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
    }
    public void homeClicked () {
        attendanceBtn.hide();
        logbookBtn.hide();
        writeBtn.hide();
        readBtn.hide();
        attendanceBtn.shrink();
        logbookBtn.shrink();
        writeBtn.shrink();
        readBtn.shrink();
        isAllFABVisible=false;
    }
    //save data preferences
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //save variable
        //editor.putString("text", attendanceShared);

        //arrayadapter to list
        Gson gson = new Gson();
        String json = gson.toJson(names);
        editor.putString("mainList", json); //(Variable,file)
        editor.apply();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        //variable collect
        sharedVariable = sharedPreferences.getString("text", "");

        Gson gson = new Gson();
        String json = sharedPreferences.getString("mainList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        names = gson.fromJson(json, type);

        if (names == null) {
            getDate();
            names = new ArrayList<>();
            tagIsEmpty.setText("List is Empty*");
            //names.add("Sample List"+"\n"+formattedDate);
        }
        myAdapter = new ArrayAdapter<String>(this, R.layout.listview_layout_cardview, R.id.tagList, names);
    }
    public void aboutBtn(MenuItem item){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("About")
                .setMessage("TAG-A-LOG is an Android Application for Attendance and Logbook that uses NFC Technology.\n")
                .setPositiveButton("Okay",(dialog, which) -> dialog.dismiss())
                .create().show();
    }
}