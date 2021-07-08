package com.pilates.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.pilates.app.controls.SlidingPanel;
import com.pilates.app.controls.TestButton;
import com.pilates.app.controls.listeners.OnSlidingPanelEventListener;
import com.pilates.app.controls.listeners.OnTestButtonListener;
import com.pilates.app.handler.Timer;
import com.pilates.app.handler.listeners.OnTimerCompleteListener;
import com.pilates.app.model.Action;
import com.pilates.app.model.ActionBody;
import com.pilates.app.model.ActionType;
import com.pilates.app.model.ClassInitData;
import com.pilates.app.model.MediaStats;
import com.pilates.app.model.MediaType;
import com.pilates.app.model.UserRole;
import com.pilates.app.model.UserSession;
import com.pilates.app.service.PeerConnectionClient;
import com.pilates.app.ws.SignalingWebSocket;
import com.pilates.app.ws.SignalingWebSocketListener;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.pilates.app.util.Constant.HandlerMessage.CLASS_INITIALIZED;
import static com.pilates.app.util.Constant.HandlerMessage.CONNECT_DONE;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_NOT_EXISTS;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CONNECTION_ESTABLISHED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_MEDIA_STATS;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_OFFER_RECEIVED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_ON_HOLD;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_REMOTE_VIDEO;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_SWITCHED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_TRAINEE_JOINED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_TRAINEE_LEAVED;


public class MainActivity extends AppCompatActivity {
    private final UserRegistry userRegistry = UserRegistry.getInstance();
    private PeerConnectionClient peerConnectionClient = PeerConnectionClient.getInstance();
    private final SignalingWebSocket webSocket = SignalingWebSocket.getInstance();
    private final SignalingWebSocketListener webSocketListener = SignalingWebSocketListener.getInstance();

    private SurfaceViewRenderer localView;
    private SurfaceViewRenderer remoteView;
    private ProgressBar pbTime;
    boolean videoCaptureStopped = false;
    private float touchStartX;
    private float touchStartY;

    private Timer timer;
    private Timer sessionTimer;

    private SlidingPanel slidingPanel;
    private ProgressBar pbTimeCurrent;
    private TextView txtTimeRemaining;
    private TestButton tbAudio;
    private TestButton tbStream;

    private boolean classReadyToStart;
    private ImageView btnStartClass;
    private LinearLayout layoutButtonsTest;
    private UserSession user;
    private boolean classTimeToStart;
    private boolean classStarted;
    private Handler handler;
    private boolean isTrainer;

    public static Long trainerId;
    private int traineesInClass = 0;
    private Target currentLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleFullscreen();

        pbTime = findViewById(R.id.pbTimeSession);
        pbTimeCurrent = findViewById(R.id.pbTimeCurrent);
        localView = findViewById(R.id.svLocalView);
        remoteView = findViewById(R.id.svRemoteView);
        slidingPanel = findViewById(R.id.frameDisplaySettings);
        txtTimeRemaining = findViewById(R.id.txtTimeRemaining);
        layoutButtonsTest = findViewById(R.id.layoutButtonsTest);
        tbAudio = findViewById(R.id.tbAudio);
        tbStream = findViewById(R.id.tbStream);
        btnStartClass = findViewById(R.id.btnStartClass);

        btnStartClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classReadyToStart) {
                    peerConnectionClient.detachLocalStreamFromView(remoteView);
                    remoteView.clearImage();
                    layoutButtonsTest.setVisibility(View.GONE);
                    SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.CLASS_STARTED));
                }
            }
        });

        tbAudio.setListener(new OnTestButtonListener() {
            @Override
            public void clicked(TestButton.TestButtonState state) {
                final ActionBody body = ActionBody.newBuilder().withMediaStats(new MediaStats(MediaType.AUDIO)).build();
                webSocket.sendMessage(new Action(ActionType.STATS, body));
                // TODO start audio test
            }

            @Override
            public void progressCompleted() {
                // below sets result of test
                tbAudio.checkTestSuccess();
                checkClassReadyToStart();
            }

            @Override
            public void progressTick() {
                final ActionBody body = ActionBody.newBuilder().withMediaStats(new MediaStats(MediaType.AUDIO)).build();
                webSocket.sendMessage(new Action(ActionType.STATS, body));
            }
        });

        tbStream.setListener(new OnTestButtonListener() {
            @Override
            public void clicked(TestButton.TestButtonState state) {

                final ActionBody body = ActionBody.newBuilder().withMediaStats(new MediaStats(MediaType.VIDEO)).build();
                webSocket.sendMessage(new Action(ActionType.STATS, body));
                // TODO start stream test
            }

            @Override
            public void progressCompleted() {
                tbStream.checkTestSuccess();
                checkClassReadyToStart();
            }

            @Override
            public void progressTick() {
                final ActionBody body = ActionBody.newBuilder().withMediaStats(new MediaStats(MediaType.VIDEO)).build();
                webSocket.sendMessage(new Action(ActionType.STATS, body));
            }
        });

        slidingPanel.setListener(new OnSlidingPanelEventListener() {
            @Override
            public void changeLayout(String tag) {
                final float scale = getResources().getDisplayMetrics().density;
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                localView.setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(localView, 1f);
                ViewCompat.setTranslationZ(remoteView, 0f);

                if(tag.equals("me_small")) {
                    FrameLayout.LayoutParams rvLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    remoteView.setLayoutParams(rvLP);

                    FrameLayout.LayoutParams mlp = new FrameLayout.LayoutParams((int) (100 * scale + 0.5f),(int) (200 * scale + 0.5f));
                    mlp.topMargin = (int) (40 * scale + 0.5f);
                    mlp.rightMargin = (int) (30 * scale + 0.5f);
                    mlp.gravity = Gravity.RIGHT;

                    localView.setLayoutParams(mlp);

                    localView.setBackgroundResource(R.drawable.small_cam_view);
                    remoteView.setBackground(null);
                }
                else if(tag.equals("split")) {
                    FrameLayout.LayoutParams rvLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(displayMetrics.heightPixels * 0.7f));
                    rvLP.gravity = Gravity.TOP;
                    rvLP.topMargin = (int)(displayMetrics.heightPixels * 0.5f);
                    remoteView.setLayoutParams(rvLP);

                    FrameLayout.LayoutParams lvLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(displayMetrics.heightPixels * 0.5f));
                    lvLP.gravity = Gravity.TOP;
                    localView.setLayoutParams(lvLP);

                    localView.setBackground(null);
                    remoteView.setBackground(null);
                }
                else if(tag.equals("me_large")) {

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    FrameLayout.LayoutParams mlp = new FrameLayout.LayoutParams((int) (100 * scale + 0.5f), (int) (200 * scale + 0.5f));
                    mlp.topMargin = (int) (40 * scale + 0.5f);
                    mlp.rightMargin = (int) (30 * scale + 0.5f);
                    mlp.gravity = Gravity.RIGHT;

                    remoteView.setLayoutParams(mlp);
                    localView.setLayoutParams(layoutParams);

                    localView.setBackground(null);
                    remoteView.setBackgroundResource(R.drawable.small_cam_view);

                    ViewCompat.setTranslationZ(localView, 0f);
                    ViewCompat.setTranslationZ(remoteView, 1f);
                }
                else if(tag.equals("me_hidden")) {
                    localView.setVisibility(View.GONE);
                    FrameLayout.LayoutParams rvLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    remoteView.setLayoutParams(rvLP);
                    remoteView.setBackground(null);
                }
            }
        });

        findViewById(R.id.frameBotTrigger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchStartX = event.getX();SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.NEXT));
                    touchStartY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP && touchStartY > event.getY()) {
                    slidingPanel.showPanel();
                }
                return true;
            }
        });


        findViewById(R.id.frameTopTrigger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final UserSession user = userRegistry.getUser();

                if (Objects.equals(user.getRole(), UserRole.TRAINER)) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // hold
                        timer.stop();
                        final String connectorName = user.getConnectorName();
                        final Long connectorId = user.getConnectorId();
                        //timerView.setText("On hold with: " + connectorName);
//                        final ActionBody body = ActionBody.newBuilder().withUSerId(connectorId).build();
//                        SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.ON_HOLD, body));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // release
                        webSocket.sendMessage(new Action(ActionType.NEXT));
                    }
                }
                return true;
            }
        });

        user = userRegistry.getUser();

        timer = new Timer(this, pbTimeCurrent);
        timer.setListener(new OnTimerCompleteListener() {
            @Override
            public void completed() {
                final UserSession user = UserRegistry.getInstance().getUser();
                System.out.println("TIMER FINISHED");
                if (Objects.equals(user.getRole(), UserRole.TRAINER)) {
                    SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.NEXT));
                }
            }
        });
        sessionTimer = new Timer(this, pbTime, txtTimeRemaining);
        sessionTimer.setListener(new OnTimerCompleteListener() {
            @Override
            public void completed() {
                if(sessionTimer.getTag().equals("start_class")) {
                    classTimeToStart = true;
                    checkClassReadyToStart();
                }
            }
        });

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        ((TextView) findViewById(R.id.tvUserCount)).setTypeface(font);
        ((TextView)findViewById(R.id.tvTraineeName)).setTypeface(font);
        isTrainer = UserRole.valueOf(getSharedPreferences("woobody", MODE_PRIVATE)
                .getString("role", null)) == UserRole.TRAINER;

        findViewById(R.id.layoutTraineeAvatar).setVisibility(View.GONE);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                switch (what) {
                    case HANDLE_REMOTE_VIDEO:
                        Log.i("[MAIN UI HANDLER]", "Handling remote videoTrack");
                        VideoTrack remoteVideoTrack = (VideoTrack) msg.obj;
                        runOnUiThread(() -> remoteVideoTrack.addSink(remoteView));
                        break;

                    case HANDLE_CONNECTION_ESTABLISHED: {
                        ActionBody body = (ActionBody) msg.obj;
                        final LocalDateTime callShouldEndTime = body.getEndTime();
                        final LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

                        long remainingTime = Duration.between(currentTime, callShouldEndTime).get(ChronoUnit.SECONDS);
                        timer.start((remainingTime * 1000), 1000);

                        findViewById(R.id.layoutTraineeAvatar).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.tvTraineeName)).setText(body.getName());

                        showCallerAvatar(body.getAvatar());
                    }
                        break;

                    case HANDLE_TRAINEE_LEAVED:
                        if(classStarted) {
                            timer.stop();
                            pbTimeCurrent.setProgress(0);
                            webSocket.sendMessage(new Action(ActionType.NEXT));
                        }

                        traineesInClass--;
                        ((TextView) findViewById(R.id.tvUserCount)).setText(String.valueOf(traineesInClass));
                        break;

                    case HANDLE_TRAINEE_JOINED:
                        traineesInClass ++;
                        ((TextView)findViewById(R.id.tvUserCount)).setText(String.valueOf(traineesInClass));
                        break;

                    case HANDLE_ON_HOLD:
                        timer.stop();
                        final String connectorName = user.getConnectorName();
                        break;

                    case HANDLE_SWITCHED:
                        pbTimeCurrent.setProgress(0);
                        break;

                    case CLASS_INITIALIZED: {
                        ActionBody body = (ActionBody)msg.obj;
                        final LocalDateTime classStartTime = body.getStartTime();
                        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

                        long secondsToStart = Duration.between(now, classStartTime).get(ChronoUnit.SECONDS);
                        long mins = (secondsToStart) / 60;
                        long secondsToMin = secondsToStart - (mins * 60);

                        txtTimeRemaining.setText(String.format("%02d", mins) + ":" + String.format("%02d", secondsToMin));
                        pbTime.setProgress(100);

                        sessionTimer.setTag("start_class");
                        sessionTimer.start(secondsToStart * 1000, 1000);
                        traineesInClass = body.getTraineeCount();
                        ((TextView)findViewById(R.id.tvUserCount)).setText(String.valueOf(body.getTraineeCount()));
                }
                        break;

                    case HANDLE_MEDIA_STATS:
                        final MediaStats mediaStats = (MediaStats) msg.obj;
                        System.out.println("MEDIA STATS HANDLED: " + mediaStats.toString());

                        final MediaType mediaType = mediaStats.getMediaType();
                        long bytesReceived = mediaStats.getBytesReceived();
                        long packetsReceived = mediaStats.getPacketsReceived();
                        long packetsLost = mediaStats.getPacketsLost();
                        long remb = mediaStats.getRemb();

                        if(mediaType == MediaType.AUDIO && tbAudio.lastMediaStats != null) {
                            if(bytesReceived > tbAudio.lastMediaStats.getBytesReceived()
                            && packetsReceived > tbAudio.lastMediaStats.getPacketsReceived())
                                tbAudio.successfulTestCalls++;
                            else
                                tbAudio.failedTestCalls++;
                        }
                        else if(mediaType == MediaType.VIDEO && tbStream.lastMediaStats != null) {
                            if(bytesReceived > tbStream.lastMediaStats.getBytesReceived()
                                    && packetsReceived > tbStream.lastMediaStats.getPacketsReceived())
                                tbStream.successfulTestCalls++;
                            else
                                tbStream.failedTestCalls++;
                        }

                        if(mediaType == MediaType.AUDIO)
                            tbAudio.lastMediaStats = mediaStats;
                        else
                            tbStream.lastMediaStats = mediaStats;

                        break;

                    case HANDLE_CLASS_STARTED:
                        ClassInitData classData = (ClassInitData) msg.obj;

                        int mins = (int) ((classData.totalSeconds - classData.currentSeconds) / 60);
                        int secondsToMin = (int) (classData.totalSeconds - classData.currentSeconds) - (mins * 60);

                        txtTimeRemaining.setText(String.format("%02d", mins) + ":" + String.format("%02d", secondsToMin));
                        pbTime.setProgress((int) ((classData.currentSeconds / (double) classData.totalSeconds) * 100));

                        sessionTimer.start((classData.totalSeconds - classData.currentSeconds) * 1000, 1000);
                        findViewById(R.id.layoutButtonsTest).setVisibility(View.GONE);

                        classStarted = true;
                        break;
                    case HANDLE_CLASS_NOT_EXISTS:

                        break;
                    case CONNECT_DONE:
                        PeerConnectionClient.disposeCurrent();

                        CompletableFuture.runAsync(SignalingWebSocket::getInstance);
                        SignalingWebSocket.getInstance().recreate();

                        PeerConnectionClient.getInstance().setUiHandler(handler);
                        SignalingWebSocketListener.getInstance().setMainUIHandler(handler);

                        PeerConnectionClient peerConnectionClient = PeerConnectionClient.getInstance();
                        peerConnectionClient.initPeerConnectionFactory(getActivity());
                        peerConnectionClient.initPeerConnection(peerConnectionClient.initLocalMediaStream());
                        break;
                    case HANDLE_OFFER_RECEIVED:
                        // step 2, init trainer or trainee after offer is created
                        if(isTrainer) {
                            SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.INIT_TRAINER));
                        }
                        else {
                            final ActionBody newTraineeBody = ActionBody.newBuilder().withUSerId(trainerId).build();
                            SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.TRY_JOIN_CLASS, newTraineeBody));
                        }
                        break;
                    default:
                        Log.i("[MAIN UI HANDLER]", "No such operation");
                        break;
                }

            }
        };

        webSocketListener.setMainUIHandler(handler);
        peerConnectionClient.setUiHandler(handler);
        startStream();

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeerConnectionClient.getInstance().setUiHandler(null);
                SignalingWebSocketListener.getInstance().setMainUIHandler(null);

                PeerConnectionClient.disposeCurrent();
                SignalingWebSocket.getInstance().sendMessage(new Action(ActionType.STOP_COMMUNICATION));
                SignalingWebSocket.getInstance().stop();
                finish();
            }
        });

        pbTimeCurrent.setVisibility(View.GONE);
        findViewById(R.id.userCount).setVisibility(
                Objects.equals(user.getRole(), UserRole.TRAINER) ? View.VISIBLE : View.GONE);
        findViewById(R.id.tvUserCount).setVisibility(
                Objects.equals(user.getRole(), UserRole.TRAINER) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutButtonsTest).setVisibility(
                Objects.equals(user.getRole(), UserRole.TRAINER) ? View.VISIBLE : View.GONE);
    }

    private void showCallerAvatar(String avatar) {
        if(currentLoad != null) {
            Glide.with(this).clear(currentLoad);
            currentLoad = null;
        }

        findViewById(R.id.cvTraineeAvatar).setVisibility(View.GONE);
        findViewById(R.id.pbCallerAvatar).setVisibility(View.VISIBLE);

        currentLoad = Glide.with(this).load(avatar + "?rnd=" + Math.random())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        findViewById(R.id.cvTraineeAvatar).setVisibility(View.VISIBLE);
                        findViewById(R.id.pbCallerAvatar).setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        findViewById(R.id.cvTraineeAvatar).setVisibility(View.GONE);
                        findViewById(R.id.pbCallerAvatar).setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.imgTraineeAvatar)));
    }

    private void startStream() {
        // I commented this because it initiated in Start class activity (for trainer)
        // and Post trainee register activity (for trainee)
        // when we remove this activities it comes here

//        peerConnectionClient.initPeerConnectionFactory(this);
//        peerConnectionClient.initPeerConnection(peerConnectionClient.initLocalMediaStream());
        peerConnectionClient.initLocalAndRemoteViews(localView, remoteView);
        peerConnectionClient.startStream(1080, 1920, 30);


        peerConnectionClient.attachLocalStreamToView(localView);

        if(Objects.equals(user.getRole(), UserRole.TRAINER))
            peerConnectionClient.attachLocalStreamToView(remoteView);

        // show timer for current trainee
        pbTimeCurrent.setVisibility(View.VISIBLE);
    }

    private void checkClassReadyToStart() {
        classReadyToStart = tbAudio.testComplete && tbStream.testComplete; // classTimeToStart && <- add this to check timer too
        btnStartClass.setImageResource(classReadyToStart ? R.drawable.btn_start : R.drawable.btn_start_inactive);
    }

    @Override
    protected void onPause() {
        System.out.println("ON PAUSE");
        super.onPause();

        if (!videoCaptureStopped) {
            peerConnectionClient.stopStream();
            videoCaptureStopped = true;
            System.out.println("STOPPED CAPTURE VIDEO");

        }
    }

    @Override
    protected void onResume() {
        System.out.println("ON RESUME");
        super.onResume();
        if (videoCaptureStopped) {
            peerConnectionClient.resumeStream();
            videoCaptureStopped = false;
            System.out.println("STARTING CAPTURE VIDEO");
        }


        ToggleFullscreen();
    }

    public void ToggleFullscreen() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private MainActivity getActivity() {
        return this;
    }

    @Override
    public void onBackPressed() {
    }
}
