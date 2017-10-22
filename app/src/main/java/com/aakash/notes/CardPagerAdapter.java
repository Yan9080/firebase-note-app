package com.aakash.notes;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 15-Oct-17.
 */

class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<TextView> mTitles;
    private List<TextView> mContents;
    private List<Note> mData;
    private float mBaseElevation;
    private Activity mContext;
    private CardView cardView;
    private TextView  title;
    private TextView content;

    CardPagerAdapter(Activity context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        mTitles = new ArrayList<>();
        mContents = new ArrayList<>();
        mContext = context;
    }

    void addCardItem(Note item) {
        mTitles.add(null);
        mContents.add(null);
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        cardView = view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        mTitles.set(position, title);
        mContents.set(position, content);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("note", mData.get(position));

                Pair cardPair = Pair.create(getCardViewAt(position),getCardViewAt(position).getTransitionName());
                Pair titlePair = Pair.create(mTitles.get(position), "title_trans");
                Pair contentPair = Pair.create(mContents.get(position), "content_trans");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext,
                        cardPair);
                ActivityCompat.startActivity(mContext, intent, options.toBundle());
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    void clearList(){
        mData.clear();
    }

    private void bind(Note item, View view) {
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        title.setText(item.getTitle());
        content.setText(item.getContent());
    }

}
