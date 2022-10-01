package com.yahoo.soccer.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Game;
import com.yahoo.soccer.model.Team;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class JsonTask extends AsyncTask<String, String, String> {
    ProgressDialog pd;
    private AppDatabase mDb;
    Set<String> teamsToDo;
    HashMap<String, Team> teamScores;
    boolean newGamesAdded = false;
    MutableLiveData<Integer> sortingConstant;

    JsonTask(MainActivity context){
        teamScores = new HashMap<>();
        teamsToDo = new HashSet<>();
        mDb = AppDatabase.getInstance(context.getApplicationContext());
        pd = new ProgressDialog(context);
        sortingConstant = context.getSortLiveData();
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

            parseAllRecords(buffer);

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

    private void updateTeamEntity() {
        for(String id:teamsToDo) {
            final Team team = new Team(id, teamScores.get(id).getName(), teamScores.get(id).getWin(), teamScores.get(id).getLoss(), teamScores.get(id).getDraw());

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if(mDb.teamDao().isRowIsExist(team.getId())){
                        mDb.teamDao().updateTeam(team);
                    }
                    else {
                        mDb.teamDao().insertTeam(team);
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void parseAllRecords(StringBuffer jsonStr) throws JSONException {

        JSONArray jsonarray= new JSONArray(jsonStr.toString());
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            String id = jsonobject.getString("GameId");
            String aname = jsonobject.getString("AwayTeamName");
            String aid = jsonobject.getString("AwayTeamId");
            String ascore = jsonobject.getString("AwayScore");
            String bname = jsonobject.getString("HomeTeamName");
            String bid = jsonobject.getString("HomeTeamId");
            String bscore = jsonobject.getString("HomeScore");
            Log.d("teamScore: ",aname+" "+ascore);

            setTeamHash(aname,aid,bname,bid,Integer.parseInt(ascore) - Integer.parseInt(bscore));

            if(!mDb.gameDao().isRowIsExist(id)) {
                saveGameRecord(id, aid, bid, ascore, bscore);
                newGamesAdded = true;
            }
        }

        if(newGamesAdded){
            updateTeamEntity();
        }

    }

    //whoWon = 0 - draw, >0 - a, <0 - b
    private void setTeamHash(String aname, String aid, String bname, String bid, int whoWon) {
        if(!teamScores.containsKey(aid)){
            teamScores.put(aid, new Team(aid,aname,0,0,0));
        }
        if(!teamScores.containsKey(bid)) {
            teamScores.put(bid, new Team(bid,bname,0,0,0));
        }

        if(whoWon==0) {
            teamScores.get(aid).setDraw(teamScores.get(aid).getDraw()+1);
            teamScores.get(bid).setDraw(teamScores.get(bid).getDraw()+1);
        }
        else if(whoWon>0) {
            teamScores.get(aid).setWin(teamScores.get(aid).getWin()+1);
            teamScores.get(bid).setLoss(teamScores.get(bid).getLoss()+1);
        }
        else {
            teamScores.get(aid).setLoss(teamScores.get(aid).getLoss()+1);
            teamScores.get(bid).setWin(teamScores.get(bid).getWin()+1);
        }
    }

    public void saveGameRecord(String id, String aid, String bid, String ascore, String bscore) {
        teamsToDo.add(aid);
        teamsToDo.add(bid);
        final Game game = new Game(id,
                aid,
                bid,
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
        sortingConstant.setValue(Constants.nsort_up);
    }
}