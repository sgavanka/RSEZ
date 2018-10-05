package app.rsez;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import app.rsez.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";
    private TextView mEmailField;
    private TextView mPasswordField;
    private TextView mFirstNameField;
    private TextView mLastNameField;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mFirstNameField = findViewById(R.id.firstName);
        mLastNameField = findViewById(R.id.lastName);
        findViewById(R.id.registerButton).setOnClickListener(this);

    }
    private boolean validateForm() {
        boolean valid = true;

        String emails = mEmailField.getText().toString();
        if (TextUtils.isEmpty(emails)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String passwords = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(passwords)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        String first = mFirstNameField.getText().toString();
        if (TextUtils.isEmpty(first)) {
            mFirstNameField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        String last = mLastNameField.getText().toString();
        if (TextUtils.isEmpty(last)) {
            mLastNameField.setError("Required.");
            valid = false;
        } else {
            mLastNameField.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser(user.getUid());
                            Intent myIntent = new Intent(RegisterActivity.this,
                                    HomeActivity.class);
                            startActivity(myIntent);
                        } else
                        {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(TAG, "onComplete: weak_password");
                                Toast.makeText(RegisterActivity.this, "Weak Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                Toast.makeText(RegisterActivity.this, "Email not valid",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(TAG, "onComplete: exist_email");
                                Toast.makeText(RegisterActivity.this, "Email Exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                Toast.makeText(RegisterActivity.this, "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                            }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                }
        // [END create_user_with_email]
    });
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.registerButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());

        }
    }
    public void createUser(String id){
        user = new User(id, mEmailField.getText().toString(), mFirstNameField.getText().toString(), mLastNameField.getText().toString());
        user.write();
    }
}
