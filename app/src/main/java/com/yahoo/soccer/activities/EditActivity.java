package com.yahoo.soccer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.yahoo.soccer.R;
import com.yahoo.soccer.constants.Constants;
import com.yahoo.soccer.database.AppDatabase;
import com.yahoo.soccer.database.AppExecutors;
import com.yahoo.soccer.model.Game;

public class EditActivity extends AppCompatActivity {
    EditText aname, bname, ascore, bscore;
    Button button;
    String mGameId;
    Intent intent;
    private AppDatabase mDb;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        mDb = AppDatabase.getInstance(getApplicationContext());
        intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPDATE_Game_Id)) {
            button.setText("Update");

            mGameId = intent.getStringExtra(Constants.UPDATE_Game_Id);
            if(mGameId==null)
                mGameId = "0";

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Game game = mDb.gameDao().loadGameById(mGameId);
                    populateUI(game);
                }
            });


        }

    }

    private void populateUI(Game game) {

        if (game == null) {
            return;
        }

        aname.setText(game.getAname());
        bname.setText(game.getBname());
        bscore.setText(game.getAscore());
        ascore.setText(game.getBscore());
    }

    private void initViews() {
        aname = findViewById(R.id.edit_name);
        bname = findViewById(R.id.edit_email);
        ascore = findViewById(R.id.edit_pincode);
        bscore = findViewById(R.id.edit_number);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    public void onSaveButtonClicked() {
        final Game game = new Game(
                aname.getText().toString(),
                bname.getText().toString(),
                bscore.getText().toString(),
                ascore.getText().toString());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (!intent.hasExtra(Constants.UPDATE_Game_Id)) {
                    mDb.gameDao().insertGame(game);
                } else {
                    game.setId(mGameId);
                    mDb.gameDao().updateGame(game);
                }
                finish();
            }
        });
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
