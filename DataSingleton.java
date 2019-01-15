package com.example.yousefebrahimzadeh.tv_fernbedienung;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class DataSingleton {
    private static final DataSingleton ourInstance = new DataSingleton();

    private ArrayList<String> arrayList = new ArrayList<String>();
    private HashMap<String, AllChans> hashMap = new HashMap<String, AllChans>();
    private String actual = "";
    private ListView list;
    private boolean writeOk = false;
    private boolean readOk = false;

    public void setReadOk(boolean readOk) {
        this.readOk = readOk;
    }

    public void setWriteOk(boolean write)
    {
        this.writeOk = write;
    }

    public void setList(ListView list) {
        this.list = list;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public void setHashMap(HashMap<String, AllChans> hashMap) {
        this.hashMap = hashMap;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public boolean getReadok()
    {
        return readOk;
    }

     public boolean getWriteOk()
     {
         return writeOk;
     }

    public ListView getList() {
        return list;
    }

    public String getActual() {
        return actual;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public HashMap<String, AllChans> getHashMap() {
        return hashMap;
    }

    public static DataSingleton getInstance() {
        return ourInstance;
    }

    private DataSingleton() {

    }
}
