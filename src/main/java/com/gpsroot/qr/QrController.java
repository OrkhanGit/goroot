package com.gpsroot.qr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/all/{userName}")
    public ResponseEntity<List<AttendanceLog>> getAll(@PathVariable String userName) {
        return ResponseEntity.ok(qrTokenService.getAll(userName));
//        return qrTokenService.getAll(userName);
    }
}
