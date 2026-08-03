package com.gpsroot.qr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class QrTokenService {

    @Value("${app.secret-key}")
    private String secretKey; // .env / application.properties-dən gəlir

    private static final String HMAC_ALGO = "HmacSHA256";

    public QrPayloadResponse generateQr(String officeId) {
        long issuedAt = System.currentTimeMillis() / 1000; // saniyə
        String nonce = UUID.randomUUID().toString();

        String rawPayload = officeId + "|" + issuedAt + "|" + nonce;
        String signature = signHmac(rawPayload, secretKey);

        return new QrPayloadResponse(officeId, issuedAt, nonce, signature);
    }

    private String signHmac(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), HMAC_ALGO
            );
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC hesablama xətası", e);
        }
    }
}
