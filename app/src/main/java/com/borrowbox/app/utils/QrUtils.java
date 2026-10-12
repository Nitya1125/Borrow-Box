package com.borrowbox.app.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public final class QrUtils {

    private QrUtils() {}

    public static Bitmap generateQrCode(String content, int sizePx) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    sizePx,
                    sizePx,
                    hints
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF0F172A : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildQrPayload(String transactionId, String token) {
        return "borrowbox://handover?txId=" + transactionId + "&token=" + token;
    }

    public static String createHandoverPayload(String transactionId, String token) {
        return buildQrPayload(transactionId, token);
    }

    public static String parseTransactionIdFromQr(String qrContent) {
        if (qrContent == null) return null;
        if (qrContent.startsWith("borrowbox://handover?")) {
            String query = qrContent.substring("borrowbox://handover?".length());
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "txId".equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    public static String parseTokenFromQr(String qrContent) {
        if (qrContent == null) return null;
        if (qrContent.startsWith("borrowbox://handover?")) {
            String query = qrContent.substring("borrowbox://handover?".length());
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "token".equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    public static class HandoverData {
        private final String transactionId;
        private final String token;

        public HandoverData(String transactionId, String token) {
            this.transactionId = transactionId;
            this.token = token;
        }

        public String getTransactionId() { return transactionId; }
        public String getToken() { return token; }
    }

    public static HandoverData parseHandoverPayload(String qrContent) {
        String txId = parseTransactionIdFromQr(qrContent);
        String token = parseTokenFromQr(qrContent);
        if (txId != null && token != null) {
            return new HandoverData(txId, token);
        }
        return null;
    }
}
