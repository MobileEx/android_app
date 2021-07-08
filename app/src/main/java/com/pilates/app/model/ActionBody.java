package com.pilates.app.model;

import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.Map;


public class ActionBody {
    //ws id
    private String id;
    private Long userId;
    private String name;
    private UserRole role;
    private String offer;
    private String answer;
    private Candidate candidate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String token;
    private String classId;
    private Integer traineeCount;
    private String avatar;

    //wsid, name
    private Map<Long, String> trainers;
    private MediaStats mediaStats;

    /*default*/ ActionBody(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.role = builder.role;
        this.offer = builder.offer;
        this.answer = builder.answer;
        this.candidate = builder.candidate;
        this.trainers = builder.trainers;
        this.userId = builder.userId;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.mediaStats = builder.mediaStats;
        this.token = builder.token;
        this.classId = builder.classId;
        this.traineeCount = builder.traineeCount;
        this.avatar = builder.avatar;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ActionBody.class);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Map<Long, String> getTrainers() {
        return trainers;
    }

    public void setTrainers(Map<Long, String> trainers) {
        this.trainers = trainers;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public MediaStats getMediaStats() {
        return mediaStats;
    }

    public void setMediaStats(MediaStats mediaStats) {
        this.mediaStats = mediaStats;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Integer getTraineeCount() {
        return traineeCount;
    }

    public void setTraineeCount(Integer traineeCount) {
        this.traineeCount = traineeCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static final class Builder {

        private String id;
        private Long userId;
        private String name;
        private UserRole role;
        private String offer;
        private String answer;
        private Candidate candidate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Map<Long, String> trainers;
        private MediaStats mediaStats;
        private String token;
        private String classId;
        private Integer traineeCount;
        private String avatar;

        /* default */ Builder() {
        }

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withOffer(final String offer) {
            this.offer = offer;
            return this;
        }

        public Builder withAnswer(final String answer) {
            this.answer = answer;
            return this;
        }

        public Builder withIceCandidate(final Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public Builder withRegisteredUsers(final Map<Long, String> users) {
            this.trainers = users;
            return this;
        }

        public Builder withStartTime(final LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(final LocalDateTime endTimeTime) {
            this.endTime = endTimeTime;
            return this;
        }

        public Builder withRole(final UserRole role) {
            this.role = role;
            return this;
        }

        public Builder withUSerId(final Long infoId) {
            this.userId = infoId;
            return this;
        }

        public Builder withTraineeCount(final Integer traineeCount) {
            this.traineeCount = traineeCount;
            return this;
        }
        public Builder withMediaStats(final MediaStats mediaStats) {
            this.mediaStats = mediaStats;
            return this;
        }

        public Builder withToken(final String token) {
            this.token = token;
            return this;
        }

        public Builder withClassId(final String classId) {
            this.classId = classId;
            return this;
        }
        public ActionBody build() {
            return new ActionBody(this);
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
