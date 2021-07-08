package com.pilates.app.controls.screens;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pilates.app.R;
import com.pilates.app.UserRegistry;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.TimeBox;
import com.pilates.app.controls.listeners.OnButtonClickListener;
import com.pilates.app.controls.listeners.OnClassInfoActionListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.Action;
import com.pilates.app.model.ActionBody;
import com.pilates.app.model.ActionType;
import com.pilates.app.model.UserSession;
import com.pilates.app.model.dto.ClassDetailsDto;
import com.pilates.app.model.dto.ClassFullInfoDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.enums.StreamingType;
import com.pilates.app.service.PeerConnectionClient;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.SelectionsHelper;
import com.pilates.app.ws.SignalingWebSocket;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.Nullable;
import jp.wasabeef.glide.transformations.BitmapTransformation;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class ClassInfoPanel extends FrameLayout {

    private final ProgressBar pbLoader;
    private final TextView tvClassInfoError;
    private final TextView tvRemainingTime;
    private UserDataService userService;
    private OnClassInfoActionListener completeListener;
    private ClassFullInfoDto specData;
    private Long classId = (long)0;
    private GallerySetup galleryShowcase;
    private Target currentLoad;
    private Target currentLoadAvatar;
    private Target currentLoadBlurred;
    private boolean draftMode;
    private boolean isTrainer;
    private boolean classStarting;
    private long classStartTimeRemaining;
    private CountDownTimer timer;
    private String mode = "draft";

    public ClassInfoPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_info_panel, this, true);

        userService = new UserDataService(context);

        pbLoader = findViewById(R.id.pbClassInfoLoader);
        pbLoader.setVisibility(GONE);

        tvClassInfoError = findViewById(R.id.tvClassInfoError);
        galleryShowcase = findViewById(R.id.galleryShowcase);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView)findViewById(R.id.tvInfoTrainerName)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoClassName)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoPurposes)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoExperienceLevel)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoClassCountType)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoClassRating)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoClassUsers)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoClassPrice)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoPilatesTypes)).setTypeface(font);
        ((TextView)findViewById(R.id.tvInfoDescription)).setTypeface(fontRegular);
        ((TextView)findViewById(R.id.tvInfoClassRemainingTimeTitle)).setTypeface(fontRegular);
        ((TextView)findViewById(R.id.tvInfoClassRemainingTime)).setTypeface(fontRegular);
        ((TextView)findViewById(R.id.tvStartingClass)).setTypeface(font);

        tvRemainingTime = ((TextView)findViewById(R.id.tvInfoClassRemainingTime));

        setImageLoading(false);
        setAvatarImageLoading(false);

        findViewById(R.id.rbClassInfoAct).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classStarting) {
                    findViewById(R.id.frameStartingClass).setVisibility(VISIBLE);
                    completeListener.main("starting");
                }
                else if(mode.equals("own")) {
                    ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(true);
                    userService.cancelClass(specData.getId(), new OnRequestResult<StatusMessageDto>() {
                        @Override
                        public void requestComplete(StatusMessageDto data) {
                            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(false);
                            completeListener.main(classStarting ? "starting" : "default");
                        }

                        @Override
                        public void requestError(VolleyError ex) {
                            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(false);
                        }
                    });
                } else if(mode.equals("new")) {
                    ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(true);
                    userService.registerForClass(specData.getId(), new OnRequestResult<StatusMessageDto>() {
                        @Override
                        public void requestComplete(StatusMessageDto data) {
                            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(false);
                            completeListener.main(classStarting ? "starting" : "default");
                        }

                        @Override
                        public void requestError(VolleyError ex) {
                            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(false);
                        }
                    });
                }
            }
        });

        findViewById(R.id.iconBackFromInfo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                completeListener.secondary();
            }
        });

        findViewById(R.id.rbClassInfoEdit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                completeListener.secondary();
            }
        });

        findViewById(R.id.rbClassInfoSubmit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RoundButton)findViewById(R.id.rbClassInfoSubmit)).setLoading(true);
                userService.publishClass(classId, new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        ((RoundButton)findViewById(R.id.rbClassInfoSubmit)).setLoading(false);
                        completeListener.main(classStarting ? "starting" : "default");
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        ((RoundButton)findViewById(R.id.rbClassInfoSubmit)).setLoading(false);
                    }
                });
            }
        });

        findViewById(R.id.framePreClassJoin).setVisibility(GONE);

        findViewById(R.id.rbClassInfoCancelJoin).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.STOP_COMMUNICATION));
                timer.cancel();
                findViewById(R.id.framePreClassJoin).setVisibility(GONE);
                SignalingWebSocket.getInstance().stop();
                PeerConnectionClient.disposeCurrent();
            }
        });

        findViewById(R.id.layoutDraft).setVisibility(GONE);
        findViewById(R.id.layoutGeneral).setVisibility(GONE);
        findViewById(R.id.frameStartingClass).setVisibility(GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
           load();
    }


    private void load() {
        findViewById(R.id.layoutDraft).setVisibility(GONE);
        findViewById(R.id.layoutGeneral).setVisibility(GONE);
        findViewById(R.id.frameStartingClass).setVisibility(GONE);
        pbLoader.setVisibility(VISIBLE);
        tvClassInfoError.setVisibility(GONE);
        findViewById(R.id.svClassInfo).setVisibility(GONE);

        userService.getFullClassInfo(classId, draftMode, new OnRequestResult<ClassFullInfoDto>() {
            @Override
            public void requestComplete(ClassFullInfoDto data) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.svClassInfo).setVisibility(VISIBLE);
                specData = data;

                showLoadedInfo();
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoader.setVisibility(GONE);
                tvClassInfoError.setVisibility(VISIBLE);
                tvClassInfoError.setText("Failed to load class info.");
            }
        });
    }

    public void setClassId(Long classId, boolean draftMode) {
        this.classId = classId;
        this.draftMode = draftMode;
    }

    public void setMode(String mode, boolean isTrainer) {
        this.isTrainer = isTrainer;
        this.mode = mode;
    }

    private void showLoadedInfo() {
        findViewById(R.id.layoutDraft).setVisibility(GONE);
        findViewById(R.id.layoutGeneral).setVisibility(VISIBLE);

        if(mode.equals("new")) {
            ((RoundButton) findViewById(R.id.rbClassInfoAct)).setText(getResources().getString(R.string.btn_add_to_studio));
        } else if(mode.equals("own")) {
            ((RoundButton) findViewById(R.id.rbClassInfoAct)).setText(getResources().getString(R.string.btn_cancel_class));
        }
        else {
            findViewById(R.id.layoutDraft).setVisibility(VISIBLE);
            findViewById(R.id.layoutGeneral).setVisibility(GONE);
        }

        ((TextView)findViewById(R.id.tvInfoTrainerName)).setText(specData.getTrainerName() + " PRESENTS");
        ((TextView)findViewById(R.id.tvInfoClassName)).setText(specData.getClassName());

        String purposeText = "FOR ";
        for(int i = 0; i < specData.getPurposes().size(); i++) {
            purposeText += (i == 0 ? "" : ", ") + specData.getPurposes().get(i).getName().toUpperCase();
        }

        ((TextView)findViewById(R.id.tvInfoPurposes)).setText(purposeText);
        ((TextView)findViewById(R.id.tvInfoExperienceLevel)).setText(specData.getExperienceLevel().toString());

        String classCountType = specData.getMultipleClasses() ? "WEEKLY" : "SINGLE";
        String[] dateParts = specData.getStartDateTime().split(" ");
        String[] timeParts = dateParts[1].split(":");

        int hour = Integer.parseInt(timeParts[0]);
        if(hour >= 0 && hour < 11)
            classCountType += " MORNING CLASS";
        else if(hour >= 11 && hour < 14)
            classCountType += " LUNCHTIME CLASS";
        else if(hour >= 14 && hour < 17)
            classCountType += " AFTERNOON CLASS";
        else if(hour >= 17)
            classCountType += " EVENING CLASS";

        ((TextView)findViewById(R.id.tvInfoClassCountType)).setText(classCountType);
        ((TextView)findViewById(R.id.tvInfoClassRating)).setText("0");
        ((TextView)findViewById(R.id.tvInfoClassUsers)).setText("0");
        ((TextView)findViewById(R.id.tvInfoClassPrice)).setText(specData.getPrice().toString());

        String typesText = "FOR ";
        for(int i = 0; i < specData.getPilatesTypes().size(); i++) {
            typesText += (i == 0 ? "" : ", ") + specData.getPilatesTypes().get(i).getName().toUpperCase() ;
        }
        ((TextView)findViewById(R.id.tvInfoPilatesTypes)).setText(typesText);
        ((TextView)findViewById(R.id.tvInfoDescription)).setText(specData.getDescription());

        galleryShowcase.setClassId(specData.getSetupId());
        galleryShowcase.load();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime classTime = LocalDateTime.parse(specData.getStartDateTime(), formatter);

        DateTimeFormatter formatterDayOfWeek = DateTimeFormatter.ofPattern("EEE HH:mm");
        ((TimeBox)findViewById(R.id.tbClassInfo)).setTimeData(classTime.format(formatterDayOfWeek), specData.getDuration());

        if(currentLoad != null) {
            Glide.with(getContext()).clear(currentLoad);
            currentLoad = null;
        }

        ((ImageView)findViewById(R.id.imgClassMain)).setImageResource(R.drawable.button_bg_gray);

        setImageLoading(true);
        currentLoad = Glide.with(getContext()).load(specData.getGalleryImages().get(0) + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setImageLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setImageLoading(false);
                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.imgClassMain)));

        if(currentLoadAvatar != null) {
            Glide.with(getContext()).clear(currentLoadAvatar);
            currentLoadAvatar = null;
        }

        findViewById(R.id.cvClassInfoAvatar).setVisibility(GONE);
        setAvatarImageLoading(true);
        currentLoadAvatar = Glide.with(getContext()).load(specData.getTrainerAvatar() + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        setAvatarImageLoading(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setAvatarImageLoading(false);
                        findViewById(R.id.cvClassInfoAvatar).setVisibility(VISIBLE);
                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.imgTrainerAvatar)));

        findViewById(R.id.rbClassInfoDraftBadge).setVisibility(draftMode ? VISIBLE : GONE);
        findViewById(R.id.imgInstruct).setVisibility(specData.getStreamingType() == StreamingType.WATCH_SHOW ? VISIBLE : GONE);

        if(currentLoadBlurred != null) {
            Glide.with(getContext()).clear(currentLoadBlurred);
            currentLoadBlurred = null;
        }

        currentLoadBlurred = Glide.with(getContext()).load(specData.getGalleryImages().get(0) + "?rnd=" + Math.random())
                .transform(new BlurTransformation())
                .into(((ImageView)findViewById(R.id.imgBlurred)));

        classStartTimeRemaining = ChronoUnit.MINUTES.between(LocalDateTime.now(ZoneOffset.UTC), classTime);
        if(mode != null && mode.equals("own") && classStartTimeRemaining < 9999999) {
            classStarting = true;
            findViewById(R.id.layoutGeneral).setVisibility(VISIBLE);
            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setText(isTrainer ? "START CLASS" : "JOIN CLASS");
            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setButtonBackground(R.drawable.button_bg_error);
            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setIconResource(R.drawable.icon_tab_go);
            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setButtonTextColor(getResources().getColor(R.color.wooLabelRed, null));
            ((RoundButton)findViewById(R.id.rbClassInfoAct)).setVisibility(VISIBLE);
        }
        ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(false);
    }

    private void setImageLoading(boolean loading) {
        findViewById(R.id.pbClassInfoImageLoader).setVisibility(loading ? VISIBLE : GONE);
    }

    private void setAvatarImageLoading(boolean loading) {
        findViewById(R.id.pbClassInfoAvatarLoader).setVisibility(loading ? VISIBLE : GONE);
    }

    public void setCompleteListener(OnClassInfoActionListener completeListener) {
        this.completeListener = completeListener;
    }

    public ClassFullInfoDto getData() {
        return specData;
    }

    public void initPreClassJoin() {
        if(timer != null) {
            timer.cancel();
        }

        if(classStartTimeRemaining < 0)
            classStartTimeRemaining = 1;

        findViewById(R.id.framePreClassJoin).setVisibility(VISIBLE);
        int seconds = (int)(classStartTimeRemaining * 60);
        int mins = seconds / 60;
        int secondsToMin = seconds - mins * 60;

        tvRemainingTime.setText(String.format("%02d", mins) + ":" + String.format("%02d", secondsToMin));
        timer = new CountDownTimer(classStartTimeRemaining * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int)(millisUntilFinished / 1000);
                int mins = seconds / 60;
                int secondsToMin = seconds - mins * 60;

                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvRemainingTime.setText(String.format("%02d", mins) + ":" + String.format("%02d", secondsToMin));
                    }
                });
            }

            @Override
            public void onFinish() {
                completeListener.main("starting");
            }
        };

        timer.start();
    }

    public void setMainLoading(boolean loading) {
        ((RoundButton)findViewById(R.id.rbClassInfoAct)).setLoading(true);
    }

    public View getClassInfoClassLoader() {
        return findViewById(R.id.frameStartingClass);
    }
}
