package com.gpsroot.qr;

public record QrPayloadResponse(
        String officeId,
        long issuedAt,
        String nonce,
        String signature
) {}
