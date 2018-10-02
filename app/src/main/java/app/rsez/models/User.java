package app.rsez.models;

import java.util.HashMap;
import java.util.Map;

public class User extends ModelBase {
    String userId;

    String email;

    String firstName;
    String lastName;

    public User(String userId, String email, String firstName, String lastName) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void create() {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);

        db.collection("users").add(user);
    }
}
