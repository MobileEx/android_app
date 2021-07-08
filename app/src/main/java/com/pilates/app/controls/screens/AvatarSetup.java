package com.pilates.app.controls.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.pilates.app.R;
import com.pilates.app.controls.AvatarUploader;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.model.dto.SaveGalleryResponseDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.TrainerGalleryDto;
import com.pilates.app.model.dto.UserInfoDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.BitmapCropper;
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

public class AvatarSetup extends FrameLayout {

    private AvatarUploader avatarUploader;
    private RoundButton rbRemoveProfilePic;
    private UserDataService userService;
    private TextView tvSignupErrorMessage;
    private Target currentLoad;
    private boolean loadOnShow;
    private FileUploader<SaveGalleryResponseDto> uploader;
    private String activeURL;

    public AvatarSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //TypedArray a = context.obtainStyledAttributes(attrs,
        //        R.styleable.RoundButton, 0, 0);
        //a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_avatar_setup, this, true);


        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        ((TextView)findViewById(R.id.tvProfilePic)).setTypeface(font);

        uploader = new FileUploader<>();
        userService = new UserDataService(context);
        avatarUploader = findViewById(R.id.avatarUploader);
        avatarUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        rbRemoveProfilePic = findViewById(R.id.rbRemoveProfilePic);
        rbRemoveProfilePic.setVisibility(View.GONE);
        rbRemoveProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProfilePic();
            }
        });
    }

    private void removeProfilePic() {
        rbRemoveProfilePic.setLoading(true);

        userService.removeProfilePic(new OnRequestResult<StatusMessageDto>() {
            @Override
            public void requestComplete(StatusMessageDto infoDto) {
                rbRemoveProfilePic.setLoading(false);

                if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText(infoDto.getMessage());
                } else {
                    rbRemoveProfilePic.setVisibility(View.GONE);
                    avatarUploader.setImageResource(R.drawable.icon_upload);
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                rbRemoveProfilePic.setLoading(false);
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("An unexpected error has occurred.");
            }
        });
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
            load();
    }


    private void load() {
        avatarUploader.setLoading(true);
        userService.getUserDetails(getContext().getSharedPreferences("woobody", MODE_PRIVATE).getLong("userId", 0),
                new OnRequestResult<UserInfoDto>() {
            @Override
            public void requestComplete(UserInfoDto data) {
                if(data.getAvatarPath() != null)
                    showAvatar(data.getAvatarPath());
                else
                    avatarUploader.setLoading(false);
            }

            @Override
            public void requestError(VolleyError ex) {

            }
        });
    }

    public void pickImage() {
        tvSignupErrorMessage.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission((Activity)getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePicture});

        ((Activity)getContext()).startActivityForResult(chooserIntent, Constant.PickReason.AVATAR);
    }

    public void init(TextView tvSignupErrorMessage, boolean loadOnShow) {
        this.tvSignupErrorMessage = tvSignupErrorMessage;
        this.loadOnShow = loadOnShow;
    }

    public void doUploadImage(Intent data) {
        try {
            ArrayList<Uri> imageUris = new ArrayList<>();

            if (data.getClipData() == null) {
                imageUris.add(data.getData());
            } else {
                for (int i = 0; i < data.getClipData().getItemCount(); i++)
                    imageUris.add(data.getClipData().getItemAt(i).getUri());
            }

            for (int i = 0; i < imageUris.size(); i++) {
                Uri selectedImage = imageUris.get(i);
                InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImage);
                // TODO use clip data for multiple image select :/
                tvSignupErrorMessage.setVisibility(View.GONE);

                if (inputStream.available() > 10000000) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("Image cannot exceed 10MB.");
                    return;
                }

                String realPath = ImageFilePath.getPath(getContext(), data.getData());
                String[] pathParts = realPath.split("/");
                final String filename = pathParts[pathParts.length - 1];
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);

                avatarUploader.setLoading(true);

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            final SaveGalleryResponseDto uploadStatus = uploader.multipartRequest(Constant.ServiceEndpoints.USER_MANAGEMENT
                                            + "/user-management/api/v1/user/upload/avatar",
                                    getContext().getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""),
                                    inputStream, "avatar", filename, null, SaveGalleryResponseDto.class);

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // status message contains URL TODO
                                    if (!uploadStatus.getError()) {
                                        showAvatar(uploadStatus.getMessage());

                                        rbRemoveProfilePic.setVisibility(View.VISIBLE);
                                    } else {
                                        avatarUploader.setLoading(false);

                                        tvSignupErrorMessage.setVisibility(View.VISIBLE);
                                        tvSignupErrorMessage.setText(uploadStatus.getMessage());
                                    }
                                }
                            });

                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
            }
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAvatar(String url) {
        activeURL = url;
        if(currentLoad != null) {
            Glide.with((Activity) getContext()).clear(currentLoad);
            currentLoad = null;
        }

        tvSignupErrorMessage.setVisibility(View.GONE);
        avatarUploader.setLoading(true);
        currentLoad = Glide.with((Activity) getContext()).load(url + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        avatarUploader.setLoading(false);

                        tvSignupErrorMessage.setVisibility(View.VISIBLE);
                        tvSignupErrorMessage.setText("Image failed to load.");
                        avatarUploader.setImageResource(R.drawable.icon_upload);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        avatarUploader.setLoading(false);
                        return false;
                    }
                })
                .into(avatarUploader.exposeImage());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public String getActiveURL() {
        return activeURL;
    }
}
