package com.example.skovgaard.androidrollthedice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.skovgaard.androidrollthedice.Model.RollModel;

/**
 * Created by Adamino.
 */

public class HistoryActivity extends AppCompatActivity {
    private RollModel mRollModel;
    private RecyclerHistoryAdapter mRecyclerHistoryAdapter;

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, HistoryActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mRollModel = RollModel.getInstance();

        RecyclerView recyclerView = findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerHistoryAdapter = new RecyclerHistoryAdapter();
        recyclerView.setAdapter(mRecyclerHistoryAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                mRollModel.clearRolls();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        mRecyclerHistoryAdapter.notifyDataSetChanged();
    }
}
