package com.codepath.apps.twitter.adapters;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.ProfileActivity;
import com.codepath.apps.twitter.utils.CommonUtils;
import com.codepath.apps.twitter.utils.PatternEditableBuilder;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

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

    @BindingAdapter({"bind:imageUrlOriginal"})
    public static void loadImageOriginal(ImageView view, String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageUrl = imageUrl.replace("_normal", "");
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_launcher))
                    .into(view);
        }
    }

    @BindingAdapter({"bind:imageUrlOriginalCircle"})
    public static void loadImageOriginalCircle(ImageView view, String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageUrl = imageUrl.replace("_normal", "");
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.ic_launcher))
                    .into(view);
        }
    }

    @BindingAdapter({"bind:imageUrlBigger"})
    public static void loadImageBigger(ImageView view, String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageUrl = imageUrl.replace("normal", "bigger");
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.ic_launcher))
                    .into(view);
        }
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
        if (createdAt != null && !createdAt.isEmpty())
            view.setText(CommonUtils.getRelativeTimeAgo(createdAt));
    }

    @BindingAdapter({"bind:createdAtExact"})
    public static void getCreatedAtExact(TextView view, String createdAt) {
        if (createdAt != null && !createdAt.isEmpty())
            view.setText(CommonUtils.getExactDate(createdAt));
    }

    @BindingAdapter({"bind:textClickableSpan"})
    public static void getTextClickableSpan(TextView view, String originalText) {
        view.setText(originalText);
        Log.d("TwitterClient", originalText);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                                intent.putExtra("username", text.replace("@",""));
                                view.getContext().startActivity(intent);
                            }
                        }).
                addPattern(Pattern.compile("\\#(\\w+)"), Color.GREEN,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
                            }
                        })
                .into(view);
    }
}
