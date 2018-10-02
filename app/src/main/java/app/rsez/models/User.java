package app.rsez.models;

import java.util.HashMap;
import java.util.Map;

public class User extends ModelBase {
    private String email;

    private String firstName;
    private String lastName;

    public User(String documentId, String email, String firstName, String lastName) {
        super(documentId);

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);

        db.collection("users").document(getDocumentId()).set(user);
    }
}
