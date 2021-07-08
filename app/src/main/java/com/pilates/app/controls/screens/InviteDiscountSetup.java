package com.pilates.app.controls.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pilates.app.R;
import com.pilates.app.controls.AvatarUploader;
import com.pilates.app.controls.DocRow;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.controls.listeners.OnTrainerSelectedOperation;
import com.pilates.app.model.dto.SaveGalleryResponseDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.UserDetailListDto;
import com.pilates.app.model.dto.UserInfoDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.Constant;
import com.pilates.app.util.FileUploader;
import com.pilates.app.util.ImageFilePath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.MODE_PRIVATE;

public class InviteDiscountSetup extends FrameLayout {


    private final RoundButtonGroup rbgSelectTraineeHasInstructor;
    private final RoundButtonGroup rbgSelectTraineeHasDiscount;
    private final RoundButton rbDiscountBack;
    private final RoundButton rbCheckCode;
    private final RoundButton rbFindTrainer;
    private final RoundButton rbCancelSelect;
    private final EditText discountCode;
    private final EditText trainerName;
    private final LinearLayout layoutFoundTrainers;
    private final LinearLayout layoutSelectedTrainer;
    private UserDataService userService;

    private TextView tvSignupErrorMessage;
    private TextView tvCodeStatus;
    private RoundButton rbContinue;

    private OnTrainerSelectedOperation listener;

    public InviteDiscountSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //TypedArray a = context.obtainStyledAttributes(attrs,
        //        R.styleable.RoundButton, 0, 0);
        //a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_invite_discount, this, true);

        userService = new UserDataService(context);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        ((TextView)findViewById(R.id.tvTraineeInstructorHeader)).setTypeface(font);
        ((TextView)findViewById(R.id.tvTraineeInstructorHeaderStep2)).setTypeface(font);

        rbgSelectTraineeHasInstructor = findViewById(R.id.rbgSelectTraineeHasInstructor);
        rbgSelectTraineeHasDiscount = findViewById(R.id.rbgSelectTraineeHasDiscount);
        rbDiscountBack = findViewById(R.id.rbDiscountBack);
        rbCheckCode = findViewById(R.id.rbCheckCode);
        rbFindTrainer = findViewById(R.id.rbFindTrainer);
        discountCode = findViewById(R.id.discountCode);
        trainerName = findViewById(R.id.trainerName);
        tvCodeStatus = findViewById(R.id.tvCodeStatus);
        tvCodeStatus.setVisibility(GONE);
        layoutFoundTrainers = findViewById(R.id.layoutFoundTrainers);
        rbCancelSelect = findViewById(R.id.rbCancelSelect);
        layoutSelectedTrainer = findViewById(R.id.layoutSelectedTrainer);

        rbCancelSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layoutSelectTrainer).setVisibility(View.GONE);
                findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.GONE);
                findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.VISIBLE);
            }
        });
        rbgSelectTraineeHasInstructor.setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(button.getValue().equals("true")) {
                    findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.GONE);
                    findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutDiscountCheck).setVisibility(View.GONE);
                    findViewById(R.id.layoutTrainerCheck).setVisibility(View.GONE);
                    layoutSelectedTrainer.setVisibility(GONE);
                    rbContinue.setVisibility(GONE);
                }
                else
                    rbContinue.setVisibility(VISIBLE);
            }

            @Override
            public void removed(RoundButton button) {
            }
        });

        rbgSelectTraineeHasDiscount.setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(button.getValue().equals("true")) {
                    findViewById(R.id.layoutDiscountCheck).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutTrainerCheck).setVisibility(View.GONE);
                }
                else {
                    findViewById(R.id.layoutDiscountCheck).setVisibility(View.GONE);
                    findViewById(R.id.layoutTrainerCheck).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void removed(RoundButton button) {
            }
        });

        rbDiscountBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.GONE);
                rbgSelectTraineeHasInstructor.clearSelected();
            }
        });

        rbCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCheckCode.setLoading(true);
                userService.findTrainerByDiscountCode(discountCode.getText().toString().trim(), new OnRequestResult<UserInfoDto>() {
                    @Override
                    public void requestComplete(UserInfoDto data) {
                        rbCheckCode.setLoading(false);
                        if(data.getError()) {
                            tvCodeStatus.setVisibility(VISIBLE);
                            tvCodeStatus.setText("Code invalid.");
                            tvCodeStatus.setTextColor(getResources().getColor(R.color.wooError, null));
                        }
                        else {
                            tvCodeStatus.setVisibility(VISIBLE);
                            tvCodeStatus.setText("Valid code.");
                            tvCodeStatus.setTextColor(getResources().getColor(R.color.wooGreen, null));

                            setSelectedTrainer(data);
                        }
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        rbCheckCode.setLoading(false);
                        tvCodeStatus.setVisibility(VISIBLE);
                        tvCodeStatus.setText("Failed to validate.");
                        tvCodeStatus.setTextColor(getResources().getColor(R.color.wooError, null));
                    }
                });
            }
        });

        rbFindTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbFindTrainer.setLoading(true);

                userService.findTrainersByName(trainerName.getText().toString(), new OnRequestResult<UserDetailListDto>() {
                    @Override
                    public void requestComplete(UserDetailListDto data) {
                        rbFindTrainer.setLoading(false);
                        layoutFoundTrainers.removeAllViewsInLayout();
                        findViewById(R.id.layoutSelectTrainer).setVisibility(View.VISIBLE);
                        findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.GONE);
                        findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.GONE);

                        if(data.getError() || data.getDetails().size() == 0) {
                            TextView v = new TextView(getContext());
                            v.setText("No instructors found.");
                            layoutFoundTrainers.addView(v);
                        }
                        else {
                            for(UserInfoDto info : data.getDetails()) {
                                DocRow row = new DocRow(getContext(), DocRow.RowType.TRAINER, true, "SELECT");
                                row.setTrainerData(info.getUserId(), info.getName(), info.getAvatarPath());
                                row.setRemoveListener(new OnRemoveByIdListener() {
                                    @Override
                                    public void remove(Long id) {
                                        findViewById(R.id.layoutSelectTrainer).setVisibility(View.GONE);
                                        findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.GONE);
                                        findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.VISIBLE);
                                        setSelectedTrainer(info);
                                    }
                                });
                                layoutFoundTrainers.addView(row);
                            }
                        }
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        rbFindTrainer.setLoading(false);
                        layoutFoundTrainers.removeAllViewsInLayout();
                        TextView v = new TextView(getContext());
                        v.setText("Couldn't load trainers.");
                        layoutFoundTrainers.addView(v);
                    }
                });
            }
        });
    }

    private void setSelectedTrainer(UserInfoDto info) {
        rbContinue.setVisibility(VISIBLE);
        findViewById(R.id.layoutDiscountCheck).setVisibility(View.GONE);
        findViewById(R.id.layoutTrainerCheck).setVisibility(View.GONE);
        rbDiscountBack.setVisibility(GONE);
        layoutSelectedTrainer.setVisibility(VISIBLE);
        layoutSelectedTrainer.removeAllViewsInLayout();

        DocRow row = new DocRow(getContext(), DocRow.RowType.TRAINER, false, null);
        row.setTrainerData(info.getUserId(), info.getName(), info.getAvatarPath());
        row.setRemoveListener(new OnRemoveByIdListener() {
            @Override
            public void remove(Long id) {
                listener.removed();
                layoutSelectedTrainer.removeAllViewsInLayout();
                layoutSelectedTrainer.setVisibility(GONE);
                rbgSelectTraineeHasDiscount.clearSelected();
                rbDiscountBack.setVisibility(VISIBLE);
                rbContinue.setVisibility(GONE);
            }
        });
        layoutSelectedTrainer.addView(row);

        listener.selected(info.getUserId());
    }

    public void setSelectedTrainerListener(OnTrainerSelectedOperation listener) {
        this.listener = listener;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
            load();
    }

    private void load() {
        findViewById(R.id.layoutTraineeInstructorStep1).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutTraineeInstructorStep2).setVisibility(View.GONE);
        findViewById(R.id.layoutSelectTrainer).setVisibility(View.GONE);
        findViewById(R.id.layoutDiscountCheck).setVisibility(View.GONE);
        findViewById(R.id.layoutTrainerCheck).setVisibility(View.GONE);

        rbContinue.setVisibility(GONE);
    }

    public void init(TextView tvSignupErrorMessage, RoundButton rbContinue) {
        this.tvSignupErrorMessage = tvSignupErrorMessage;
        this.rbContinue = rbContinue;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

}
