package app.rsez;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import app.rsez.models.QRCode;

public class InviteActivity extends Activity implements View.OnClickListener {

    String eventID;
    String eventName;
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
        Bitmap qrcode;
        int i = v.getId();
        if (i == R.id.inviteButton){
            EditText emailText = findViewById(R.id.emailEditText);
            String email = emailText.getText().toString();
            System.out.println("Generate QRCode");
            //TODO: Generate qr code to email
            try {
                qrcode = QRCode.generateQRCode(this, eventID + " - " + email);
                // Store image in Devise database to send image to mail
                ImageView imageViewQrCode = findViewById(R.id.qrcodeView);
                imageViewQrCode.setImageBitmap(qrcode);
//                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir;
//                File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                // Create imageDir
//                File file = new File(path, "temp.png");
//                FileOutputStream fos = new FileOutputStream(file);

                String url = MediaStore.Images.Media.insertImage(getContentResolver(), qrcode, "qrcode" , "qrcode");

                // Use the compress method on the BitMap object to write image to the OutputStream
//                qrcode.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.close();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ email});
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
}
