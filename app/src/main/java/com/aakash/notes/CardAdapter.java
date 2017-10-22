package com.aakash.notes;


import android.support.v7.widget.CardView;

/**
 * Created by akash on 15-Oct-17.
 */

    interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;
    float getBaseElevation();
    CardView getCardViewAt(int position);
    int getCount();
}
