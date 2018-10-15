package app.rsez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import app.rsez.models.Event;
import app.rsez.models.QRCode;
import app.rsez.models.User;

import static android.support.constraint.Constraints.TAG;


public class InviteActivity extends Activity implements View.OnClickListener {
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String eventID;
    String eventName;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Button invite = findViewById(R.id.inviteButton);
        eventID = getIntent().getStringExtra("eventID");
        eventName = getIntent().getStringExtra("eventName");
        invite.setOnClickListener(this);
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
            getUserFromEmail(email, this, qrcode);





        }
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
    public void getUserFromEmail(final String email , final Context context, Bitmap qrcode){
        final User[] user = {null};
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = new User(documentSnapshot.getString("UserId"), documentSnapshot.getString("email"),
                        documentSnapshot.getString("firstName"), documentSnapshot.getString("LastName"));

                if(user[0] == null) {
                    System.out.println("User not found");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("User does not exist would you like to send an Email?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
                else {

                    try {
                        System.out.println("user found");
                        Bitmap qrcode = QRCode.generateQRCode(context, eventID + " - " + email);
                        // Store image in Devise database to send image to mail
                        ImageView imageViewQrCode = findViewById(R.id.qrcodeView);
                        imageViewQrCode.setImageBitmap(qrcode);
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
        });

    }
}
