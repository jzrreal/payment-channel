package com.merchant.external.api.utilities;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.external.api.enums.DateFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

    public static String generateTimestamp(String timestamp, String datePattern) {
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String utcTimestamp = sdt.format(new Date());
        LocalDateTime datetimeWithoutZone = LocalDateTime.parse(
            timestamp,
            // DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            DateTimeFormatter.ofPattern(datePattern)
        );
        // convert it to a zone-aware datetime object by adding a zone
        ZonedDateTime utcZdt = datetimeWithoutZone.atZone(ZoneId.of("UTC"));
        // print the datetime in utc once
        System.out.println(utcZdt);
        // then convert the zoned datetime to a different time zone
        ZonedDateTime asiaJakartaZdt = utcZdt.withZoneSameInstant(ZoneId.of("Asia/Jakarta"));
        // and print the result
        return asiaJakartaZdt.toString();
    }
    
    public static String generateMandiriSignatureHeader(
        String httpMethod,
        String url,
        Object hexSha256RequestBody,
        String timestamp,
        String accessToken,
        String clientKey
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String urlPath = url.split("timestamp")[0];
        String dataToHash = stringAppend(
            httpMethod, ":",
            urlPath, ":",
            accessToken, ":",
            timestamp
        );

        SecretKeySpec secretKeySpec = new SecretKeySpec(clientKey.getBytes(), "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKeySpec);
        byte[] sha512 = mac.doFinal(dataToHash.getBytes());
        return Base64.getEncoder().encodeToString(sha512);
    }

    public static String hexSha256(Object requestBody) throws JsonProcessingException, NoSuchAlgorithmException {
        ObjectMapper objectMapper = new ObjectMapper();
        String minifyBody = objectMapper.writeValueAsString(requestBody);
        MessageDigest digest = MessageDigest.getInstance("SHA256");
        byte[] sha456 = digest.digest(minifyBody.getBytes());

        Formatter formatter = new Formatter();
        for(byte b : sha456) {
            formatter.format("%02X", b);
        }
        formatter.close();
        return formatter.toString();
    }

    public static String sha256withRsa(String clientId, String timestamp, int keySize) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String dataToHash = stringAppend(clientId, "|", timestamp);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        MessageDigest digest = MessageDigest.getInstance("SHA256");
        byte[] hashedData = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hashedData);
        byte[] signedHash = signature.sign();
        return signedHash.toString();
    }

    public static String sha256withRsa(String clientId, String timestamp, String privateKey)
    throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        String dataToHash = stringAppend(clientId, "|", timestamp);
        byte[] b1 = Base64.getDecoder().decode(privateKey);
        log.info(Base64.getEncoder().encodeToString(b1));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        log.info("RSA");
        signature.initSign(keyFactory.generatePrivate(spec));
        signature.update(dataToHash.getBytes("UTF-8"));
        byte[] signedHash = signature.sign();
        return Base64.getEncoder().encodeToString(signedHash);
    }

    public static String stringAppend(String... str) {
		long st = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for(String s : str) {
			sb.append(s);
		}
		long et = System.currentTimeMillis();
		log.debug("Concatenation time for {} words: {}{}", str.length, (et - st), "ms");
		return sb.toString();
	}

    public static String sign(String keystore, String keystorePassword, String keyAlias, String keyPassword, String algorithm, String message)
      {
        FileInputStream fis = null;
         String signature = null;
         String keystoreFile = "API_Portal.jks";

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
}
