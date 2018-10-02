package app.rsez.utils;

import java.util.UUID;

public class FirebaseUtils {
    public static String generateDocumentId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
    }
}
