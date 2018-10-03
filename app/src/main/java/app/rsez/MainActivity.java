package app.rsez;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public  class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";
    private TextView mEmailField;
    private TextView mPasswordField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        // Button buttonLogin = (Button) findViewById(R.id.loginButton);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);
        // Capture button clicks
       /* buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        HomeActivity.class);
                startActivity(myIntent);

            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent myIntent = new Intent(MainActivity.this,
                    HomeActivity.class);
            startActivity(myIntent);
        }
    }

  /*  private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, write UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent myIntent = new Intent(MainActivity.this,
                                    HomeActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            Intent myIntent = new Intent(MainActivity.this,
//                                    MainActivity.class);
//                            startActivity(myIntent);
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    } */

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

        return valid;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, write UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent myIntent = new Intent(MainActivity.this,
                                    HomeActivity.class);
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Incorrect Email or Password.",
                                    Toast.LENGTH_SHORT).show();
                           /* Intent myIntent = new Intent(MainActivity.this,
                                    MainActivity.class);
                            startActivity(myIntent); */
                        }

                    }
                });
        // [END sign_in_with_email]
    }

        @Override
        public void onClick(View v){
            int i = v.getId();
            if (i == R.id.registerButton) {
                Intent myIntent = new Intent(MainActivity.this,
                        RegisterActivity.class);
                startActivity(myIntent);

                //createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else if (i == R.id.loginButton) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }/* else if (i == R.id.signOutButton) {
                            signOut();
                        } else if (i == R.id.verifyEmailButton) {
                            sendEmailVerification();
                        }*/
        }
    }













/*

 */