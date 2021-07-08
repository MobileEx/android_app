package com.pilates.app.model.dto;

import com.pilates.app.model.UserRole;

public class UserInfoDto extends StatusMessageDto{

    private Long userId;
    private String email;
    private String phone;
    private String name;
    private String country;
    private String city;
    private Integer postCode;
    private String address;
    private String password;
    private String avatarPath;
    private UserRole role;

    public UserInfoDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Integer getPostCode() {
        return postCode;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public UserRole getRole() {
        return role;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
