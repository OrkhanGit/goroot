package com.gpsroot.qr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrTokenService qrTokenService;

    public QrController(QrTokenService qrTokenService) {
        this.qrTokenService = qrTokenService;
    }

    @GetMapping("/generate")
    public ResponseEntity<QrPayloadResponse> generate(
            @RequestParam String officeId
    ) {
        return ResponseEntity.ok(qrTokenService.generateQr(officeId));
    }
}
