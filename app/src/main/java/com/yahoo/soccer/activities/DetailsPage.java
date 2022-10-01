package com.yahoo.soccer.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yahoo.soccer.R;
import com.yahoo.soccer.adaptors.DetailsAdaptor;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DetailsPage extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private DetailsAdaptor mAdapter;
    private AppDatabase mDb;
    String mTeamId;
    String mTeamName;
    Intent intent;
    private MutableLiveData<Integer> sortingConstant;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        sortingConstant = new MutableLiveData<>();
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
                mTeamName = mDb.teamDao().loadTeamById(mTeamId).getName();  // provide compatibility to all the versions
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().setTitle(mTeamName);
                        mAdapter.setTasks(teams);
                        sortingConstant.setValue(Constants.nsort_up);
                    }
                });
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        sortingConstant.observe(this, sortObserver);
        setSortButtonClicks();
    }
    private void setSortButtonClicks() {

        final List<ImageView> allSorts = new ArrayList<>(Arrays.asList(
                (ImageView)findViewById(R.id.wsort_up),(ImageView)findViewById(R.id.wsort_down),
                (ImageView)findViewById(R.id.lsort_up),(ImageView)findViewById(R.id.lsort_down),
                (ImageView)findViewById(R.id.dsort_up),(ImageView)findViewById(R.id.dsort_down),
                (ImageView)findViewById(R.id.nsort_up),(ImageView)findViewById(R.id.nsort_down),
                (ImageView)findViewById(R.id.tsort_up),(ImageView)findViewById(R.id.tsort_down)
        ));

        final List<LinearLayout> allSortViews = new ArrayList<>(Arrays.asList(
                (LinearLayout)findViewById(R.id.sort_w),(LinearLayout)findViewById(R.id.sort_l),
                (LinearLayout)findViewById(R.id.sort_d),(LinearLayout)findViewById(R.id.sort_name),
                (LinearLayout)findViewById(R.id.sort_t)
        ));

        for(int i=0;i<allSortViews.size();i++){
            LinearLayout ll = allSortViews.get(i);
            ll.setOnClickListener(getListner(i, allSorts));
        }

    }

    private View.OnClickListener getListner(int i, final List<ImageView> allSorts) {
        final int finalI = i;

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    int sortVal = sortingConstant.getValue();

                    for(ImageView iv:allSorts){
                        iv.setVisibility(View.INVISIBLE);
                    }

                    if(sortVal/2==finalI){
                        sortVal = sortVal%2==1?finalI*2:finalI*2+1;
                        if(sortVal%2==1){
                            allSorts.get(sortVal-1).setVisibility(View.GONE);
                        }
                        else{
                            allSorts.get(sortVal+1).setVisibility(View.GONE);
                        }
                    }
                    else {
                        sortVal = finalI*2;
                    }

                    allSorts.get(sortVal).setVisibility(View.VISIBLE);
                    sortingConstant.postValue(sortVal);
                } catch (NullPointerException ignored){
                }
            }
        };
    }

    final Observer<Integer> sortObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer integer) {
            sortTasks(integer);
        }
    };

    private void sortTasks(final Integer sortVal) {

                switch (sortVal){
                    case Constants.wsort_up:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return t1.getWin() - team.getWin();
                        }
                    });
                        break;
                    case Constants.wsort_down:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return team.getWin() - t1.getWin();
                        }
                    });
                        break;
                    case Constants.lsort_up:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return t1.getLoss() - team.getLoss();
                        }
                    });
                        break;
                    case Constants.lsort_down:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return team.getLoss() - t1.getLoss();
                        }
                    });
                        break;
                    case Constants.dsort_up:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return t1.getDraw() - team.getDraw();
                        }
                    });
                        break;
                    case Constants.dsort_down:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return team.getDraw() - t1.getDraw();
                        }
                    });
                        break;
                    case Constants.nsort_up:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return team.getName().compareTo(t1.getName());
                        }
                    });
                        break;
                    case Constants.nsort_down:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return t1.getName().compareTo(team.getName());
                        }
                    });
                        break;
                    case Constants.tsort_up:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            int tot1 = team.getWin()+team.getLoss()+team.getDraw();
                            int tot2 = t1.getWin()+t1.getLoss()+t1.getDraw();
                            return tot2-tot1;
                        }
                    });
                        break;
                    case Constants.tsort_down:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            int tot1 = team.getWin()+team.getLoss()+team.getDraw();
                            int tot2 = t1.getWin()+t1.getLoss()+t1.getDraw();
                            return tot1-tot2;                        }
                    });
                        break;
                    default:  mAdapter.getTasks().sort(new Comparator<Team>() {
                        @Override
                        public int compare(Team team, Team t1) {
                            return team.getName().compareTo(t1.getName());
                        }
                    });
                        break;
                }

                mAdapter.notifyDataSetChanged();
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
























