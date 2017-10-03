package com.codepath.apps.twitter.adapters;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.utils.CommonUtils;

import org.w3c.dom.Text;

/**
 * Created by rdeshpan on 9/25/2017.
 */

public class CustomBindingAdapter {
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {

        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(new RequestOptions()
                        .override(125, 125)
                        .circleCrop()
                        .placeholder(R.drawable.ic_launcher))
                .into(view);
    }

    @BindingAdapter({"bind:imageUrlFull"})
    public static void loadImageFull(ImageView view, String imageUrl) {

        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher))
                .into(view);
    }


    @BindingAdapter({"bind:createdAt"})
    public static void getCreatedAt(TextView view, String createdAt) {
        if(createdAt != null &&  !createdAt.isEmpty())
            view.setText(CommonUtils.getRelativeTimeAgo(createdAt));
    }

    @BindingAdapter({"bind:createdAtExact"})
    public static void getCreatedAtExact(TextView view, String createdAt) {
        if(createdAt != null &&  !createdAt.isEmpty())
            view.setText(CommonUtils.getExactDate(createdAt));
    }
}
