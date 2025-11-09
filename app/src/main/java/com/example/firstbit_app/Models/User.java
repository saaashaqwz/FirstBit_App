package com.example.firstbit_app.Models;

/**
 * класс, представляющий модель пользователя
 */
public class User {
    private String login;
    private String phone;
    private String password;

    public User(String login, String phone, String password) {
        this.login = login;
        this.phone = phone;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }
    public String getPhone() {
        return phone;
    }
    public String getPassword() {
        return password;
    }
}
