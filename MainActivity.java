package com.example.yousefebrahimzadeh.tv_fernbedienung;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {


    private SeekBar myseekbar = null;

    public HashMap<String, AllChans> ChannelList = new HashMap<String, AllChans>();
    public ArrayList<String> data =  new ArrayList<String>();
    private String actualChan = "";

    String[] ChannelListKey = new String[23];
    int[] ChannelListFrequency = new int[23];
    int[] ChannelListQuality = new int[23];
    String[] ChannelListProvider = new String[23];
    String[] ChannelListChannelNumber = new String[23];

    private StoragePermission sPermission = new StoragePermission();;
    private String ip = "172.16.203.149";
    ListView lists;
    boolean writeOk = false;
    boolean readOk = false;
    HttpRequest httpRequest1 = new HttpRequest(ip,"8080", 4,true);



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            httpRequest1.execute("debug=1");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //IP-Adresse Eingabe

        // EditText myipAdresse = (EditText) findViewById(R.id.editText6);
        //  ip = myipAdresse.getText().toString();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPermission.askPermissionAndReadFile(MainActivity.this);
        sPermission.askPermissionAndWriteFile(MainActivity.this);
        writeOk = sPermission.getWritePermission();
        readOk = sPermission.getReadPermission();
        DataSingleton.getInstance().setWriteOk(writeOk);
        DataSingleton.getInstance().setReadOk(readOk);
        if (readOk) {
            File extStore = Environment.getExternalStorageDirectory();
            String path = extStore.getAbsolutePath() + "/" + "hashmap.ser";
            String path2 = extStore.getAbsolutePath() + "/" + "hashmap2.ser";
            String path3 = extStore.getAbsolutePath() + "/" + "hashmap3.ser";

            try {
                File myFile = new File(path);
                FileInputStream fIn = new FileInputStream(myFile);
                ObjectInputStream myIn = new ObjectInputStream(fIn);
                ChannelList = (HashMap) myIn.readObject();
                myIn.close();
                fIn.close();
                File myFile2 = new File(path2);
                FileInputStream fIn2 = new FileInputStream(myFile2);
                ObjectInputStream myIn2 = new ObjectInputStream(fIn2);
                data = (ArrayList) myIn2.readObject();
                myIn2.close();
                fIn2.close();
                /*File myFile3 = new File(path3);
                FileInputStream fIn3 = new FileInputStream(myFile3);
                ObjectInputStream myIn3 = new ObjectInputStream(fIn3);
                soundStatus = myIn3.readInt();
                myIn3.close();
                fIn3.close();*/
                Toast.makeText(getApplicationContext(), "show", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "not show", Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            DataSingleton.getInstance().setHashMap(ChannelList);
            DataSingleton.getInstance().setArrayList(data);
            //liste2.setAdapter(new kanalActivity.MyListAdaper(kanalActivity.this, R.layout.list_items, data));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Volumen
        SeekBar myseekbar = (SeekBar) findViewById(R.id.Lautstaerke);
        final TextView txt3 = (TextView) findViewById(R.id.textView2);
        myseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String myVolume = "volume=" + Integer.toString(progress);
                try {
                    httpRequest1.execute(myVolume);
                    txt3.setText(myVolume + "");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Mute Button
        ToggleButton btn = (ToggleButton) findViewById(R.id.stummButton);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    try {
                        httpRequest1.execute("volume=0");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {


                }
            }
        });


        // 4x3 Button
        Button my4x3Button = (Button) findViewById(R.id.Button4x3);
        my4x3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    httpRequest1.execute("zoomMain=1");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // Cine Button
        Button CineButton = (Button) findViewById(R.id.buttonCine);
        CineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    httpRequest1.execute("zoomMain=1");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // 16x9 Button
        Button my16x9Button = (Button) findViewById(R.id.Button16x9);
        my16x9Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    httpRequest1.execute("zoomMain=0");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        //Pause - Play Button
        ToggleButton myPauseButton = (ToggleButton) findViewById(R.id.pauseButton);
        myPauseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    try {
                        httpRequest1.execute("timeShiftPlay=17");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        httpRequest1.execute("timeShiftPause=");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


        //PiP Button

        ToggleButton myPiPButton = (ToggleButton) findViewById(R.id.pipButton);
        myPiPButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String instructionPip = "";
                if (isChecked) {
                    AsynTaskRunner runner = new AsynTaskRunner();
                    runner.execute("showPip=1");

                } else {
                    AsynTaskRunner runner = new AsynTaskRunner();
                    runner.execute("showPip=0");
                    AsynTaskRunner runner2 = new AsynTaskRunner();
                    runner2.execute(instructionPip);


                }
            }
        });


        // Kanalerhöhen
        Button myNextChanel = (Button) findViewById(R.id.nextChannel);
        myNextChanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actualChan = "";
                String instruction = "";
                String nextChanS = "";
                data = DataSingleton.getInstance().getArrayList();
                ChannelList = DataSingleton.getInstance().getHashMap();
                actualChan = DataSingleton.getInstance().getActual();
                boolean nextChan = false;
                //actualChan = getC.getText().toString();

                for (int i = 0; i < 23; i++) {
                    if (nextChan) {
                        for (HashMap.Entry<String, AllChans> entry : ChannelList.entrySet()) {
                            if (entry.getKey().equals(data.get(i))) {
                                instruction = "channelMain=" + entry.getValue().channelNumber;
                                nextChanS = entry.getKey();
                            }
                        }
                        AsynTaskRunner runner = new AsynTaskRunner();
                        runner.execute(instruction);
                        //getC.setText(nextChanS);
                        nextChan = false;
                    }
                    if (actualChan.equals(data.get(i))) {
                        if(data.get(22).equals(actualChan))
                        {
                            for (HashMap.Entry<String, AllChans> entry : ChannelList.entrySet()) {
                                    if (entry.getKey().equals(data.get(0))) {
                                        instruction = "channelMain=" + entry.getValue().channelNumber;
                                        nextChanS = entry.getKey();
                                        AsynTaskRunner runner = new AsynTaskRunner();
                                        runner.execute(instruction);
                                        break;
                                    }
                                }
                        }
                        nextChan = true;
                    }

                }
                actualChan = nextChanS;
                DataSingleton.getInstance().setActual(actualChan);
            }
        });


        //kanalverringern preChannel
        Button mypreChannel = (Button) findViewById(R.id.preChannel);
        mypreChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actualChan = "";
                String backChan = "";
                String instruction = "";
                actualChan = DataSingleton.getInstance().getActual();
                //actualChan = getC.getText().toString();
                for (int i = 0; i < 23; i++) {
                    if (actualChan.equals(data.get(i))) {
                        if(i == 0)
                        {
                            for (HashMap.Entry<String, AllChans> entry : ChannelList.entrySet()) {
                                if (entry.getKey().equals(data.get(22))) {
                                    instruction = "channelMain=" + entry.getValue().channelNumber;
                                    backChan = entry.getKey();
                                    AsynTaskRunner runner = new AsynTaskRunner();
                                    runner.execute(instruction);
                                    break;
                                }
                            }
                        }
                        AsynTaskRunner runner = new AsynTaskRunner();
                        runner.execute(instruction);
                        actualChan = backChan;
                        //getC.setText(backChan);
                    } else {
                        for (HashMap.Entry<String, AllChans> entry : ChannelList.entrySet()) {
                            if (entry.getKey().equals(data.get(i))) {
                                instruction = "channelMain=" + entry.getValue().channelNumber;
                                backChan = entry.getKey();
                            }
                        }
                    }
                    DataSingleton.getInstance().setActual(actualChan);

                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.kanale) {
            // Handle the camera action
            openKanalAcitivity();

        } else if (id == R.id.startseite) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openKanalAcitivity(){

        Intent intent = new Intent (this, kanalActivity.class);
        startActivity(intent);
    }

    //App umdrehen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int counter = 0;
        for (HashMap.Entry<String, AllChans> entry : ChannelList.entrySet()) {
            ChannelListKey[counter] = entry.getKey();
            ChannelListQuality[counter] = entry.getValue().quality;
            ChannelListFrequency[counter] = entry.getValue().frequency;
            ChannelListProvider[counter] = entry.getValue().provider;
            ChannelListChannelNumber[counter] = entry.getValue().channelNumber;
            counter++;
        }
        outState.putStringArrayList("DataStore", data);
        outState.putString("ChanTxtStore", actualChan);
        outState.putStringArray("ChannelListKey", ChannelListKey);
        outState.putStringArray("ChannelListChannnelNumber", ChannelListChannelNumber);
        outState.putStringArray("ChannelListProvider", ChannelListProvider);
        outState.putIntArray("ChannelListFrequency", ChannelListFrequency);
        outState.putIntArray("ChannelListQuality", ChannelListQuality);
    }
    @Override
    protected void onPause() {
        super.onPause();
        File extStore = Environment.getExternalStorageDirectory();
        final String path = extStore.getAbsolutePath() + "/" + "hashmap.ser";
        final String path2 = extStore.getAbsolutePath() + "/" + "hashmap2.ser";
        final String path3 = extStore.getAbsolutePath() + "/" + "hashmap3.ser";

        if(writeOk) {
            try {
                ChannelList = DataSingleton.getInstance().getHashMap();
                data = DataSingleton.getInstance().getArrayList();
                //ChannelList
                File myFile = new File(path);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                ObjectOutputStream myOut = new ObjectOutputStream(fOut);
                myOut.writeObject(ChannelList);
                myOut.close();
                fOut.close();
                //data
                File myFile2 = new File(path2);
                myFile2.createNewFile();
                FileOutputStream fOut2 = new FileOutputStream(myFile2);
                ObjectOutputStream myOut2 = new ObjectOutputStream((fOut2));
                myOut2.writeObject(data);
                myOut2.close();
                fOut2.close();
                //Soundstatus
               /* File myFile3 = new File(path3);
                myFile3.createNewFile();
                FileOutputStream fOut3 = new FileOutputStream(myFile3);
                ObjectOutputStream myOut3 = new ObjectOutputStream((fOut3));
                myOut3.writeInt(soundStatus);
                myOut3.close();
                fOut3.close();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
