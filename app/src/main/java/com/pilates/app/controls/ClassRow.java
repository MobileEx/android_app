package com.pilates.app.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.pilates.app.R;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.model.dto.ClassListingInfoDto;

import androidx.annotation.Nullable;

public class ClassRow extends FrameLayout {

    private final ProgressBar pbMain;
    private final ProgressBar pbAvatar;
    private ClassListingInfoDto data;

    public ClassRow(Context context, ClassListingInfoDto data) {
        super(context);

        this.data = data;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_row, this, true);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Bold.ttf");

        ((TextView)findViewById(R.id.tvClassRowName)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassRowPurpose)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassRowRating)).setTypeface(font);

        ((TextView)findViewById(R.id.tvClassRowName)).setText(data.getClassName());
        String purposeText = "FOR ";
        for(int i = 0; i < data.getPurposes().size(); i++) {
            if(i > 1)
                break;

            purposeText += (i == 0 ? "" : ", ") + data.getPurposes().get(i).getName().toUpperCase();
        }

        ((TextView)findViewById(R.id.tvClassRowPurpose)).setText(purposeText);
        if(data.getRating() != null)
            ((TextView)findViewById(R.id.tvClassRowRating)).setText(String.format("%.1f", data.getRating()));
        else
            ((TextView)findViewById(R.id.tvClassRowRating)).setText("-");
        pbMain = findViewById(R.id.pbClassRowMain);
        pbAvatar = findViewById(R.id.pbClassRowAvatar);

        findViewById(R.id.cvClassRowMain).setVisibility(GONE);
        setMainImageLoading(true);
        Glide.with(getContext()).load(data.getMainImage() + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setMainImageLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setMainImageLoading(false);
                        findViewById(R.id.cvClassRowMain).setVisibility(VISIBLE);
                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.imgClassRowGalleryMain)));

        ((TimeBox)findViewById(R.id.tbClassRow)).setTimeData(data.getStartDateTime(), data.getDuration());

        findViewById(R.id.cvClassRowAvatar).setVisibility(GONE);
        setAvatarImageLoading(true);
        Glide.with(getContext()).load(data.getTrainerAvatar() + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setAvatarImageLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setAvatarImageLoading(false);
                        findViewById(R.id.cvClassRowAvatar).setVisibility(VISIBLE);
                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.imgClassRowAvatar)));
    }

    private void setAvatarImageLoading(boolean loading) {
        pbAvatar.setVisibility(loading ? VISIBLE : GONE);
    }

    private void setMainImageLoading(boolean loading) {
        pbMain.setVisibility(loading ? VISIBLE : GONE);
    }
}
