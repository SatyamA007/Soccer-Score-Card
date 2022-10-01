package com.yahoo.soccer.activities;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yahoo.soccer.R;
import com.yahoo.soccer.adaptors.TeamAdaptor;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    private RecyclerView mRecyclerView;
    private TeamAdaptor mAdapter;
    private AppDatabase mDb;
    private MutableLiveData<Integer> sortingConstant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Overall Standing");  // provide compatibility to all the versions
        sortingConstant = new MutableLiveData<>();
        floatingActionButton = findViewById(R.id.refreshFAB);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask((MainActivity) v.getContext()).execute("http://l.yimg.com/re/v2/coding_exercise/soccer_data.json");
                sortingConstant.setValue(Constants.nsort_up);
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSortButtonClicks();

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TeamAdaptor(this);
        mRecyclerView.setAdapter(mAdapter);
        mDb = AppDatabase.getInstance(getApplicationContext());
        sortingConstant.observe(this, sortObserver);
        sortingConstant.setValue(Constants.nsort_up);
    }

    final Observer<Integer> sortObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer integer) {
            retrieveTasks();
        }
    };

    private void setSortButtonClicks() {

        final List<ImageView> allSorts = new ArrayList<>(Arrays.asList(
                (ImageView)findViewById(R.id.wsort_up),(ImageView)findViewById(R.id.wsort_down),
                (ImageView)findViewById(R.id.lsort_up),(ImageView)findViewById(R.id.lsort_down),
                (ImageView)findViewById(R.id.dsort_up),(ImageView)findViewById(R.id.dsort_down),
                (ImageView)findViewById(R.id.nsort_up),(ImageView)findViewById(R.id.nsort_down)
        ));

        final List<LinearLayout> allSortViews = new ArrayList<>(Arrays.asList(
                (LinearLayout)findViewById(R.id.sort_w),(LinearLayout)findViewById(R.id.sort_l),
                (LinearLayout)findViewById(R.id.sort_d),(LinearLayout)findViewById(R.id.sort_name)
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

                    sortingConstant.postValue(sortVal);
                    allSorts.get(sortVal).setVisibility(View.VISIBLE);
                } catch (NullPointerException ignored){
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveTasks();
    }

    private void retrieveTasks() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Team> teams;
                int sortVal = 0;

                try {
                    sortVal = sortingConstant.getValue();
                } catch (NullPointerException ignored){
                }

                switch (sortVal){
                    case Constants.wsort_up:  teams = mDb.teamDao().loadTeamByWin();
                                                break;
                    case Constants.wsort_down:  teams = mDb.teamDao().loadTeamByWinRev();
                                                break;
                    case Constants.lsort_up:  teams = mDb.teamDao().loadTeamByLoss();
                                                break;
                    case Constants.lsort_down:  teams = mDb.teamDao().loadTeamByLossRev();
                                                break;
                    case Constants.dsort_up:  teams = mDb.teamDao().loadTeamByDraw();
                                                break;
                    case Constants.dsort_down:  teams = mDb.teamDao().loadTeamByDrawRev();
                                                break;
                    case Constants.nsort_up:  teams = mDb.teamDao().loadTeamByName();
                                                break;
                    case Constants.nsort_down:  teams = mDb.teamDao().loadTeamByNameRev();
                                                break;
                    default:  teams = mDb.teamDao().loadTeamByName();
                                                break;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.setTasks(teams);
                    }
                });
            }
        });


    }

    public MutableLiveData<Integer> getSortLiveData() {
        return sortingConstant;
    }
}
