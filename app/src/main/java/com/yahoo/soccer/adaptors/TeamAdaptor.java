package com.yahoo.soccer.adaptors;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.soccer.R;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.model.Team;

import java.util.List;

public class TeamAdaptor extends RecyclerView.Adapter<TeamAdaptor.MyViewHolder> {
    private Context context;
    private List<Team> mTeamList;

    public TeamAdaptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.teamlist_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdaptor.MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(mTeamList.get(i).getName());
        int won = mTeamList.get(i).getWin();
        int draw = mTeamList.get(i).getDraw();
        int loss = mTeamList.get(i).getLoss();
        float wpercent = (float) (won)/(won+draw+loss)*100;

        myViewHolder.won.setText(String.valueOf(won));
        myViewHolder.draw.setText(String.valueOf(draw));
        myViewHolder.loss.setText(String.valueOf(loss));
        myViewHolder.wpercent.setText(String.format("%.02f", wpercent));
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, won, loss, draw, wpercent;
        ImageView editImage;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            name = itemView.findViewById(R.id.team_name);
            won = itemView.findViewById(R.id.team_w);
            loss = itemView.findViewById(R.id.team_l);
            draw = itemView.findViewById(R.id.team_d);
            wpercent = itemView.findViewById(R.id.team_wpercent);

//            editImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //int elementId = mTeamList.get(getAdapterPosition()).getId();
//                    Intent i = new Intent(context, EditActivity.class);
//                    //i.putExtra(Constants.UPDATE_Team_Id, elementId);
//                    context.startActivity(i);
//                }
//            });
        }
    }
}
