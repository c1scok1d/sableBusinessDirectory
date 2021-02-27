package com.macinternetservices.sablebusinessdirectory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.Geofence;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.macinternetservices.sablebusinessdirectory.MainActivity.GEOFENCE_EXPIRATION_IN_MILLISECONDS;


public class RecentListingsAdapter extends RecyclerView.Adapter {

    private ArrayList<ListingsModel> dataset;
    private ArrayList<ListingsModel> locationReview = new ArrayList<>();
    private  ArrayList<ListingsModel> locationFoo = new ArrayList<>();
    private ArrayList<ListingsModel> locationReviewShow = new ArrayList<>();

    private Context mContext;


    public RecentListingsAdapter(ArrayList<ListingsModel> mlist, Context context) {
        dataset = mlist;
        this.mContext = context;

    }

    public  class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvContent, tvRatingCount, tvId;
        ImageView ivFeaturedImage;
        RatingBar simpleRatingBar;


        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvContent = itemView.findViewById(R.id.tvContent);
            this.simpleRatingBar = itemView.findViewById(R.id.simpleRatingBar);
            this.tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
            this.ivFeaturedImage = itemView.findViewById(R.id.ivFeaturedImage);
            this.tvId = itemView.findViewById(R.id.tvId);
           // ArrayList<ListingsModel> locationReviewShow = new ArrayList<>();


            ivFeaturedImage.setOnClickListener(v -> {
                Intent showReviews = new Intent(v.getContext(), ListReviewActivity.class);


                for (int i = 0; i < dataset.size(); i++) {

                    if ((dataset.get(i).id == Integer.parseInt(tvId.getText().toString()))) {

                        locationReviewShow.add((new ListingsModel(ListingsModel.IMAGE_TYPE,
                                locationReview.get(i).id,
                                locationReview.get(i).title,
                                locationReview.get(i).link,
                                locationReview.get(i).status,
                                locationReview.get(i).category,
                                locationReview.get(i).featured,
                                locationReview.get(i).featured_image,
                                locationReview.get(i).bldgno,
                                locationReview.get(i).street,
                                locationReview.get(i).city,
                                locationReview.get(i).state,
                                locationReview.get(i).country,
                                locationReview.get(i).zipcode,
                                locationReview.get(i).latitude,
                                locationReview.get(i).longitude,
                                locationReview.get(i).rating,
                                locationReview.get(i).ratingCount,
                                locationReview.get(i).phone,
                                locationReview.get(i).email,
                                locationReview.get(i).website,
                                locationReview.get(i).twitter,
                                locationReview.get(i).facebook,
                                locationReview.get(i).video,
                                locationReview.get(i).hours,
                                locationReview.get(i).isOpen,
                                locationReview.get(i).logo,
                                locationReview.get(i).content,
                                locationReview.get(i).featured_image,
                                locationReview.get(i).content,
                                new SimpleGeofence(locationReview.get(i).title, locationReview.get(i).latitude, locationReview.get(i).longitude,
                                        100, GEOFENCE_EXPIRATION_IN_MILLISECONDS,
                                        Geofence.GEOFENCE_TRANSITION_ENTER
                                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                                | Geofence.GEOFENCE_TRANSITION_EXIT))));

                        Bundle locationReviewBundle = new Bundle();
                        locationReviewBundle.putParcelableArrayList("locationReviewBundle", locationReviewShow);
                        showReviews.putExtra("locationReview", locationReviewShow);
                        itemView.getContext().startActivity(showReviews);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_listings, parent, false);
        return new ImageTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {

        //final WooModel object = dataset.get(position);

        Picasso.Builder builder = new Picasso.Builder(mContext);
        final ListingsModel object = dataset.get(position);


        ((ImageTypeViewHolder) holder).tvName.setText(dataset.get(position).title);
        ((ImageTypeViewHolder) holder).tvId.setText(String.valueOf(dataset.get(position).id));
        ((ImageTypeViewHolder) holder).tvContent.setText(dataset.get(position).content);
        ((ImageTypeViewHolder) holder).simpleRatingBar.setRating(dataset.get(position).rating);
        ((ImageTypeViewHolder) holder).tvRatingCount.setText(String.valueOf(dataset.get(position).ratingCount));
        builder.build().load(dataset.get(position).featured_image).into(((ImageTypeViewHolder) holder).ivFeaturedImage);
        locationReview.add(new ListingsModel(ListingsModel.IMAGE_TYPE,
                object.id,
                object.title,
                object.link,
                object.status,
                object.category,
                object.featured,
                object.image,
                object.bldgno,
                object.street,
                object.city,
                object.state,
                object.country,
                object.zipcode,
                object.latitude,
                object.longitude,
                object.rating,
                object.ratingCount,
                object.phone,
                object.email,
                object.website,
                object.twitter,
                object.facebook,
                object.video,
                object.hours,
                object.isOpen,
                object.logo,
                object.content,
                object.featured_image,
                object.content,
                new SimpleGeofence(object.title, object.latitude, object.longitude,
                        100, GEOFENCE_EXPIRATION_IN_MILLISECONDS,
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT)));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}