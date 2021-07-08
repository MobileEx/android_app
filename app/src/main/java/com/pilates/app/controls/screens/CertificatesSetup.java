package com.pilates.app.controls.screens;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.pilates.app.R;
import com.pilates.app.controls.DateTimeSelector;
import com.pilates.app.controls.DocRow;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.listeners.OnDateTimePickedListener;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.TrainerCertificateDto;
import com.pilates.app.model.dto.TrainerCertificateListDto;
import com.pilates.app.model.dto.TrainerGalleryDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.Constant;
import com.pilates.app.util.FileUploader;
import com.pilates.app.util.ImageFilePath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.MODE_PRIVATE;

public class CertificatesSetup extends FrameLayout {

    private final EditText documentName;
    private final EditText documentDate;
    private final TextView tvFileInfo;
    private final RoundButton rbCancelDocument;
    private final ProgressBar pbLoader;
    private final LinearLayout mainLayout;
    private UserDataService userService;
    private TextView tvSignupErrorMessage;
    private FileUploader<TrainerCertificateDto> uploader;
    private InputStream inputStream;
    private String filename;
    private RoundButton rbAddDocument;
    private RoundButton rbSaveDocument;
    private int fileSize;
    private RoundButton rbContinueSignup;
    private int addedCerts;
    private DateTimeSelector dts;

    public CertificatesSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //TypedArray a = context.obtainStyledAttributes(attrs,
        //        R.styleable.RoundButton, 0, 0);
        //a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_certificate_setup, this, true);

        uploader = new FileUploader<>();
        userService = new UserDataService(context);

        rbAddDocument = findViewById(R.id.rbAddDocument);
        rbAddDocument.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCert();
            }
        });

        documentName = findViewById(R.id.documentName);
        documentDate = findViewById(R.id.documentDate);

        documentDate.setKeyListener(null);
        documentDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dts.open("certificates", false);
            }
        });

        rbSaveDocument = findViewById(R.id.rbSaveDocument);
        rbSaveDocument.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSignupErrorMessage.setVisibility(View.GONE);

                if(documentName.getText().toString() == null || documentName.getText().toString().length() == 0) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("Enter document name.");
                    return;
                }

                if(documentDate.getText().toString() == null || documentDate.getText().toString().length() == 0) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("Enter document date.");
                    return;
                }

                TrainerCertificateDto body = new TrainerCertificateDto();
                body.setDate(documentDate.getText().toString());
                body.setName(documentName.getText().toString());

                submitCertificate(body);
            }
        });

        rbCancelDocument = findViewById(R.id.rbCancelDocument);
        rbCancelDocument.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rbContinueSignup.setVisibility(VISIBLE);
                findViewById(R.id.layoutMain).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutAddingNew).setVisibility(View.GONE);
            }
        });

        tvFileInfo = findViewById(R.id.tvFileInfo);
        pbLoader = findViewById(R.id.pbCertLoader);
        pbLoader.setVisibility(GONE);

        mainLayout = findViewById(R.id.layoutDocItems);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView) findViewById(R.id.tvCertificatesHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvDocumentDate)).setTypeface(font);
        ((TextView) findViewById(R.id.tvDocumentName)).setTypeface(font);
        ((TextView) findViewById(R.id.tvUploadInfo)).setTypeface(fontRegular);
        tvFileInfo.setTypeface(font);


        findViewById(R.id.layoutMain).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutAddingNew).setVisibility(View.GONE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.VISIBLE)
           load();
    }


    private void load() {
        pbLoader.setVisibility(VISIBLE);

        userService.getCertificates(new OnRequestResult<TrainerCertificateListDto>() {
            @Override
            public void requestComplete(TrainerCertificateListDto data) {
                pbLoader.setVisibility(GONE);
                if(!data.getError()) {
                    mainLayout.removeAllViewsInLayout();
                    for(TrainerCertificateDto cert : data.getCertificates())
                        addDocumentToView(cert);

                    addedCerts = data.getCertificates().size();
                }
            }

            @Override
            public void requestError(VolleyError ex) {

            }
        });
    }

    public void pickCert() {
        if (ContextCompat.checkSelfPermission((Activity)getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Document");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePicture});

        ((Activity)getContext()).startActivityForResult(chooserIntent, Constant.PickReason.DOCUMENT);
    }

    public void init(TextView tvSignupErrorMessage, RoundButton rbContinueSignup, DateTimeSelector dts) {
        this.tvSignupErrorMessage = tvSignupErrorMessage;
        this.rbContinueSignup = rbContinueSignup;
        this.dts = dts;

        dts.addListener("certificates", new OnDateTimePickedListener() {
            @Override
            public void pickedDate(int year, int month, int day, String formatted) {
                documentDate.setText(formatted);
            }

            @Override
            public void pickedTime(int hour, int minutes, String formatted) {

            }
        });
    }

    public void doUploadCert(Intent data) {
        try {
            ArrayList<Uri> certUris = new ArrayList<>();

            certUris.add(data.getData());

            for (int i = 0; i < certUris.size(); i++) {
                Uri selectedImage = certUris.get(i);
                inputStream = getContext().getContentResolver().openInputStream(selectedImage);

                tvSignupErrorMessage.setVisibility(View.GONE);
                fileSize = inputStream.available();

                if (fileSize > 10485760) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText("Document cannot exceed 10MB.");
                    return;
                }

                String realPath = ImageFilePath.getFileName((Activity)getContext(), data.getData());
                String[] pathParts = realPath.split("/");
                filename = pathParts[pathParts.length - 1];
                createNewDoc();
            }
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewDoc() {
        tvFileInfo.setText("File name: " + filename + "\nFile size: " + (fileSize / 1024) + " KB");
        rbContinueSignup.setVisibility(GONE);
        findViewById(R.id.layoutMain).setVisibility(View.GONE);
        findViewById(R.id.layoutAddingNew).setVisibility(View.VISIBLE);
    }

    private void submitCertificate(TrainerCertificateDto body) {
        rbSaveDocument.setLoading(true);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                    final TrainerCertificateDto uploadStatus = uploader.multipartRequest(Constant.ServiceEndpoints.USER_MANAGEMENT
                                    +  "/user-management/api/v1/classinfo/save/certificate",
                            getContext().getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""),
                            inputStream, "certificate", filename,
                            new GsonBuilder().create().toJson(body, TrainerCertificateDto.class),
                                TrainerCertificateDto.class);

                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rbSaveDocument.setLoading(false);

                            if (uploadStatus != null && !uploadStatus.getError()) {
                                addDocumentToView(uploadStatus);
                                rbContinueSignup.setVisibility(VISIBLE);
                                addedCerts++;
                            } else {
                                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                                tvSignupErrorMessage.setText(uploadStatus != null ? uploadStatus.getMessage() : "An unexpected error has occurred.");
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

    private void removeDocumentFromView(DocRow document) {
        mainLayout.removeView(document);
        addedCerts--;
    }

    private void addDocumentToView(TrainerCertificateDto uploadStatus) {
        DocRow newRow = new DocRow(getContext(), DocRow.RowType.DEFAULT, false, null);
        newRow.setData(uploadStatus.getId(), uploadStatus.getName(), "Awarded " + uploadStatus.getDate());
        mainLayout.addView(newRow);

        newRow.setRemoveListener(new OnRemoveByIdListener() {
            @Override
            public void remove(Long id) {
                newRow.setLoading(true);

                userService.removeCertificate(id, new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        newRow.setLoading(false);

                        if(data.getError()) {
                            tvSignupErrorMessage.setVisibility(VISIBLE);
                            tvSignupErrorMessage.setText(data.getMessage());
                        }
                        else
                            removeDocumentFromView(newRow);
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        newRow.setLoading(false);

                        tvSignupErrorMessage.setVisibility(VISIBLE);
                        tvSignupErrorMessage.setText("Couldn't remove certificate.");
                    }
                });
            }
        });

        findViewById(R.id.layoutMain).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutAddingNew).setVisibility(View.GONE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public int getAddedCerts() {
        return addedCerts;
    }
}
