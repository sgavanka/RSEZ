package app.rsez;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.rsez.models.Ticket;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanFragment extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ZXingScannerView zXingScannerView;
    final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    int cameraEnabled = 0;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        eventID = getIntent().getStringExtra("eventId");
        System.out.println("QRScan eventID: " + eventID);
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            zXingScannerView.setResultHandler(this);
            zXingScannerView.startCamera();
        }
        //qrScan();
    }

    public void qrScan(){


        if (cameraEnabled == 1) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        //Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
        String res = result.getText();
        if (res.contains(" - ")) {
            String event = res.substring(0, res.indexOf('-') - 1);
            if (event.equals(eventID)) {
                String user = res.substring(res.indexOf('-') + 2);
                CollectionReference colRef = db.collection("tickets");
                final Query ticket = colRef.whereEqualTo("eventId", event).whereEqualTo("userId", user);
                ticket.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> tickets = task.getResult().getDocuments();
                            //System.out.println("Number of results: " + tickets.size());
                            if (tickets.size() == 1) {
                                Toast.makeText(getApplicationContext(), "Ticket is a valid ticket", Toast.LENGTH_SHORT).show();
                                System.out.println("Valid Ticket");
                                checkIn(tickets.get(0));

                            } else if (tickets.size() == 0) {
                                Toast.makeText(getApplicationContext(), "Ticket is NOT a valid ticket", Toast.LENGTH_SHORT).show();
                                System.out.println("No Tickets Found");
                            } else {
                                Toast.makeText(getApplicationContext(), "Ticket is NOT a valid ticket", Toast.LENGTH_SHORT).show();
                                System.out.println("More than one ticket found");
                            }
                            zXingScannerView.resumeCameraPreview(QRScanFragment.this);
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Ticket is NOT a valid ticket for this event", Toast.LENGTH_SHORT).show();
                System.out.println("Ticket is not for correct event");
                zXingScannerView.resumeCameraPreview(QRScanFragment.this);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Ticket is NOT a valid ticket", Toast.LENGTH_SHORT).show();
            System.out.println("No - found in qrcode. May have been generated from a different source");
            zXingScannerView.resumeCameraPreview(QRScanFragment.this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //zXingScannerView.setResultHandler(this);
                    //zXingScannerView.startCamera();
                    cameraEnabled = 1;
                    zXingScannerView.setResultHandler(this);
                    zXingScannerView.startCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    cameraEnabled = -1;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void checkIn(DocumentSnapshot ticket){
        System.out.println("CHECK IN: " + ticket.get("checkInDateTime"));
        if (ticket.get("checkInDateTime") == null) {
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            Ticket newTicket = new Ticket(ticket.getId(), ticket.getString("eventId"), ticket.getString("userId"), currentDate);
            newTicket.write(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Checked in Successfully");
                    Toast.makeText(getApplicationContext(), "Check in Successful", Toast.LENGTH_SHORT).show();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Checked in Failed");
                    Toast.makeText(getApplicationContext(), "Failed to check in properly", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            System.out.println("Checked in Failed: User already checked in");
            Toast.makeText(getApplicationContext(), "This ticket has already been checked in", Toast.LENGTH_SHORT).show();
        }
    }
}
