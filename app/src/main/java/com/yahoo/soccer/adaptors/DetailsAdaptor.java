package com.yahoo.soccer.adaptors;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.yahoo.soccer.R;
import com.yahoo.soccer.activities.DetailsPage;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.model.Game;
import com.yahoo.soccer.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DetailsAdaptor extends RecyclerView.Adapter<DetailsAdaptor.MyViewHolder> {
    private Context context;
    private List<Team> mTeamList;

    public DetailsAdaptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.teamlist_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsAdaptor.MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(mTeamList.get(i).getName());
        int won = mTeamList.get(i).getWin();
        int draw = mTeamList.get(i).getDraw();
        int loss = mTeamList.get(i).getLoss();
        int total = won+draw+loss;
        final int idx = i;

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String elementId = mTeamList.get(idx).getId();
                Intent i = new Intent(context, DetailsPage.class);
                i.putExtra(Constants.UPDATE_Team_Id, elementId);
                context.startActivity(i);
            }
        });

        myViewHolder.won.setText(String.valueOf(won));
        myViewHolder.draw.setText(String.valueOf(draw));
        myViewHolder.loss.setText(String.valueOf(loss));
        myViewHolder.total.setText(String.valueOf(total));
    }

    @Override
    public int getItemCount() {
        if (mTeamList == null) {
            return 0;
        }
        return mTeamList.size();

    }

    public void setTasks(List<Team> teamList) {
        mTeamList = teamList;
        notifyDataSetChanged();
    }

    public List<Team> getTasks() {

        return mTeamList;
    }

    public List<Team> getTeamsAgainst(String mTeamId, AppDatabase mDb) {
        List<Game> allGames = mDb.gameDao().loadGamesWithTeamId(mTeamId);
        HashMap<String, Team> teamScores = new HashMap<>();

        for(Game g:allGames){
            String aname = mDb.teamDao().loadTeamById(g.getAid()).getName();
            String bname = mDb.teamDao().loadTeamById(g.getBid()).getName();
            setTeamHash(aname, g.getAid(), bname, g.getBid(), Integer.parseInt(g.getAscore()) - Integer.parseInt(g.getBscore()), teamScores);
        }

        teamScores.remove(mTeamId);

        List<Team> teams = new ArrayList<>(teamScores.values());
        Collections.sort(teams, new Comparator<Team>() {
            @Override
            public int compare(Team team, Team t1) {
                return team.getName().compareTo(t1.getName());
            }
        });

        return  teams;
    }

    private void setTeamHash(String aname, String aid, String bname, String bid, int whoWon, HashMap<String, Team> teamScores) {
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, won, loss, draw, total;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            name = itemView.findViewById(R.id.team_name);
            won = itemView.findViewById(R.id.team_w);
            loss = itemView.findViewById(R.id.team_l);
            draw = itemView.findViewById(R.id.team_d);
            total = itemView.findViewById(R.id.team_wpercent);
        }
    }
}
