package com.yahoo.soccer.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class JsonTask extends AsyncTask<String, String, String> {
    ProgressDialog pd;
    private AppDatabase mDb;

    JsonTask(Context context){
        mDb = AppDatabase.getInstance(context.getApplicationContext());
        pd = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();

        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }

            parseRecord(buffer);

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void parseRecord(StringBuffer jsonStr) throws JSONException {

        JSONArray jsonarray= new JSONArray(jsonStr.toString());
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String id = jsonobject.getString("GameId");
            String aname = jsonobject.getString("AwayTeamName");
            String ascore = jsonobject.getString("AwayScore");
            String bname = jsonobject.getString("HomeTeamName");
            String bscore = jsonobject.getString("HomeScore");
            Log.d("teamScore: ",aname+" "+ascore);

            if(!mDb.gameDao().isRowIsExist(id))
                saveGameRecord(id,aname,bname,ascore,bscore);
        }
    }

    public void saveGameRecord(String id, String aname, String bname, String ascore, String bscore) {
        final Game game = new Game(id,
                aname,
                bname,
                bscore,
                ascore);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
               mDb.gameDao().insertGame(game);
            }
        });
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pd.isShowing()){
            pd.dismiss();
        }
    }
}