package com.cv.simulator.videoosd.v1.websocket;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;

public class WsPayloadEncoder {

    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRandom secureRandom = new SecureRandom();
    private final boolean enabled;
    private final String normalizedMode;
    private final String keyId;
    private final byte[] aesKey;

    public WsPayloadEncoder(SimulatorOsdProperties.WsCrypto properties) {
        SimulatorOsdProperties.WsCrypto wsCrypto = properties == null ? new SimulatorOsdProperties.WsCrypto() : properties;
        this.enabled = wsCrypto.isEnabled();
        this.normalizedMode = normalizeMode(wsCrypto.getMode());
        if (!enabled || "plain".equals(normalizedMode)) {
            this.keyId = wsCrypto.getKeyId();
            this.aesKey = null;
            return;
        }
        if (!"aes-gcm".equals(normalizedMode)) {
            throw new IllegalStateException("unsupported ws crypto mode: " + wsCrypto.getMode());
        }
        if (isBlank(wsCrypto.getKeyId())) {
            throw new IllegalStateException("ws crypto key-id must not be blank when AES-GCM is enabled");
        }
        this.keyId = wsCrypto.getKeyId();
        this.aesKey = decodeAndValidateKey(wsCrypto.getKeyBase64());
    }

    public String encode(String plaintextPayload) {
        if (!enabled || "plain".equals(normalizedMode)) {
            return plaintextPayload;
        }
        return encryptAesGcm(plaintextPayload);
    }

    private String encryptAesGcm(String plaintextPayload) {
        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintextPayload.getBytes(StandardCharsets.UTF_8));
            return objectMapper.writeValueAsString(new WsEncryptedEnvelope(
                    true,
                    "AES-GCM",
                    keyId,
                    Base64.getEncoder().encodeToString(iv),
                    Base64.getEncoder().encodeToString(ciphertext)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("failed to encrypt websocket payload", e);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize encrypted websocket payload", e);
        }
    }

    private String normalizeMode(String mode) {
        if (mode == null) {
            return "plain";
        }
        return mode.trim().toLowerCase(Locale.ROOT);
    }

    private byte[] decodeAndValidateKey(String keyBase64) {
        if (isBlank(keyBase64)) {
            throw new IllegalStateException("ws crypto key-base64 must not be blank when AES-GCM is enabled");
        }
        final byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(keyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("ws crypto key-base64 is not valid Base64", e);
        }
        if (decoded.length != 16 && decoded.length != 24 && decoded.length != 32) {
            throw new IllegalStateException("ws crypto key-base64 must decode to 16, 24, or 32 bytes for AES");
        }
        return decoded;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class WsEncryptedEnvelope {
        private final boolean encrypted;
        private final String alg;
        private final String kid;
        private final String iv;
        private final String ciphertext;

        private WsEncryptedEnvelope(boolean encrypted, String alg, String kid, String iv, String ciphertext) {
            this.encrypted = encrypted;
            this.alg = alg;
            this.kid = kid;
            this.iv = iv;
            this.ciphertext = ciphertext;
        }

        public boolean isEncrypted() {
            return encrypted;
        }

        public String getAlg() {
            return alg;
        }

        public String getKid() {
            return kid;
        }

        public String getIv() {
            return iv;
        }

        public String getCiphertext() {
            return ciphertext;
        }
    }
}
