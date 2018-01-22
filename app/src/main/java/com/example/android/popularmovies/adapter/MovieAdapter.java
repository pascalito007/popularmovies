package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by jem001 on 02/08/2017.
 */

//Create Adapter
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private MovieItemClickListener mOnclickListener;
    private Cursor mCursor;

    public MovieAdapter(MovieItemClickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_item, parent, false);
        view.setFocusable(true);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        int viewType = getItemViewType(position);
        holder.itemTitle.setText(mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_TITLE)));
        holder.itemVoteAverage.setText(String.valueOf(mCursor.getDouble(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_VOTE_AVERAGE))));
        Glide.with(holder.itemImageView.getContext()).load(NetworkUtils.BASE_IMAGE_URL + mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_POSTER_PATH))).into(holder.itemImageView);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        this.mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImageView;
        TextView itemTitle;
        TextView itemVoteAverage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            itemImageView = (ImageView) itemView.findViewById(R.id.img_item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.tv_title);
            itemVoteAverage = (TextView) itemView.findViewById(R.id.tv_vote_average);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            mOnclickListener.onListItemClick(mCursor.getInt(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID)), mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_TITLE)));
        }

    }

    public interface MovieItemClickListener {
        void onListItemClick(Integer movieId, String title);
    }
}
