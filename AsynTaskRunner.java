package com.example.yousefebrahimzadeh.tv_fernbedienung;


import android.os.AsyncTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class AsynTaskRunner extends AsyncTask<String, JSONObject, JSONObject> {

    JSONObject ChannelList;
    protected void onPreExecute(String instruction)
    {
        //Ist diese Activity diese Activity mit der gestartet wurde
    }
    protected JSONObject doInBackground(String...instructions)
    {
        HttpRequest httpRequest1 = new HttpRequest("172.16.203.149", "8080", 10000);
        try {

            ChannelList = httpRequest1.execute(instructions[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ChannelList;//try catch Internet is off?!
    }

    protected JSONObject onPostExecute(String instruction)
    {

        return ChannelList;
        //post Message. Is Internet on?!
    }
}
