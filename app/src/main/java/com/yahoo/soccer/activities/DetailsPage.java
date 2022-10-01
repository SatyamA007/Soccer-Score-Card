package com.yahoo.soccer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.yahoo.soccer.R;
import com.yahoo.soccer.adaptors.DetailsAdaptor;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Team;

import java.util.List;

public class DetailsPage extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DetailsAdaptor mAdapter;
    private AppDatabase mDb;
    String mTeamId;
    Intent intent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        if (intent == null || !intent.hasExtra(Constants.UPDATE_Team_Id)||intent.getStringExtra(Constants.UPDATE_Team_Id)==null) {
            finish();
        }

        mTeamId = intent.getStringExtra(Constants.UPDATE_Team_Id);
        mDb = AppDatabase.getInstance(getApplicationContext());
        // Initialize the adapter
        mAdapter = new DetailsAdaptor(this);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Team> teams = mAdapter.getTeamsAgainst(mTeamId, mDb);
                getSupportActionBar().setTitle(mDb.teamDao().loadTeamById(mTeamId).getName());  // provide compatibility to all the versions
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.setTasks(teams);
                    }
                });
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
























