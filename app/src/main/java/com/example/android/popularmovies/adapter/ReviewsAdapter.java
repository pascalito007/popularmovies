package com.example.android.popularmovies.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoovieContrat;

/**
 * Created by jem001 on 29/08/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.GenreViewHolder> {

    private Cursor mCursor;

    public ReviewsAdapter() {
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.reviewTextView.setText(mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_CONTENT)));
        holder.authorTextView.setText(mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_AUTHOR)));
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        this.mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTextView;
        TextView authorTextView;

        public GenreViewHolder(View itemView) {
            super(itemView);
            reviewTextView = (TextView) itemView.findViewById(R.id.reviews);
            authorTextView = (TextView) itemView.findViewById(R.id.author);
        }
    }
}
