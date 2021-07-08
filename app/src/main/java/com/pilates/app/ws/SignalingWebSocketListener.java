package com.pilates.app.ws;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.pilates.app.model.MediaStats;
import com.pilates.app.service.PeerConnectionClient;
import com.pilates.app.UserRegistry;
import com.pilates.app.model.Action;
import com.pilates.app.model.ActionBody;
import com.pilates.app.model.ActionType;
import com.pilates.app.model.Candidate;
import com.pilates.app.model.ClassInitData;
import com.pilates.app.model.UserSession;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.RequiresApi;

import static com.pilates.app.model.ActionType.CLASS_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.CLASS_INITIALIZED;
import static com.pilates.app.util.Constant.HandlerMessage.CONNECT_DONE;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_NOT_EXISTS;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_NOT_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CLASS_STARTED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_CONNECTION_ESTABLISHED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_MEDIA_STATS;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_OFFER_RECEIVED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_ON_HOLD;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_SWITCHED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_TRAINEE_JOINED;
import static com.pilates.app.util.Constant.HandlerMessage.HANDLE_TRAINEE_LEAVED;

public class SignalingWebSocketListener extends WebSocketAdapter {

    private static SignalingWebSocketListener instance = new SignalingWebSocketListener();
    private PeerConnectionClient peerConnectionClient = PeerConnectionClient.getInstance();
    private final UserRegistry userRegistry = UserRegistry.getInstance();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            final ZonedDateTime parsed = ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString(), formatter.withZone(ZoneId.of("UTC")));
            return parsed.toLocalDateTime();
        }
    }).create();
    private final AtomicBoolean failedConnection = new AtomicBoolean(true);

//    private PeerConnectionClient peerConnection;
    private Handler mainUIHandler;

    private SignalingWebSocketListener() {
    }

    public static SignalingWebSocketListener getInstance() {
        if (instance == null) {
            instance = new SignalingWebSocketListener();
        }

        return instance;
    }

    private void log(String s) {
        Log.i("[WS] ", s);
    }


    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        log("Web socket connection established");
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        log("Web socket connection error " + exception.getMessage());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        log("Received message " + text);

        try {
            final Action action = gson.fromJson(text, Action.class);
            final ActionType type = action.getType();
            final ActionBody body = action.getBody();

            switch (type) {
                case ICE_EXCHANGE:

                    final Candidate candidate = body.getCandidate();
                    peerConnectionClient.addIceCandidate(candidate);
                    break;

                case ANSWER:

                    final String answer = body.getAnswer();
                    peerConnectionClient.setRemoteDescription(answer);
                    break;

                case INITIALIZED:
                    final Message classInitializedMessage = mainUIHandler.obtainMessage(CLASS_INITIALIZED,
                            body);
                    mainUIHandler.sendMessage(classInitializedMessage);
                    break;

                case TRAINERS:
                    final Map<Long, String> trainees = body.getTrainers();
                    userRegistry.putAllTrainers(trainees);
                    break;

                case ADD_TRAINER:
                    final Long trainerId = body.getUserId();
                    final String trainerName = body.getName();
                    userRegistry.putTrainer(trainerId, trainerName);
                    break;

                case REMOVE_TRAINER:
                    userRegistry.removeTrainer(body.getUserId() + "");
                    break;

                case ADD_TRAINEE:
                    final Long traineeId = body.getUserId();
                    final String traineeName = body.getName();
                    userRegistry.addTrainee(traineeId, traineeName);
                    mainUIHandler.sendEmptyMessage(HANDLE_TRAINEE_JOINED);
                    break;

                case REMOVE_TRAINEE:
                    userRegistry.removeTrainee(body.getUserId());
                    mainUIHandler.sendEmptyMessage(HANDLE_TRAINEE_LEAVED);
                    break;

                case CALL_IN_PROGRESS:
                    final Long connectorId = body.getUserId();
                    final String connectorName = body.getName();
                    final UserSession user = userRegistry.getUser();

                    final Message handleConnectionEstablishedMessage = mainUIHandler.obtainMessage(HANDLE_CONNECTION_ESTABLISHED,
                            body);

                    user.setConnectorId(connectorId);
                    user.setConnectorName(connectorName);

                    mainUIHandler.sendMessage(handleConnectionEstablishedMessage);
                    break;

                case ON_HOLD:
                    mainUIHandler.sendEmptyMessage(HANDLE_ON_HOLD);
                    break;
                case SWITCHED:
                    mainUIHandler.sendEmptyMessage(HANDLE_SWITCHED);
                    break;
                case STATS:
                    final MediaStats mediaStats = body.getMediaStats();
                    final Message mediaStatsMessage = mainUIHandler.obtainMessage(HANDLE_MEDIA_STATS, mediaStats);
                    mainUIHandler.sendMessage(mediaStatsMessage);
                    break;
                case CLASS_STARTED:
                    final LocalDateTime scClassStartTime = body.getStartTime();
                    final LocalDateTime scClassEndTime = body.getEndTime();
                    final LocalDateTime scNow = LocalDateTime.now(ZoneOffset.UTC);

                    final ClassInitData startData = new ClassInitData();
                    startData.currentSeconds = Duration.between(scNow, scClassEndTime).get(ChronoUnit.SECONDS);
                    startData.totalSeconds = Duration.between(scClassStartTime, scClassEndTime).get(ChronoUnit.SECONDS);

                    final Message classStartedMessage = mainUIHandler.obtainMessage(HANDLE_CLASS_STARTED, startData);
                    mainUIHandler.sendMessage(classStartedMessage);
                    break;
                case CLASS_NOT_STARTED:
                    mainUIHandler.sendEmptyMessage(HANDLE_CLASS_NOT_STARTED);
                    break;
                case CONNECT:
                    mainUIHandler.sendEmptyMessage(CONNECT_DONE);
                    break;
                case CLASS_NOT_EXISTS:
                    mainUIHandler.sendEmptyMessage(HANDLE_CLASS_NOT_EXISTS);
                    break;
                case OFFER_RECEIVED:
                    mainUIHandler.sendEmptyMessage(HANDLE_OFFER_RECEIVED);
                    break;
                default:
                    log("No such action " + action);
                    break;

            }
        }
        catch (Exception ex) {
            int a = 5;
            int b = a + 3;
            return;
        }

    }


    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        log("Web socket disconnected");
    }


    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        if (Objects.equals(newState, WebSocketState.CLOSED)) {
            failedConnection.set(true);
        } else if (Objects.equals(newState, WebSocketState.OPEN)) {
            failedConnection.set(false);
        }
    }

    public boolean isConnectionFailed() {
        return failedConnection.get();
    }

    public void setMainUIHandler(Handler mainUIHandler) {
        this.mainUIHandler = mainUIHandler;
    }
}
