package com.pilates.app.controls.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
import com.pilates.app.controls.GalleryUploader;
import com.pilates.app.model.dto.SaveGalleryResponseDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.TrainerGalleryDto;
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

public class GallerySetup extends FrameLayout {

    private final ProgressBar pbLoader;
    private final boolean classMode;
    private final boolean showcaseMode;
    private ArrayList<GalleryUploader> galleryImages;
    private ImageView imgAddImage;
    private ArrayList<SaveGalleryResponseDto> galleryPaths;
    private TextView tvImagesCount;
    private UserDataService userService;
    private BitmapCropper bitmapCropper;
    private TextView tvSignupErrorMessage;
    private FileUploader<SaveGalleryResponseDto> uploader;
    private Long classId = (long)0;

    public GallerySetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GallerySetup, 0, 0);
        classMode = a.getBoolean(R.styleable.GallerySetup_classMode, false);
        showcaseMode = a.getBoolean(R.styleable.GallerySetup_showcaseMode, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(showcaseMode ? R.layout.layout_gallery_showcase : R.layout.layout_gallery_setup, this, true);


        uploader = new FileUploader<SaveGalleryResponseDto>();

        galleryPaths = new ArrayList<>();
        galleryImages = new ArrayList<>();
        galleryImages.add(findViewById(R.id.guGallery1));
        galleryImages.add(findViewById(R.id.guGallery2));
        galleryImages.add(findViewById(R.id.guGallery3));
        galleryImages.add(findViewById(R.id.guGallery4));
        galleryImages.add(findViewById(R.id.guGallery5));
        galleryImages.add(findViewById(R.id.guGallery6));
        galleryImages.add(findViewById(R.id.guGallery7));
        galleryImages.add(findViewById(R.id.guGallery8));

        if(showcaseMode)
            for(GalleryUploader image : galleryImages)
                image.setShowcaseMode();

        userService = new UserDataService(context);
        bitmapCropper = new BitmapCropper();
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");

        if(!showcaseMode) {
            imgAddImage = findViewById(R.id.imgAddImage);
            imgAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage();
                }
            });
            tvImagesCount = ((TextView) findViewById(R.id.tvImagesCount));
            tvImagesCount.setTypeface(font);
            ((TextView) findViewById(R.id.tvGalleryHeader)).setTypeface(font);
        }

        pbLoader = findViewById(R.id.pbGalleryLoader);
        pbLoader.setVisibility(GONE);

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
            load();
    }


    public void load() {
        pbLoader.setVisibility(VISIBLE);

        if(!showcaseMode)
            tvSignupErrorMessage.setVisibility(GONE);

        if(classMode)
            userService.getClassGallery(classId, new OnRequestResult<TrainerGalleryDto>() {
                @Override
                public void requestComplete(TrainerGalleryDto data) {
                    pbLoader.setVisibility(GONE);
                    galleryPaths.clear();

                    for(SaveGalleryResponseDto image : data.getImages()) {
                        galleryPaths.add(image);
                    }

                    updateLoadedGallery();
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbLoader.setVisibility(GONE);
                }
            });
        else
            userService.getGallery(new OnRequestResult<TrainerGalleryDto>() {
                @Override
                public void requestComplete(TrainerGalleryDto data) {
                    pbLoader.setVisibility(GONE);
                    galleryPaths.clear();

                    for(SaveGalleryResponseDto image : data.getImages()) {
                        galleryPaths.add(image);
                    }

                    updateLoadedGallery();
                }

                @Override
                public void requestError(VolleyError ex) {
                    pbLoader.setVisibility(GONE);
                }
            });
    }

    public void pickImage() {
        if(galleryPaths.size() >= 8)
            return;

        if (ContextCompat.checkSelfPermission((Activity)getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePicture});

        ((Activity)getContext()).startActivityForResult(chooserIntent, Constant.PickReason.GALLERY);
    }

    private void updateLoadedGallery() {
        int idx = 0;

        boolean hasLoading = false;
        ArrayList<Integer> nulledImages = new ArrayList<>();

        while (idx < galleryImages.size()) {
            final int finalIdx = idx;
            if (idx < galleryPaths.size()) {
                if(galleryPaths.get(finalIdx) != null && !galleryPaths.get(finalIdx).isLoading()) {
                    galleryImages.get(finalIdx).setVisibility(View.VISIBLE);
                    galleryImages.get(finalIdx).setRemoveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(galleryPaths.get(finalIdx) == null || galleryPaths.get(finalIdx).isLoading())
                                return;

                            removeGalleryImage(finalIdx);
                        }
                    });

                    galleryImages.get(finalIdx).setLoading(false);
                    galleryImages.get(finalIdx).loadImage(galleryPaths.get(finalIdx).getPath());
                }
                else {
                    if(!hasLoading && galleryPaths.get(finalIdx) != null && galleryPaths.get(finalIdx).isLoading())
                        hasLoading = true;
                    else if(galleryPaths.get(finalIdx) == null)
                        nulledImages.add(finalIdx);

                    galleryImages.get(finalIdx).setVisibility(View.VISIBLE);
                    galleryImages.get(finalIdx).setLoading(true);
                }
            } else
                galleryImages.get(finalIdx).setVisibility(View.GONE);
            idx++;
        }

        if(!hasLoading && nulledImages.size() > 0)
        {
            for(int nulledIdx : nulledImages)
                galleryPaths.remove(nulledIdx);

            updateLoadedGallery();
        }

        if(!showcaseMode)
            tvImagesCount.setText(galleryPaths.size() + "/8 Max");
    }

    public void setErrorTextView(TextView tvSignupErrorMessage) {
        this.tvSignupErrorMessage = tvSignupErrorMessage;
    }

    private void addNewGalleryImage() {
        galleryImages.get(galleryPaths.size() + 1).setVisibility(View.VISIBLE);
        galleryImages.get(galleryPaths.size() + 1).setLoading(true);
    }

    private void removeGalleryImage(int idx) {
        galleryImages.get(idx).setLoading(true);

        if(classMode)
            userService.removeClassGalleryImage(galleryPaths.get(idx).getId(), new OnRequestResult<StatusMessageDto>() {
                @Override
                public void requestComplete(StatusMessageDto infoDto) {
                    galleryImages.get(idx).setLoading(false);

                    if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                        tvSignupErrorMessage.setVisibility(View.VISIBLE);
                        tvSignupErrorMessage.setText(infoDto.getMessage());
                    } else {
                        galleryPaths.remove(idx);
                        updateLoadedGallery();
                    }
                }

                @Override
                public void requestError(VolleyError ex) {
                    galleryImages.get(idx).setLoading(false);
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("An unexpected error has occurred.");
                }
            });
        else
            userService.removeGalleryImage(galleryPaths.get(idx).getId(), new OnRequestResult<StatusMessageDto>() {
                @Override
                public void requestComplete(StatusMessageDto infoDto) {
                    galleryImages.get(idx).setLoading(false);

                    if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                        tvSignupErrorMessage.setVisibility(View.VISIBLE);
                        tvSignupErrorMessage.setText(infoDto.getMessage());
                    } else {
                        galleryPaths.remove(idx);
                        updateLoadedGallery();
                    }
                }

                @Override
                public void requestError(VolleyError ex) {
                    galleryImages.get(idx).setLoading(false);
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("An unexpected error has occurred.");
                }
            });
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
                // can't upload more than 8 photoss
                if(galleryPaths.size() + (i + 1) > 8)
                    continue;

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

                galleryPaths.add(new SaveGalleryResponseDto());
                galleryPaths.get(galleryPaths.size() - 1).setLoading(true);
                final int dummyPathIdx = galleryPaths.size() - 1;
                addNewGalleryImage();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {

                            final SaveGalleryResponseDto uploadStatus = uploader.multipartRequest(Constant.ServiceEndpoints.USER_MANAGEMENT
                                            +  (classMode ? "/user-management/api/v1/class/save/gallery/" + classId : "/user-management/api/v1/classinfo/save/gallery"),
                                    getContext().getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""),
                                    inputStream, "gallery", filename, null, SaveGalleryResponseDto.class);

                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // status message contains URL TODO
                                    if (!uploadStatus.getError()) {
                                        galleryPaths.set(dummyPathIdx, uploadStatus);
                                        updateLoadedGallery();
                                    } else {
                                        galleryImages.get(dummyPathIdx).setVisibility(View.GONE);
                                        galleryImages.get(dummyPathIdx).setLoading(false);

                                        galleryPaths.set(dummyPathIdx, null);
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        updateLoadedGallery();
    }

    public ArrayList<SaveGalleryResponseDto> getGalleryPaths() {
        return galleryPaths;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}
