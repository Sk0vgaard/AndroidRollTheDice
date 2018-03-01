package com.example.skovgaard.androidrollthedice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.skovgaard.androidrollthedice.BE.Roll;
import com.example.skovgaard.androidrollthedice.Model.DiceModel;

/**
 * Created by Adamino.
 */

public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.HistoryHolder> {
    private DiceModel mDiceModel = DiceModel.getInstance();

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new HistoryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        Roll roll = mDiceModel.getRoll(position);
        holder.bind(roll);
    }

    @Override
    public int getItemCount() {
        return mDiceModel.getDiceList().size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(null, parent, false));
        }

        public void bind(Roll roll) {
            // TODO: Implement
        }
    }
}
