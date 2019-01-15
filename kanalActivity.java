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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.concurrent.ExecutionException;


class AllChans implements Serializable {
    public int frequency = 0;
    public int quality = 0;
    public String provider = "";
    public String channelNumber = "";

};

public class kanalActivity extends AppCompatActivity implements Serializable{

    private Button myZuruckButton;
    private Button searchChan;
    private ListView liste2 = null;

    public HashMap<String, AllChans> ChannelList = new HashMap<String, AllChans>();
    public ArrayList<String> data =  new ArrayList<String>();
    String actualChan = "";

    String[] ChannelListKey = new String[23];
    int[] ChannelListFrequency = new int[23];
    int[] ChannelListQuality = new int[23];
    String[] ChannelListProvider = new String[23];
    String[] ChannelListChannelNumber = new String[23];
    boolean writeOk = false;
    boolean readOk = false;
    StoragePermission sPermission = null;

    HttpRequest httpRequest2 = new HttpRequest("172.16.203.149","8080", 4,true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanal);

        writeOk = DataSingleton.getInstance().getWriteOk();
        readOk = DataSingleton.getInstance().getReadok();
        myZuruckButton =  (Button)findViewById(R.id.zuruckButton);
        searchChan = (Button) findViewById(R.id.scanButton);

        liste2 = (ListView) findViewById(R.id.liste);
        liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
        sPermission = new StoragePermission();
        myZuruckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartAcitivity();
            }

        });

        if(savedInstanceState != null)
        {
            ChannelList.clear();
            data.clear();
            data = savedInstanceState.getStringArrayList("DataStore");
            actualChan = savedInstanceState.getString("ChanTxtStore");
            ChannelListKey = savedInstanceState.getStringArray("ChannelListKey");
            ChannelListChannelNumber = savedInstanceState.getStringArray("ChannelListChannnelNumber");
            ChannelListProvider = savedInstanceState.getStringArray("ChannelListProvider");
            ChannelListFrequency = savedInstanceState.getIntArray("ChannelListFrequency");
            ChannelListQuality = savedInstanceState.getIntArray("ChannelListQuality");
            for (int i = 0; i < 23; i++)
            {
                AllChans tmpFin = new AllChans();
                tmpFin.quality = ChannelListQuality[i];
                tmpFin.provider = ChannelListProvider[i];
                tmpFin.channelNumber = ChannelListChannelNumber[i];
                tmpFin.frequency = ChannelListFrequency[i];
                ChannelList.put(ChannelListKey[i], tmpFin);
            }

            liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));

        }
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
                File myFile3 = new File(path3);
                /*FileInputStream fIn3 = new FileInputStream(myFile3);
                ObjectInputStream myIn3 = new ObjectInputStream(fIn3);
                soundStatus = myIn3.readInt();
                myIn3.close();
                fIn3.close();*/
                //Toast.makeText(getApplicationContext(), "show", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
               // Toast.makeText(getApplicationContext(), "not show", Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            DataSingleton.getInstance().setHashMap(ChannelList);
            DataSingleton.getInstance().setArrayList(data);
            liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
        }
        liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
        //sound.setProgress(soundStatus);
        //soundStatusS = "volume=" + soundStatus;
        //AsynTaskRunner runner = new AsynTaskRunner();
        //runner.execute(soundStatusS);
        searchChan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject TVReply = new JSONObject();
                int counter = 0;
                try {
                    TVReply = new AsynTaskRunner().execute("scanChannels=").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray channels = TVReply.getJSONArray("channels");
                    for (int i = 0; i < channels.length(); i++) {
                        AllChans tmpFin = new AllChans();
                        JSONObject tmpChan = channels.getJSONObject(i);
                        String key = tmpChan.getString("program");

                        boolean existsAlready = false;
                        for(int j = 0; j < ChannelList.size(); j++){
                            if(ChannelList.containsKey(key))
                                existsAlready = true;
                        }

                        if(existsAlready == false){
                            tmpFin.frequency = tmpChan.getInt("frequency");
                            tmpFin.quality = tmpChan.getInt("quality");
                            tmpFin.provider = tmpChan.getString("provider");
                            tmpFin.channelNumber = tmpChan.getString("channel");
                            ChannelList.put(key, tmpFin);

                            data.add(key);
                        }
                        else {
                            if(ChannelList.get(key).quality < tmpFin.quality){
                                ChannelList.put(key, tmpFin);
                            }
                        }
                    }
                    liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(kanalActivity.this, "Kanäle werden gesucht...", Toast.LENGTH_SHORT).show();
                DataSingleton.getInstance().setArrayList(data);
                DataSingleton.getInstance().setHashMap(ChannelList);
                DataSingleton.getInstance().setList(liste2);
            }
        });



    }

    public void openStartAcitivity(){

        Intent intent = new Intent (this, MainActivity.class);
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
               // Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//adapter
public class MyListAdaper extends ArrayAdapter<String> implements Serializable{
        int layout;
    private List<String> mObjects;

    private MyListAdaper(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mObjects = objects;
        layout = resource;
    }

    ViewHolder mainViewholder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            //Views...
            viewHolder.chanName = (Button) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);

            viewHolder.chaneButton =(Button) convertView.findViewById(R.id.name3);
            convertView.setTag(viewHolder);

            viewHolder.ChanButtonverringen =(Button) convertView.findViewById(R.id.name4);
            convertView.setTag(viewHolder);

            viewHolder.PiPButton =(Button) convertView.findViewById(R.id.pipB);
            convertView.setTag(viewHolder);



        }
        mainViewholder = (ViewHolder) convertView.getTag();
        mainViewholder.chanName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelNumberTmp = "";
                String instruction = "";
                for(HashMap.Entry<String, AllChans> entry : ChannelList.entrySet())
                {
                    channelNumberTmp = entry.getValue().channelNumber;
                    if(data.get(position).equals(entry.getKey()))
                    {
                        AsynTaskRunner runner = new AsynTaskRunner();
                        instruction = "channelMain=" + channelNumberTmp;
                        runner.execute(instruction);
                        actualChan = data.get(position);
                        DataSingleton.getInstance().setActual(actualChan);
                    }
                }
            }
        });

        //Kanäle erhöhen
        mainViewholder = (ViewHolder)convertView.getTag();
        mainViewholder.chaneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionMap = 0;
                if(position != 0)
                {
                    String tmp = data.get(position);
                    data.set(position, (data.get(position - 1)));
                    data.set(position - 1, tmp);

                    liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
                }


            }
        });

        //Kanäle Verringen
        mainViewholder = (ViewHolder)convertView.getTag();
        mainViewholder.ChanButtonverringen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != 0)
                {
                    String tmp = data.get(position);
                    data.set(position, (data.get(position + 1)));
                    data.set(position + 1 , tmp);
                    liste2.setAdapter(new MyListAdaper(kanalActivity.this, R.layout.list_items, data));
                }
            }
        });




        /*mainViewholder.list_imageBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelNumberTmp = "";
                for(HashMap.Entry<String, AllChans> entry : ChannelList.entrySet())
                {
                    channelNumberTmp = entry.getValue().channelNumber;
                    if(data.get(position) == entry.getKey())
                    {
                        AsynTaskRunner runner = new AsynTaskRunner();
                        instruction = "channelMain=" + channelNumberTmp;
                        //getC.setText(entry.getKey());
                        runner.execute(instruction);
                    }
                }
            }
        });*/



        //PiP
        mainViewholder = (ViewHolder)convertView.getTag();
        mainViewholder.PiPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelNumberTmp = "";
                String instructionPip = "";
                for(HashMap.Entry<String, AllChans> entry : ChannelList.entrySet())
                {
                    channelNumberTmp = entry.getValue().channelNumber;
                    if(data.get(position).equals(entry.getKey()))
                    {
                        AsynTaskRunner runner = new AsynTaskRunner();
                        instructionPip = "channelPip=" + channelNumberTmp;
                       // getC.setText(entry.getKey());
                        runner.execute(instructionPip);
                        instructionPip = "channelMain="+ channelNumberTmp;
                    }
                }
            }
        });

       // mainViewholder.list_chanName.setText(getItem(position));*/
        mainViewholder.chanName.setText(data.get(position));
        return convertView;
    }

    public class ViewHolder {

        Button chanName;
        Button chaneButton;
        Button ChanButtonverringen;
        Button PiPButton;

    }
}
}



