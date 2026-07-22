package com.docsy.security;

import com.docsy.config.DocsyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * HMAC-MD5 签名验证器 — 与 docsz 三方签名机制一致
 * 签名算法: MD5(app_secret + "@@" + timestamp + "@@" + nonce)
 */
@Component
public class HmacVerifier {

    private static final Logger log = LoggerFactory.getLogger(HmacVerifier.class);

    private final DocsyProperties properties;

    public HmacVerifier(DocsyProperties properties) {
        this.properties = properties;
    }

    /**
     * 验证三方应用签名
     *
     * @param appSecret  应用密钥
     * @param timestamp  请求时间戳（秒）
     * @param nonce      随机字符串
     * @param sign       请求携带的签名
     * @return 签名是否有效
     */
    public boolean verify(String appSecret, String timestamp, String nonce, String sign) {
        // 检查时间戳有效期
        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            log.warn("无效的时间戳: {}", timestamp);
            return false;
        }

        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > properties.getSignExpireSeconds()) {
            log.warn("签名已过期: timestamp={}, now={}, expire={}s", ts, now, properties.getSignExpireSeconds());
            return false;
        }

        // 计算签名并比较
        String expected = computeSign(appSecret, timestamp, nonce);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                sign.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 计算签名: MD5(app_secret + "@@" + timestamp + "@@" + nonce)
     */
    public static String computeSign(String appSecret, String timestamp, String nonce) {
        String raw = appSecret + "@@" + timestamp + "@@" + nonce;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 计算失败", e);
        }
    }
}
