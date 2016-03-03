package com.alexgwyn.viewexploder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SampleViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    static class SampleViewHolder extends RecyclerView.ViewHolder {

        public SampleViewHolder(View itemView) {
            super(itemView);
        }

        public static SampleViewHolder inflate(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
            return new SampleViewHolder(view);
        }
    }
}
