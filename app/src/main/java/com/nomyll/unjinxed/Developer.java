package com.nomyll.unjinxed;

public class Developer {
    public static String login = "dev";
    public static String password = "dev";

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Developer.password = password;
    }

    public static String getLogin() {

        return login;
    }

    public static void setLogin(String login) {
        Developer.login = login;
    }
}
