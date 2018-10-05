package app.rsez.models;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;

//import javax.imageio.ImageIO;

public class QRCode {

    //filePath = "<code>.png";
    //charset = "UTF-8";
    //height = 200;
    //width = 200;

    public static Bitmap generateQRCode(Context context, String code) throws WriterException, IOException {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        String filepath = contextWrapper.getFilesDir().getAbsolutePath();
        System.out.println("Filepath: " + filepath);
       BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 600, 600);
        return bitmap;
    }
}
