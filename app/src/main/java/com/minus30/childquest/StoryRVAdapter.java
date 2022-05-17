package com.minus30.childquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minus30.childquest.R;

import java.util.List;

public class StoryRVAdapter extends RecyclerView.Adapter<StoryRVAdapter.StoryViewHolder> {

    public static class StoryViewHolder extends RecyclerView.ViewHolder  {

        CardView scenarioView;
        TextView scenarioNameView, scenarioRatingView, scenarioPriceView, scenarioTimeView;
        ImageView scenarioImageView;

        StoryViewHolder(View itemView) {
            super(itemView);
            scenarioView = (CardView)itemView.findViewById(R.id.scenario);
            scenarioNameView = (TextView)itemView.findViewById(R.id.scenarioName);
            scenarioImageView = (ImageView)itemView.findViewById(R.id.scenarioImage);
            scenarioRatingView = (TextView)itemView.findViewById(R.id.scenarioRating);
            scenarioPriceView = (TextView)itemView.findViewById(R.id.scenarioPrice);
            scenarioTimeView = (TextView)itemView.findViewById(R.id.scenarioTime);
        }
    }


    List<Story> stories;

    StoryRVAdapter(List<Story> stories){
        this.stories = stories;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.scenario_loop, viewGroup, false);

        StoryViewHolder pvh = new StoryViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(StoryViewHolder StoryViewHolder, int i) {
        String price = StoryHelper.NumberToPriceText(stories.get(i).price);

        StoryViewHolder.scenarioView.setTag(stories.get(i).storyId);
        StoryViewHolder.scenarioNameView.setText(stories.get(i).name);
        StoryViewHolder.scenarioImageView.setImageBitmap(stories.get(i).photoBitmap);
        StoryViewHolder.scenarioRatingView.setText(stories.get(i).rating);
        StoryViewHolder.scenarioPriceView.setText(price);
        StoryViewHolder.scenarioTimeView.setText(stories.get(i).duration);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }
}