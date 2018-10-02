package app.rsez;

import android.graphics.Bitmap;

public class QRCode {
    String code;
    Bitmap qrImage;

    public QRCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public Bitmap getQrImage(){
        return qrImage;
    }

    public Bitmap generate(){
        //TODO: implement QR generator

        return qrImage;
    }
}
