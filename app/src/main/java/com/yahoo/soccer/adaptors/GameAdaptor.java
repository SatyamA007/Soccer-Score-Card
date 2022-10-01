package com.yahoo.soccer.adaptors;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.soccer.R;
import com.yahoo.soccer.activities.EditActivity;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.model.Game;

import java.util.List;

public class GameAdaptor extends RecyclerView.Adapter<GameAdaptor.MyViewHolder> {
    private Context context;
    private List<Game> mGameList;

    public GameAdaptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdaptor.MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(mGameList.get(i).getAname());
        myViewHolder.email.setText(mGameList.get(i).getBname());
        myViewHolder.number.setText(mGameList.get(i).getAscore());
        myViewHolder.pincode.setText(mGameList.get(i).getBscore());
    }

    @Override
    public int getItemCount() {
        if (mGameList == null) {
            return 0;
        }
        return mGameList.size();

    }

    public void setTasks(List<Game> gameList) {
        mGameList = gameList;
        notifyDataSetChanged();
    }

    public List<Game> getTasks() {

        return mGameList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, pincode, number, city;
        ImageView editImage;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            name = itemView.findViewById(R.id.person_name);
            email = itemView.findViewById(R.id.person_email);
            pincode = itemView.findViewById(R.id.person_pincode);
            number = itemView.findViewById(R.id.person_number);
            city = itemView.findViewById(R.id.person_city);
            editImage = itemView.findViewById(R.id.edit_Image);
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int elementId = mGameList.get(getAdapterPosition()).getId();
                    Intent i = new Intent(context, EditActivity.class);
                    //i.putExtra(Constants.UPDATE_Game_Id, elementId);
                    context.startActivity(i);
                }
            });
        }
    }
}
