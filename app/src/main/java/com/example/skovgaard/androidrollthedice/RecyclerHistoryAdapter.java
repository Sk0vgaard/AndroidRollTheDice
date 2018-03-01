package com.example.skovgaard.androidrollthedice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.skovgaard.androidrollthedice.BE.Roll;
import com.example.skovgaard.androidrollthedice.BLL.DieManager;
import com.example.skovgaard.androidrollthedice.Model.RollModel;

/**
 * Created by Adamino.
 */

public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.HistoryHolder> {
    private RollModel mRollModel = RollModel.getInstance();

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new HistoryHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        Roll roll = mRollModel.getRoll(position);
        holder.bind(roll);
    }

    @Override
    public int getItemCount() {
        return mRollModel.getDiceList().size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        TextView mRollInfo;
        LinearLayout mDiceList;
        Context mContext;

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.history_row_item, parent, false));
            mContext = parent.getContext();

            mRollInfo = itemView.findViewById(R.id.txtRollInfo);
            mDiceList = itemView.findViewById(R.id.linearDice);
        }

        public void bind(Roll roll) {
            mRollInfo.setText(roll.getTimeAsString());

            for (int currentDie : roll.getDice()) {
                int dieResource = DieManager.getImage(currentDie);
                ImageView dieImageView = new ImageView(mContext);
                dieImageView.setImageResource(dieResource);
                mDiceList.addView(dieImageView);
            }
        }
    }
}
