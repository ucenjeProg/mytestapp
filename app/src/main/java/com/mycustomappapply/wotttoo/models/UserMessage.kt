package com.mycustomappapply.wotttoo.models;

public class UserMessage {
    String nickname;
    String profileUrl;
    String id;
    String message;
    String CreatedAt;

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public UserMessage(String nickname, String profileUrl, String id, String message, String createdAt) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.id = id;
        this.message = message;
        CreatedAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
