package com.pilates.app.controls.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.pilates.app.R;
import com.pilates.app.controls.DocRow;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.listeners.OnOperationCompleteListener;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.model.dto.SelectionItemDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PilatesTypesSetup extends FrameLayout {

    private final ProgressBar pbLoader;
    private final ProgressBar pbLoaderSelection;
    private UserDataService userService;
    private TextView tvSignupErrorMessage;
    private RoundButton rbAddPilatesType;
    private RoundButton rbCancelAddType;
    private RoundButton rbContinueSignup;
    private OnOperationCompleteListener completeListener;

    private final LinearLayout layoutItems;
    private final LinearLayout layoutSelectionItems;

    private ArrayList<SelectionItemDto> selectedTypes;

    public PilatesTypesSetup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_pilates_types, this, true);

        selectedTypes = new ArrayList<>();
        userService = new UserDataService(context);

        rbAddPilatesType = findViewById(R.id.rbAddPilatesType);
        rbAddPilatesType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addNewType();
            }
        });

        rbCancelAddType = findViewById(R.id.rbCancelAddType);
        rbCancelAddType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rbContinueSignup.setVisibility(VISIBLE);
                findViewById(R.id.layoutMain).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutAddNew).setVisibility(View.GONE);
            }
        });

        pbLoader = findViewById(R.id.pbTypesLoader);
        pbLoader.setVisibility(GONE);

        pbLoaderSelection = findViewById(R.id.pbTypesSelectionLoader);
        pbLoaderSelection.setVisibility(GONE);

        layoutItems = findViewById(R.id.layoutItems);
        layoutSelectionItems = findViewById(R.id.layoutSelectionItems);

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        ((TextView) findViewById(R.id.tvPilatesHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvPilatesTypesSearchInfo)).setTypeface(fontRegular);
        ((TextView) findViewById(R.id.tvPilatesTypesInfo)).setTypeface(fontRegular);


        findViewById(R.id.layoutMain).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutAddNew).setVisibility(View.GONE);
    }

    private void addNewType() {
        rbContinueSignup.setVisibility(View.GONE);

        findViewById(R.id.layoutMain).setVisibility(View.GONE);
        findViewById(R.id.layoutAddNew).setVisibility(View.VISIBLE);

        layoutSelectionItems.removeAllViewsInLayout();
        pbLoaderSelection.setVisibility(VISIBLE);

        userService.getPilatesTypesSelection(new OnRequestResult<SelectionListDto>() {
            @Override
            public void requestComplete(SelectionListDto data) {
                pbLoaderSelection.setVisibility(GONE);
                layoutSelectionItems.removeAllViewsInLayout();

                for(SelectionItemDto type : data.getData()) {
                    boolean alreadyAdded = false;

                    for(SelectionItemDto addedType : selectedTypes) {
                        if(addedType.getId() == type.getId()) {
                            alreadyAdded = true;
                            break;
                        }
                    }

                    if(alreadyAdded)
                        continue;

                    DocRow newRow = new DocRow(getContext(), DocRow.RowType.SIMPLE, true, "SELECT");
                    newRow.setData(Long.parseLong(type.getId()), type.getName().toString(), "");
                    newRow.setRemoveListener(new OnRemoveByIdListener() {
                        @Override
                        public void remove(Long id) {
                            selectedTypes.add(type);
                            layoutSelectionItems.removeView(newRow);
                            showLoadedTypes();
                        }
                    });
                    layoutSelectionItems.addView(newRow);
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoaderSelection.setVisibility(GONE);
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

        userService.getPilatesTypes(new OnRequestResult<SelectionListDto>() {
            @Override
            public void requestComplete(SelectionListDto data) {
                pbLoader.setVisibility(GONE);
                selectedTypes = data.getData();
                showLoadedTypes();
            }

            @Override
            public void requestError(VolleyError ex) {
                pbLoader.setVisibility(GONE);
            }
        });
    }

    private void showLoadedTypes() {
        layoutItems.removeAllViewsInLayout();

        for(SelectionItemDto type : selectedTypes) {
            DocRow newRow = new DocRow(getContext(), DocRow.RowType.SIMPLE, false, null);
            newRow.setData(Long.parseLong(type.getId()), type.getName().toString(), "");
            newRow.setRemoveListener(new OnRemoveByIdListener() {
                @Override
                public void remove(Long id) {
                    selectedTypes.remove(type);
                    layoutItems.removeView(newRow);
                }
            });
            layoutItems.addView(newRow);
        }
    }

    public void init(TextView tvSignupErrorMessage, RoundButton rbContinueSignup) {
        this.tvSignupErrorMessage = tvSignupErrorMessage;
        this.rbContinueSignup = rbContinueSignup;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public ArrayList<SelectionItemDto> getSelectedTypes() {
        return selectedTypes;
    }

    public void saveTypes() {
        SelectionListDto list = new SelectionListDto();
        list.setData(selectedTypes);

        rbContinueSignup.setLoading(true);
        tvSignupErrorMessage.setVisibility(GONE);

        userService.savePilatesTypes(list, new OnRequestResult<StatusMessageDto>() {
            @Override
            public void requestComplete(StatusMessageDto data) {
                if(!data.getError()) {
                    rbContinueSignup.setLoading(false);
                    completeListener.complete();
                }
                else
                {
                    tvSignupErrorMessage.setVisibility(VISIBLE);
                    tvSignupErrorMessage.setText(data.getMessage());
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                rbContinueSignup.setLoading(false);
                tvSignupErrorMessage.setVisibility(VISIBLE);
                tvSignupErrorMessage.setText("Couldn't save pilates types.");
            }
        });
    }

    public void setCompleteListener(OnOperationCompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}
