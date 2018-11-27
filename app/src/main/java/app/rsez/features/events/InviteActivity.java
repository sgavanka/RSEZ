package app.rsez.features.events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import app.rsez.R;
import app.rsez.models.QRCode;
import app.rsez.models.Ticket;
import app.rsez.models.User;
import app.rsez.utils.FirebaseUtils;

import static android.support.constraint.Constraints.TAG;


public class InviteActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String eventID;
    private String eventName;
    private String email;
    private LinearLayout usersListLinearLayout;
    private String curUser;
    private ArrayList<String> hostIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_back);

        usersListLinearLayout = findViewById(R.id.usersList);

        eventID = getIntent().getStringExtra("eventID");
        eventName = getIntent().getStringExtra("eventName");

        hostIds = new ArrayList<String>();

        findViewById(R.id.inviteButton).setOnClickListener(this);

        getHostList();
    }

    @Override
    public void onClick(View v) {
        Bitmap qrcode = null;
        int i = v.getId();
        if (i == R.id.inviteButton) {
            EditText emailText = findViewById(R.id.emailEditText);
            String email = emailText.getText().toString();
            this.email = email;
            if (isEmailValid(email)) {
                inviteUser(email, this, qrcode);
            } else {
                emailText.setError("Malformed Email");
            }
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Ticket ticket = new Ticket(FirebaseUtils.generateDocumentId(), eventID, curUser, null);
                    ticket.write();
                    sendEmail(InviteActivity.this, email);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


    public void getHostList() {
        Query query = db.collection("hosts").whereEqualTo("eventId", eventID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docSnap = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docSnap) {
                        hostIds.add(doc.getString("userId"));
                    }
                } else {
                    System.out.println("Hosts task failed");
                }
                getUserList();
            }
        });
    }

    public void getUserList() {
        mLinearLayout.removeAllViews();
        CollectionReference colRef = db.collection("users");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> users = task.getResult().getDocuments();
                    for (int i = 0; i < users.size(); i++) {
                        DocumentSnapshot docSnap = users.get(i);
                        boolean isHost = false;
                        for (String id : hostIds) {
                            if (docSnap.getString("email").equals(id)) {
                                isHost = true;
                                break;
                            }
                        }

                        if (!isHost) {
                            final User user = new User(docSnap.getString("UserId"), docSnap.getString("email"), docSnap.getString("firstName"), docSnap.getString("lastName"));
                            db.collection("tickets").whereEqualTo("userId", user.getEmail()).whereEqualTo("eventId", eventID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    if (value.isEmpty()) {
                                        View userInviteView = getLayoutInflater().inflate(R.layout.view_user_invite, null);
                                        ((TextView) userInviteView.findViewById(R.id.user_name)).setText(user.getFirstName() + " " + user.getLastName());
                                        ((TextView) userInviteView.findViewById(R.id.user_email)).setText(user.getEmail());
                                        userInviteView.findViewById(R.id.invite_button).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                inviteUser(user.getEmail(), InviteActivity.this, null);
                                            }
                                        });

                                        usersListLinearLayout.addView(userInviteView);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void inviteUser(final String email, final Context context, Bitmap qrcode) {
        final User[] user = {null};
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = new User(documentSnapshot.getString("UserId"), documentSnapshot.getString("email"),
                        documentSnapshot.getString("firstName"), documentSnapshot.getString("lastName"));
                System.out.println("USER[0]: " + documentSnapshot.getString("firstName"));
                if (documentSnapshot.getString("firstName") != null) {
                    CollectionReference colRef = db.collection("tickets");
                    Query query = colRef.whereEqualTo("userId", email).whereEqualTo("eventId", eventID);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> users = task.getResult().getDocuments();
                                if (users.size() == 0) {
                                    Ticket ticket = new Ticket(FirebaseUtils.generateDocumentId(), eventID, user[0].getEmail(), null);
                                    ticket.write();

                                    onBackPressed();

                                    Toast toast = Toast.makeText(getApplicationContext(), "Invite Sent, Make sure to send an Email too", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
                                    toast.show();

                                    sendEmail(context, email);
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "User has already been invited", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
                                    toast.show();
                                }
                            }
                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    curUser = email;
                    builder.setMessage("User does not exist would you like to send an Email?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("User does not exist would you like to send an Email?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void sendEmail(Context context, String email) {

        try {
            Bitmap qrcode = QRCode.generateQRCode(context, eventID + " - " + email);

            // Store image in Devise database to send image to mail
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), qrcode, "qrcode", "qrcode");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "You have been invited to " + eventName);
            Uri uri = Uri.parse(url);

            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            // Need this to prompts email client only
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        getUserList();


    }
}
