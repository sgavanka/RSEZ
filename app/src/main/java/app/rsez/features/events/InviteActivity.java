package app.rsez.features.events;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.rsez.R;
import app.rsez.models.QRCode;
import app.rsez.models.User;


public class InviteActivity extends Activity implements View.OnClickListener {
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String eventID;
    private String eventName;
    private String email;
    private Context context;
    private LinearLayout mLinearLayout;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Button invite = findViewById(R.id.inviteButton);
        mLinearLayout = findViewById(R.id.usersList);
        eventID = getIntent().getStringExtra("eventID");
        eventName = getIntent().getStringExtra("eventName");
        context = this;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        invite.setOnClickListener(this);
        getUserList();
    }

    @Override
    public void onClick(View v) {
        Bitmap qrcode = null;
        int i = v.getId();
        if (i == R.id.inviteButton){
            EditText emailText = findViewById(R.id.emailEditText);
            String email = emailText.getText().toString();
            System.out.println("Generate QRCode");
            //TODO: Generate qr code to email
            //user = User.getUserFromEmail(email);
            this.email = email;
            if(isEmailValid(email)) {
                getUserFromEmail(email, this, qrcode);
            }
            else {
                 emailText.setError("Malformed Email");
            }


        }
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    sendEmail(context, email);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };


    public void getUserList() {
        CollectionReference colRef = db.collection("users");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<DocumentSnapshot> users = task.getResult().getDocuments();
                    for (int i = 0; i < users.size(); i++){
                        DocumentSnapshot docSnap = users.get(i);
                        //System.out.println("User " + i + ": " + docSnap.get("firstName"));
                        if (!mUser.getEmail().equals(docSnap.getString("email"))) {
                            final User user = new User(docSnap.getString("UserId"), docSnap.getString("email"), docSnap.getString("firstName"), docSnap.getString("lastName"));

                            TextView tempText = new TextView(context);
                            String Username = user.getFirstName() + " " + user.getLastName() + "\n" + user.getEmail();
                            tempText.setText(Username);
                            tempText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            tempText.setTextSize(17);
                            tempText.setBackground(ContextCompat.getDrawable(context, R.drawable.customborder2));
                            tempText.setTextColor(Color.BLACK);
                            tempText.setPadding(10, 10, 0, 20);
                            tempText.setClickable(true);
                            tempText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("User clicked: " + user.getEmail());
                                    getUserFromEmail(user.getEmail(), context, null);
                                }
                            });
                            mLinearLayout.addView(tempText);
                            Space tempSpace = new Space(context);
                            tempSpace.setMinimumHeight(5);
                            mLinearLayout.addView(tempSpace);
                        }
                    }
                }
            }
        });

    }

    //
    public void getUserFromEmail(final String email , final Context context, Bitmap qrcode){
        final User[] user = {null};
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = new User(documentSnapshot.getString("UserId"), documentSnapshot.getString("email"),
                        documentSnapshot.getString("firstName"), documentSnapshot.getString("lastName"));
                  System.out.println("USER[0]: " + documentSnapshot.getString("firstName"));
                   if(documentSnapshot.getString("firstName") != null) {
                       Toast toast= Toast.makeText(getApplicationContext(),
                               "Invite Sent, Make sure to send an Email too", Toast.LENGTH_LONG);
                       toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 50);
                       toast.show();
                       user[0].addEvent(eventID);
                       user[0].write();
                       sendEmail(context, email);
                   }
                   else {
                       System.out.println("User not found");
                       AlertDialog.Builder builder = new AlertDialog.Builder(context);
                       builder.setMessage("User does not exist would you like to send an Email?").setPositiveButton("Yes", dialogClickListener)
                               .setNegativeButton("No", dialogClickListener).show();

                   }


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    System.out.println("User not found");
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
            System.out.println("user found");
            Bitmap qrcode = QRCode.generateQRCode(context, eventID + " - " + email);
            // Store image in Devise database to send image to mail
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), qrcode, "qrcode", "qrcode");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "You have been invited to " + eventName);
            Uri uri = Uri.parse(url);

            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            //need this to prompts email client only
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
