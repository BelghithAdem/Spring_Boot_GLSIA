package com.BeeOranized.BeeOranized.Dtos;

public class LoginRequestDto {

    private String userEmail;

    private String userPassword;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LoginRequestDto(String userEmail, String userPassword) {
        super();
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public LoginRequestDto() {
        super();
    }

}