package app.rsez;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import java.io.IOException;

import app.rsez.models.QRCode;

public class InviteActivity extends Activity implements View.OnClickListener {

    String eventID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Button invite = findViewById(R.id.inviteButton);
        eventID = getIntent().getStringExtra("eventID");
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
                ImageView imageViewQrCode = findViewById(R.id.qrcodeView);
                imageViewQrCode.setImageBitmap(qrcode);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
