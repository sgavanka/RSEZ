package app.rsez.models;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

//import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCode {
    String code;

    public QRCode(String code){
        this.code = code;
    }

    //filePath = "<code>.png";
    //charset = "UTF-8";
    //height = 200;
    //width = 200;

    public static void createQRCode(String code, String filePath, String charset, Map hintMap, int height, int width) throws WriterException, IOException {BitMatrix matrix = new MultiFormatWriter().encode(new String(code.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
    }
}
