package com.pilates.app.model.dto;

import com.google.gson.GsonBuilder;

import com.pilates.app.model.UserRole;


public class UserDto implements Request {

    private String confirmPassword;
    private String email;
    private String password;
    private UserRole role;
    private String name;

    public UserDto(final Builder builder) {
        this.confirmPassword = builder.confirmPassword;
        this.password = builder.password;
        this.email = builder.email;
        this.role = builder.role;
        this.name = builder.name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
    public void setPassword(final String password) {
        this.password = password;
    }
    public void setRole(final UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, UserDto.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static final class Builder {
        private String confirmPassword;
        private String password;
        private String email;
        private UserRole role;
        private String name;

        public Builder withConfirmPassword(final String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public Builder withEmail(final String email) {
            this.email = email;
            return this;
        }
        public Builder withPassword(final String password) {
            this.password = password;
            return this;
        }
        public Builder withRole(final UserRole role) {
            this.role = role;
            return this;
        }


        public Builder withName(final String name) {
            this.name = name;
            return this;
        }
        public UserDto build() {
            return new UserDto(this);
        }
    }
}
