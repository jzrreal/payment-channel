package com.merchant.external.api.utilities;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.merchant.external.api.enums.DateFormat;

public class MandiriUtil {

    public static String generateAccessTokenSignature(String keystoreFile, String keystorePassword, String keyAlias, String keyPassword, String algorithm, String message)
      {
        FileInputStream fis = null;
         String signature = null;
        //  String keystoreFile = "API_Portal.jks";

         try
         {
               KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
               fis = new FileInputStream(keystoreFile);
               ks.load(fis,keystorePassword.toCharArray());

            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry (keyAlias, new KeyStore.PasswordProtection(keyPassword.toCharArray ()));
            RSAPrivateKey pvKey = (RSAPrivateKey)pkEntry.getPrivateKey();
            Signature sign = Signature.getInstance(algorithm);
               sign.initSign(pvKey);
               byte[] byteMessage = message.getBytes();

               sign.update(byteMessage, 0, byteMessage.length);
               byte[] byteSignature = sign.sign();

               signature = new String(Base64.getEncoder().encode(byteSignature));
            // System.out.println("signature = "+signature);
         }
         catch(Exception ex)
         {
            ex.printStackTrace();
         }
         return signature;
      }

    public static HttpHeaders generateDefaultHeaders(
        String externalId, String channelId, String httpMethod, String url,
        String hexSha256RequestBodyString, String accessToken, String clientSecret,
        String partnerId, String timestamp
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        HttpHeaders headers = new HttpHeaders();
        // SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.MANDIRI_FORMAT.value());
        // String timestamp = dateFormat.format(new Date());
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-TIMESTAMP", timestamp);
        headers.set(
            "X-SIGNATURE",
            generateRequestSignature(
                httpMethod, url, hexSha256RequestBodyString, timestamp, accessToken, clientSecret)
            );
        headers.set("X-PARTNER-ID", partnerId);
        headers.set("X-EXTERNAL-ID", externalId);
        headers.set("CHANNEL-ID", channelId);
        return headers;
    }

    public static String generateExternalId(String transactionId) {
        // return CommonUtil.randomDecimalString(6);
        return "500001";
    }

    public static String generateRequestSignature(
        String httpMethod,
        String urlPath,
        String hexSha256RequestBody,
        String timestamp,
        String accessToken,
        String clientSecret
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String dataToHash = CommonUtil.stringAppend(
            httpMethod, ":",
            urlPath, ":",
            accessToken, ":",
            hexSha256RequestBody, ":",
            timestamp
        );

        SecretKeySpec secretKeySpec = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKeySpec);
        byte[] sha512 = mac.doFinal(dataToHash.getBytes(StandardCharsets.UTF_8));
        // String hexFormat = HexFormat.of().formatHex(sha512);
        String result = Base64.getEncoder().encodeToString(sha512);
        return result;
    }

    public static String formatTimestamp(DateFormat datePattern, Long currentTimeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern.value());
        return dateFormat.format(new Date(currentTimeMillis));
    }
}
