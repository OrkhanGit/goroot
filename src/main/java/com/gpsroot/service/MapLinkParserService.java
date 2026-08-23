package com.gpsroot.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MapLinkParserService {

    private static final List<Pattern> PATTERNS = List.of(
            Pattern.compile("!3d(-?\\d+\\.\\d+)!4d(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]ll=(-?\\d+\\.\\d+)[,%2C]+(-?\\d+\\.\\d+)"),
            Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)"),
            Pattern.compile("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    );

    public Optional<double[]> extractCoordinates(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return Optional.empty();
        }

        String cleanedUrl = unwrapSafeLink(rawUrl);
        String resolvedUrl = resolveRedirect(cleanedUrl);
        String decodedUrl = URLDecoder.decode(resolvedUrl, StandardCharsets.UTF_8);

        for (Pattern pattern : PATTERNS) {
            Matcher m = pattern.matcher(decodedUrl);
            if (m.find()) {
                return Optional.of(new double[]{
                        Double.parseDouble(m.group(1)),
                        Double.parseDouble(m.group(2))
                });
            }
        }

        return Optional.empty();
    }

    private String unwrapSafeLink(String url) {
        if (url.contains("safelinks.protection.outlook.com")) {
            try {
                URI uri = new URI(url);
                String query = uri.getQuery();
                if (query != null) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("url=")) {
                            return URLDecoder.decode(param.substring(4), StandardCharsets.UTF_8);
                        }
                    }
                }
            } catch (URISyntaxException e) {
                return url;
            }
        }
        return url;
    }

    private String resolveRedirect(String shortUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(shortUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            String location = conn.getHeaderField("Location");
            return (location != null) ? location : shortUrl;
        } catch (IOException e) {
            return shortUrl;
        }
    }
}