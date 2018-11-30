package app.rsez.features.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import app.rsez.R;
import app.rsez.features.login.RegisterActivity;
import app.rsez.models.User;

public class EditAccount extends AppCompatActivity {
    private static final String TAG = "auth" ;
    private FirebaseAuth mAuth;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mName;
    private TextView mEmailStatic;

    private String m_Text = "";

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        mAuth = FirebaseAuth.getInstance();
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.user_email);
        mName = findViewById(R.id.name);
        mEmailStatic = findViewById(R.id.email);
        setTextViews();
    }

    public void setTextViews() {
        mEmail.setText(mAuth.getCurrentUser().getEmail());
        Query query = db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                    for(DocumentSnapshot doc : docSnap) {
                        mFirstName.setText(doc.getString("firstName"));
                        mLastName.setText(doc.getString("lastName"));
                        mName.setText(doc.getString("firstName") + " " + doc.getString("lastName"));
                        mEmailStatic.setText(mEmail.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Password");

                final EditText input = new EditText(this);

              input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        confirmAuth(mAuth.getCurrentUser().getEmail(), m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void confirmAuth(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            if(validateForm()) {
                                final String firstName = mFirstName.getText().toString();
                                final String lastName = mLastName.getText().toString();
                                final String email = mAuth.getCurrentUser().getEmail();
                                final String textEmail = mEmail.getText().toString();

                               updateEmail(email, textEmail, firstName, lastName);
                                //onBackPressed();
                            }
                        }
                        else {
                            Log.d(TAG, "User re-authenticated FAIL.");
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Authentication Failed", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
                            toast.show();

                        }

                    }
                });
    }
    private void updateEmail(final String emailOriginal, final String emailChanged, final String firstName, final String lastName) {
        System.out.println("Inside update email");
        if(!emailOriginal.equals(emailChanged)) {
            FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
            users.updateEmail(emailChanged)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Log.d(TAG, "User email address updated.");
                                Query query = db.collection("users").whereEqualTo("email", emailOriginal);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                                            for(DocumentSnapshot docs : docSnap) {
                                                docs.getReference().update("email", emailChanged);
                                                docs.getReference().update("firstName", firstName);
                                                docs.getReference().update("lastName", lastName);

                                            }
                                        }
                                    }
                                });
                                updateTickets(emailOriginal, emailChanged);
                                updateEvents(emailOriginal, emailChanged);
                                updateHosts(emailOriginal, emailChanged);
                                Intent intent = new Intent(EditAccount.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else {
                                try
                                {
                                    throw task.getException();
                                }
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");
                                    Toast.makeText(getApplicationContext(), "Email not valid",
                                            Toast.LENGTH_SHORT).show();
                                    mEmail.setError("Invalid Email");
                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Log.d(TAG, "onComplete: exist_email");
                                    Toast.makeText(getApplicationContext(), "Email Exists",
                                            Toast.LENGTH_SHORT).show();
                                    mEmail.setError("Email Exists");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
        else {
            Query query = db.collection("users").whereEqualTo("email", emailOriginal);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                        for(DocumentSnapshot docs : docSnap) {
                            docs.getReference().update("firstName", firstName);
                            docs.getReference().update("lastName", lastName);
                            Intent intent = new Intent(EditAccount.this, HomeActivity.class);
                            startActivity(intent);

                        }
                    }
                }
            });

        }
    }
    private boolean validateForm() {
        boolean valid = true;

        String event = mFirstName.getText().toString();
        if (TextUtils.isEmpty(event)) {
            mFirstName.setError("Required.");
            valid = false;
        } else {
            mFirstName.setError(null);
        }

        String desc = mLastName.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            mLastName.setError("Required.");
            valid = false;
        } else {
            mLastName.setError(null);
        }

        String date = mEmail.getText().toString();
        if (TextUtils.isEmpty(date)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        return valid;
    }

    public void updateTickets(final String email, final String emailChange) {
        Query query = db.collection("tickets").whereEqualTo("userId", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                    for(DocumentSnapshot docs : docSnap) {
                       docs.getReference().update("userId", emailChange);

                    }
                }

            }
        });
    }
    public void updateEvents(final String email, final String emailChange) {
        Query query = db.collection("events").whereEqualTo("hostEmail", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                    for(DocumentSnapshot docs : docSnap) {
                        docs.getReference().update("hostEmail", emailChange);

                    }
                }

            }
        });
    }

    public void updateHosts(final String email, final String emailChange) {
        Query query = db.collection("hosts").whereEqualTo("userId", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                    for(DocumentSnapshot docs : docSnap) {
                        docs.getReference().update("userId", emailChange);

                    }
                }

            }
        });
    }

}
/*
 //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail("user@example.com")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User email address updated.");
                                        }
                                    }
                                });
                        //----------------------------------------------------------\\
 */
