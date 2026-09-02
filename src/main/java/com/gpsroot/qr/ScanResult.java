package com.gpsroot.qr;

import java.time.Instant;
import java.time.LocalDateTime;

record ScanResult(String type, Instant timestamp) {}
