package com.cv.simulator.videoosd.v1.websocket;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WsPayloadEncoderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void keepsPlainPayloadWhenCryptoDisabled() {
        SimulatorOsdProperties.WsCrypto properties = new SimulatorOsdProperties.WsCrypto();
        properties.setEnabled(false);
        properties.setMode("plain");

        WsPayloadEncoder encoder = new WsPayloadEncoder(properties);

        assertEquals("{\"timestamp\":1}", encoder.encode("{\"timestamp\":1}"));
    }

    @Test
    void encryptsPayloadWithAesGcmEnvelope() throws Exception {
        SimulatorOsdProperties.WsCrypto properties = new SimulatorOsdProperties.WsCrypto();
        properties.setEnabled(true);
        properties.setMode("aes-gcm");
        properties.setKeyId("test-key");
        properties.setKeyBase64("MDEyMzQ1Njc4OWFiY2RlZg==");

        WsPayloadEncoder encoder = new WsPayloadEncoder(properties);

        String encoded = encoder.encode("{\"timestamp\":1782972590947,\"attitude_head\":87.0}");
        JsonNode node = objectMapper.readTree(encoded);

        assertTrue(node.get("encrypted").asBoolean());
        assertEquals("AES-GCM", node.get("alg").asText());
        assertEquals("test-key", node.get("kid").asText());
        assertFalse(node.get("iv").asText().isEmpty());
        assertFalse(node.get("ciphertext").asText().isEmpty());
        assertFalse(encoded.contains("\"attitude_head\":87.0"));
        assertEquals(
                "{\"timestamp\":1782972590947,\"attitude_head\":87.0}",
                decrypt(node.get("iv").asText(), node.get("ciphertext").asText(), properties.getKeyBase64()));
    }

    @Test
    void rejectsInvalidAesKeyConfiguration() {
        SimulatorOsdProperties.WsCrypto properties = new SimulatorOsdProperties.WsCrypto();
        properties.setEnabled(true);
        properties.setMode("aes-gcm");
        properties.setKeyId("test-key");
        properties.setKeyBase64("bad-key");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new WsPayloadEncoder(properties));

        assertTrue(exception.getMessage().contains("ws crypto key-base64"));
    }

    private String decrypt(String ivBase64, String ciphertextBase64, String keyBase64) throws Exception {
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        byte[] key = Base64.getDecoder().decode(keyBase64);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }
}
