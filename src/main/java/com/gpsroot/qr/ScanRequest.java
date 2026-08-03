package com.gpsroot.qr;

record ScanRequest(
        String officeId,
        long issuedAt,
        String nonce,
        String signature,
        String employeeId,
        double gpsLat,
        double gpsLng
) {}
