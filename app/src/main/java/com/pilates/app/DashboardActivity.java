package com.pilates.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.pilates.app.controls.ClassList;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.listeners.OnClassInfoActionListener;
import com.pilates.app.controls.listeners.OnClassListSelectedListener;
import com.pilates.app.controls.listeners.OnOperationCompleteWithIdListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.controls.screens.ClassInfoPanel;
import com.pilates.app.model.Action;
import com.pilates.app.model.ActionBody;
import com.pilates.app.model.ActionType;
import com.pilates.app.model.UserRole;
import com.pilates.app.model.UserSession;
import com.pilates.app.model.dto.ClassListingInfoDto;
import com.pilates.app.model.dto.DashboardInfoDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.service.PeerConnectionClient;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.ws.SignalingWebSocket;
import com.pilates.app.ws.SignalingWebSocketListener;

import java.util.concurrent.CompletableFuture;

import static com.pilates.app.util.Constant.HandlerMessage.CONNECT_DONE;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_NOT_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_OFFER_RECEIVED;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvLogout;
    private UserDataService userService;
    private ProgressBar pbDashboardInfo;
    private boolean trainerApproved;
    private ClassList clStudioOwn;
    private ClassList clStudioHistory;
    private ClassInfoPanel classInfoPanel;
    private boolean isTrainer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userService = new UserDataService(this);
        isTrainer = UserRole.valueOf(getSharedPreferences("woobody", MODE_PRIVATE)
                .getString("role", null)) == UserRole.TRAINER;

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                switch (what) {
                    case HANDLE_CLASS_STARTED:
                        classInfoPanel.getClassInfoClassLoader().setVisibility(View.GONE);

                        final ActionBody traineeBody = ActionBody.newBuilder().withUSerId(classInfoPanel.getData().getTrainerId()).build();
                        SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.CONNECT_TO, traineeBody));

                        final UserSession user = UserRegistry.getInstance().getUser();
                        user.setConnectorName(classInfoPanel.getData().getTrainerName());
                        user.setConnectorId(classInfoPanel.getData().getTrainerId());

                        startActivity(new Intent(getActivity(), MainActivity.class));
                        break;
                    case HANDLE_CLASS_NOT_STARTED:
                        classInfoPanel.initPreClassJoin();
                        break;
                    case CONNECT_DONE:
                        // step 1, create offer after connection
                        if(classInfoPanel.getData() == null)
                            break;

                        PeerConnectionClient peerConnectionClient = PeerConnectionClient.getInstance();
                        peerConnectionClient.initPeerConnectionFactory(getActivity());
                        peerConnectionClient.initPeerConnection(peerConnectionClient.initLocalMediaStream());
                        break;
                    case HANDLE_OFFER_RECEIVED:
                        classInfoPanel.getClassInfoClassLoader().setVisibility(View.GONE);

                        // step 2, init trainer or trainee after offer is created
                        if(isTrainer) {
                            SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.INIT_TRAINER));
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                        else {
                            final ActionBody newTraineeBody = ActionBody.newBuilder().withUSerId(classInfoPanel.getData().getTrainerId()).build();
                            SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.TRY_JOIN_CLASS, newTraineeBody));
                        }
                        break;
                }
            }
        };

        // set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");

        ((TextView)findViewById(R.id.tvMyStudio)).setTypeface(fontBold);
        ((TextView)findViewById(R.id.tvNextClass)).setTypeface(font);
        ((TextView)findViewById(R.id.tvDashboardInfoTitle)).setTypeface(font);
        ((TextView)findViewById(R.id.tvDashboardInfoValue)).setTypeface(font);
        ((TextView)findViewById(R.id.tvStudioTitle)).setTypeface(fontBold);

        tvLogout = ((TextView)findViewById(R.id.tvLogout));
        tvLogout.setTypeface(fontBold);

        findViewById(R.id.hbAddClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!trainerApproved)
                    return;

                startActivity(new Intent(getActivity(), AddClassActivity.class));
            }
        });

        findViewById(R.id.hbBrowseClasses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ClassListActivity.class));
            }
        });

        findViewById(R.id.tvLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("woobody", MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.remove("accessToken");
                edit.commit();

                finish();
            }
        });

        findViewById(R.id.frameMyStudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameStudioClasses).setVisibility(View.VISIBLE);
                clStudioOwn.load();
            }
        });

        findViewById(R.id.imgIconBackStudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameStudioClasses).setVisibility(View.GONE);
            }
        });

        pbDashboardInfo = findViewById(R.id.pbDashboardInfo);
        classInfoPanel = findViewById(R.id.classInfoPanel);
        classInfoPanel.setVisibility(View.GONE);
        setupStudio();

        findViewById(R.id.layoutTrainerDashButtons).setVisibility(isTrainer ? View.VISIBLE :View.GONE);
        findViewById(R.id.layoutTraineeDashButtons).setVisibility(!isTrainer ? View.VISIBLE :View.GONE);

        SignalingWebSocket.token = getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", null);
    }

    void setupStudio() {
        ((RoundButtonGroup)findViewById(R.id.rbgSelectStudioList)).setSelectedValue("own");
        ((RoundButtonGroup)findViewById(R.id.rbgSelectStudioList)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(button.getValue().equals("own")) {
                    clStudioOwn.setVisibility(View.VISIBLE);
                    clStudioHistory.setVisibility(View.GONE);
                }
                else {
                    clStudioHistory.setVisibility(View.VISIBLE);
                    clStudioOwn.setVisibility(View.GONE);
                }
            }

            @Override
            public void removed(RoundButton button) {

            }
        });

        clStudioOwn = findViewById(R.id.clStudioOwn);
        clStudioHistory = findViewById(R.id.clStudioHistory);

        clStudioOwn.setVisibility(View.VISIBLE);
        clStudioHistory.setVisibility(View.GONE);

        clStudioOwn.setClassSelectedListener(new OnClassListSelectedListener() {
            @Override
            public void selected(ClassListingInfoDto item) {
                classInfoPanel.setMode("own", isTrainer);
                classInfoPanel.setClassId(item.getId(), false);
                classInfoPanel.setVisibility(View.VISIBLE);
            }
        });

        clStudioHistory.setClassSelectedListener(new OnClassListSelectedListener() {
            @Override
            public void selected(ClassListingInfoDto item) {
                classInfoPanel.setMode("history", isTrainer);
                classInfoPanel.setClassId(item.getId(), false);
                classInfoPanel.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.frameStudioClasses).setVisibility(View.GONE);

        classInfoPanel.setCompleteListener(new OnClassInfoActionListener() {
            @Override
            public void main(String action) {
                if(action.equals("starting")) {
                    // start class
                    connectAndStartClass();
                }
                else {
                    // cancel class
                    classInfoPanel.setVisibility(View.GONE);
                    clStudioOwn.load();
                }
            }

            @Override
            public void secondary() {
                classInfoPanel.setVisibility(View.GONE);
            }
        });
    }

    private void connectAndStartClass() {
        PeerConnectionClient.disposeCurrent();

        SignalingWebSocket.classId = classInfoPanel.getData().getId().toString();
        CompletableFuture.runAsync(SignalingWebSocket::getInstance);
        SignalingWebSocket.getInstance().recreate();

        PeerConnectionClient.getInstance().setUiHandler(handler);
        SignalingWebSocketListener.getInstance().setMainUIHandler(handler);

        MainActivity.trainerId = classInfoPanel.getData().getTrainerId();
        final UserSession user = UserRegistry.getInstance().getUser();
        final ActionBody body = ActionBody.newBuilder()
                .withUSerId(user.getUserId())
                .withName(user.getName())
                .withToken(getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", null))
                .withRole(user.getRole())
                .withClassId(classInfoPanel.getData().getId().toString()).build();
        SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.CONNECT, body));
    }

    private DashboardActivity getActivity() {
        return this;
    }

    @Override
    public void onResume(){
        super.onResume();

        findViewById(R.id.layoutDashboardInfo).setVisibility(View.GONE);
        findViewById(R.id.arApproved).setVisibility(View.GONE);
        findViewById(R.id.frameMyStudio).setVisibility(View.GONE);
        pbDashboardInfo.setVisibility(View.VISIBLE);

        userService.getDashboardInfo(new OnRequestResult<DashboardInfoDto>() {
            @Override
            public void requestComplete(DashboardInfoDto data) {
                pbDashboardInfo.setVisibility(View.GONE);

                findViewById(R.id.layoutDashboardInfo).setVisibility(!isTrainer || data.isTrainerApproved() ? View.VISIBLE : View.GONE);
                findViewById(R.id.arApproved).setVisibility(!isTrainer || data.isTrainerApproved() ? View.GONE : View.VISIBLE);
                findViewById(R.id.frameMyStudio).setVisibility(View.VISIBLE);

                trainerApproved = data.isTrainerApproved() || !isTrainer;
                findViewById(R.id.hbAddClass).setAlpha(trainerApproved ? 1f : 0.5f);

                if(isTrainer) {
                    ((TextView)findViewById(R.id.tvDashboardInfoTitle)).setText(getResources().getString(R.string.label_dashboard_title_trainer));
                    ((TextView)findViewById(R.id.tvDashboardInfoValue)).setText("€" + data.getCurrentBalance());
                } else if(data.isHasNextClass()) {
                    ((TextView)findViewById(R.id.tvDashboardInfoTitle)).setText(getResources().getString(R.string.label_dashboard_title_trainee));
                    ((TextView)findViewById(R.id.tvDashboardInfoValue)).setText(data.getNextClassStartDateTime());
                } else {
                    ((TextView)findViewById(R.id.tvDashboardInfoTitle)).setText("");
                    ((TextView)findViewById(R.id.tvDashboardInfoValue)).setText(getResources().getString(R.string.label_dashboard_greeting));
                }

                if(data.isHasNextClass()) {
                    long hour = data.getMinutesBeforeNextClass() / 60;
                    long minuteNormalized = data.getMinutesBeforeNextClass() - (hour * 60);

                    if(hour > 0 && hour < 12)
                        ((TextView) findViewById(R.id.tvNextClass)).setText("NEXT CLASS IN: " + hour + "h " + minuteNormalized + "m");
                    else if(hour == 0)
                        ((TextView) findViewById(R.id.tvNextClass)).setText("NEXT CLASS IN: " + minuteNormalized + "m");
                    else
                        ((TextView) findViewById(R.id.tvNextClass)).setText("NEXT CLASS: " + data.getNextClassStartDateTime());
                }
                else {
                    ((TextView) findViewById(R.id.tvNextClass)).setText(getResources().getString(R.string.label_dashboard_classes));
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                pbDashboardInfo.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(classInfoPanel.getVisibility() == View.VISIBLE)
            classInfoPanel.setVisibility(View.GONE);
        if(findViewById(R.id.frameStudioClasses).getVisibility() == View.VISIBLE)
            findViewById(R.id.frameStudioClasses).setVisibility(View.GONE);
    }
}
