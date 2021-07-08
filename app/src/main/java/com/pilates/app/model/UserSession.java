package com.pilates.app.model;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;


public class UserSession {

    private final Long userId;
    private final String name;
    private final UserRole role;

    private final List<IceCandidate> remoteCandidates = new ArrayList<>();

    private String trainerInfoId;
    private Long connectorId;
    private String connectorName;
    private boolean init;
    private SessionDescription answer;

    public UserSession(final Long userId, final String name, final UserRole role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.init = true;
    }




    public void addRemoteCandidate(final IceCandidate candidate) {
        remoteCandidates.add(candidate);
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public List<IceCandidate> getRemoteCandidates() {
        return remoteCandidates;
    }

    public void  init() {
        this.init = true;
    }

    public SessionDescription getAnswer() {
        return answer;
    }

    public void setAnswer(SessionDescription answer) {
        this.answer = answer;
    }

//    public void setWsSession(WebSocket wsSession) {
//        this.wsSession = wsSession;
//    }
    public boolean isInit() {
        return init;
    }


    public Long getUserId() {
        return userId;
    }

    public String getTrainerInfoId() {
        return trainerInfoId;
    }

    public void setConnectorInfoId(String trainerInfoId) {
        this.trainerInfoId = trainerInfoId;
    }
}
