package uk.ac.man.cs.eventlite.helpers;

import org.hibernate.validator.constraints.NotEmpty;

public class UserCreateForm {

    @NotEmpty
    private String username = "";

    @NotEmpty
    private String password = "";

    @NotEmpty
    private String passwordRepeated = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeated() {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated) {
        this.passwordRepeated = passwordRepeated;
    }

    @Override
    public String toString() {
        return "UserCreateForm{" +
                "username='" + username + '\'' +
                ", password=***" + '\'' +
                ", passwordRepeated=***" + '\'' +
                '}';
    }

}