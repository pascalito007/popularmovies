package com.example.android.popularmovies.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.DetailsActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jem001 on 29/08/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Cursor mCursor;
    private TrailerItemClickListener mOnclickListener;

    public TrailerAdapter(TrailerItemClickListener clickListener) {
        this.mOnclickListener=clickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        view.setFocusable(true);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.trailerTitleTextView.setText(mCursor.getString(DetailsActivity.INDEX_TRAILER_TITLE));
        holder.trailerTitleTextView.setTag(mCursor.getString(DetailsActivity.INDEX_TRAILER_TYPE));
        Glide.with(holder.trailerImageView.getContext()).load(NetworkUtils.BASE_IMAGE_YOUTUBE_URL+"/"+mCursor.getString(DetailsActivity.INDEX_TRAILER_KEY)+"/default.jpg").into(holder.trailerImageView);
    }

    @Override
    public int getItemCount() {
        return mCursor==null?0:mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        this.mCursor=newCursor;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trailerTitleTextView;
        ImageView trailerImageView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerTitleTextView = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            trailerImageView= (ImageView) itemView.findViewById(R.id.img_trailer_item);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            mOnclickListener.onClick(mCursor.getString(mCursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_KEY)));
        }
    }

    public interface TrailerItemClickListener {
        void onClick(String key);
    }
}
