package com.example.skovgaard.androidrollthedice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Adamino.
 */

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerHistoryAdapter adapter = new RecyclerHistoryAdapter();
        recyclerView.setAdapter(adapter);
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, HistoryActivity.class);
    }
}
