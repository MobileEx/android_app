package com.pilates.app.controls.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.pilates.app.R;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnButtonClickListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.dto.ClassDetailsDto;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.enums.StreamingType;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.SelectionsHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class ClassDetailsSetup extends FrameLayout {

    private final ProgressBar pbLoader;
    private final TextView tvError;
    private UserDataService userService;
    private OnOperationCompleteWithIdListener completeListener;
    private ClassDetailsDto specData;
    private Long classId = (long)0;
    private OnButtonClickListener addImageListener;

    public ClassDetailsSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_class_details, this, true);

        userService = new UserDataService(context);

        pbLoader = findViewById(R.id.pbDetailsLoader);
        pbLoader.setVisibility(GONE);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView)findViewById(R.id.tvClassDetails)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassImages)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassDescription)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassPilatesType)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassMainPurposes)).setTypeface(font);
        ((TextView)findViewById(R.id.tvClassRequirements)).setTypeface(font);

        tvError =((TextView)findViewById(R.id.tvError));
        tvError.setTypeface(font);
        tvError.setVisibility(GONE);

        RoundButton rbContinue = findViewById(R.id.rbClassDetailsContinue);
        rbContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)findViewById(R.id.classDescription)).getText() != null)
                    specData.setDescription(((EditText)findViewById(R.id.classDescription)).getText().toString());
                if(((EditText)findViewById(R.id.classRequirements)).getText() != null)
                    specData.setRequirements(((EditText)findViewById(R.id.classRequirements)).getText().toString());

                tvError.setVisibility(GONE);
                rbContinue.setLoading(true);
                userService.saveClassDetails(specData, new OnRequestResult<StatusMessageDto>() {
                    @Override
                    public void requestComplete(StatusMessageDto data) {
                        rbContinue.setLoading(false);
                        if(data.getError()) {
                            tvError.setText(data.getMessage());
                            tvError.setVisibility(VISIBLE);
                        }
                        else
                            completeListener.complete(Long.parseLong(data.getMessage()));
                    }

                    @Override
                    public void requestError(VolleyError ex) {
                        rbContinue.setLoading(false);
                        tvError.setText("Couldn't save details.");
                        tvError.setVisibility(VISIBLE);
                    }
                });
            }
        });

        findViewById(R.id.rbManageClassImages).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageListener.clicked(null);
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
        pbLoader.setVisibility(VISIBLE);
        findViewById(R.id.layoutFields).setVisibility(GONE);

        userService.getClassDetails(classId, new OnRequestResult<ClassDetailsDto>() {
            @Override
            public void requestComplete(ClassDetailsDto data) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
                specData = data;

                setupClassSpec();
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoader.setVisibility(GONE);
                findViewById(R.id.layoutFields).setVisibility(VISIBLE);
            }
        });
    }

    private void setupClassSpec() {

        ArrayList<SelectionListDto> pilatesRows = SelectionsHelper.getMultiRowSelection(specData.getPilatesTypesOptions());

        ((RoundButtonGroup)findViewById(R.id.rbgSelectPilatesTypesOne)).setButtonSource(pilatesRows.get(0));
        ((RoundButtonGroup)findViewById(R.id.rbgSelectPilatesTypesOne)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                specData.getPilatesTypes().add(SelectionsHelper.findById(button.getValue(), specData.getPilatesTypesOptions()));
            }

            @Override
            public void removed(RoundButton button) {
                specData.getPilatesTypes().remove(SelectionsHelper.findById(button.getValue(), specData.getPilatesTypes()));
            }
        });

        if(pilatesRows.size() > 1) {
            ((RoundButtonGroup) findViewById(R.id.rbgSelectPilatesTypesTwo)).setVisibility(VISIBLE);
            ((RoundButtonGroup) findViewById(R.id.rbgSelectPilatesTypesTwo)).setButtonSource(pilatesRows.get(1));
            ((RoundButtonGroup) findViewById(R.id.rbgSelectPilatesTypesTwo)).setButtonListener(new OnRoundButtonClickListener() {
                @Override
                public void clicked(RoundButton button) {
                    specData.getPilatesTypes().add(SelectionsHelper.findById(button.getValue(), specData.getPilatesTypesOptions()));
                }

                @Override
                public void removed(RoundButton button) {
                    specData.getPilatesTypes().remove(SelectionsHelper.findById(button.getValue(), specData.getPilatesTypes()));
                }
            });
        } else {
            ((RoundButtonGroup) findViewById(R.id.rbgSelectPilatesTypesTwo)).setVisibility(GONE);
        }

        ArrayList<SelectionListDto> purposesRows = SelectionsHelper.getMultiRowSelection(specData.getPurposesOptions());

        ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeOne)).setButtonSource(purposesRows.get(0));
        ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeOne)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                specData.getPurposes().add(SelectionsHelper.findById(button.getValue(), specData.getPurposesOptions()));
            }

            @Override
            public void removed(RoundButton button) {
                specData.getPurposes().remove(SelectionsHelper.findById(button.getValue(), specData.getPurposes()));
            }
        });

        if(purposesRows.size() > 1) {
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeTwo)).setVisibility(VISIBLE);
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeTwo)).setButtonSource(purposesRows.get(1));
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeTwo)).setButtonListener(new OnRoundButtonClickListener() {
                @Override
                public void clicked(RoundButton button) {
                    specData.getPurposes().add(SelectionsHelper.findById(button.getValue(), specData.getPurposesOptions()));
                }

                @Override
                public void removed(RoundButton button) {
                    specData.getPurposes().remove(SelectionsHelper.findById(button.getValue(), specData.getPurposes()));
                }
            });
        }
        else
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeTwo)).setVisibility(GONE);


        if(purposesRows.size() > 2) {
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeThree)).setVisibility(VISIBLE);
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeThree)).setButtonSource(purposesRows.get(2));
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeThree)).setButtonListener(new OnRoundButtonClickListener() {
                @Override
                public void clicked(RoundButton button) {
                    specData.getPurposes().add(SelectionsHelper.findById(button.getValue(), specData.getPurposesOptions()));
                }

                @Override
                public void removed(RoundButton button) {
                    specData.getPurposes().remove(SelectionsHelper.findById(button.getValue(), specData.getPurposes()));
                }
            });
        }
        else
            ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeThree)).setVisibility(GONE);

        ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeOne)).setSelectedValues(SelectionsHelper.getIds(specData.getPurposes()));
        ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeTwo)).setSelectedValues(SelectionsHelper.getIds(specData.getPurposes()));
        ((RoundButtonGroup)findViewById(R.id.rbgSelectMainPurposeThree)).setSelectedValues(SelectionsHelper.getIds(specData.getPurposes()));

        ((RoundButtonGroup)findViewById(R.id.rbgSelectPilatesTypesOne)).setSelectedValues(SelectionsHelper.getIds(specData.getPilatesTypes()));
        ((RoundButtonGroup)findViewById(R.id.rbgSelectPilatesTypesTwo)).setSelectedValues(SelectionsHelper.getIds(specData.getPilatesTypes()));

        ((EditText)findViewById(R.id.classDescription)).setText(specData.getDescription());
        ((EditText)findViewById(R.id.classRequirements)).setText(specData.getRequirements());
    }

    public void setCompleteListener(OnOperationCompleteWithIdListener completeListener) {
        this.completeListener = completeListener;
    }

    public void setClassId(Long id) {
        classId = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setAddImageListener(OnButtonClickListener addImageListener) {
        this.addImageListener = addImageListener;
    }

    public void setGalleryImageCount(int size) {
        ((RoundButton)findViewById(R.id.rbManageClassImages)).setButtonBackground(size > 0 ?
                R.drawable.button_bg_green : R.drawable.button_bg_gray);
        ((RoundButton)findViewById(R.id.rbManageClassImages)).setButtonTextColor(size > 0 ?
                getContext().getResources().getColor(R.color.wooLabelGreen, null) :
                getContext().getResources().getColor(R.color.labelDark, null));
        ((RoundButton)findViewById(R.id.rbManageClassImages)).setIconResource(size > 0 ? R.drawable.icon_complete : R.drawable.icon_add);
    }
}
