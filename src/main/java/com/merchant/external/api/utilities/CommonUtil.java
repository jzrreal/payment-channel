package com.merchant.external.api.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    public static String hexSha256(String data) throws JsonProcessingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA256");
        byte[] sha256 = digest.digest(data.getBytes());
        String result = HexFormat.of().formatHex(sha256);
        return result.toLowerCase();
    }

    // public static String sha256withRsa(String clientId, String timestamp, int keySize) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    //     String dataToHash = stringAppend(clientId, "|", timestamp);
    //     KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    //     keyPairGenerator.initialize(keySize);
    //     KeyPair keyPair = keyPairGenerator.generateKeyPair();
    //     PrivateKey privateKey = keyPair.getPrivate();
    //     PublicKey publicKey = keyPair.getPublic();

    //     MessageDigest digest = MessageDigest.getInstance("SHA256");
    //     byte[] hashedData = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

    //     Signature signature = Signature.getInstance("SHA256withRSA");
    //     signature.initSign(privateKey);
    //     signature.update(hashedData);
    //     byte[] signedHash = signature.sign();
    //     return signedHash.toString();
    // }

    // public static String sha256withRsa(String clientId, String timestamp, String privateKey)
    // throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
    //     String dataToHash = stringAppend(clientId, "|", timestamp);
    //     byte[] b1 = Base64.getDecoder().decode(privateKey);
    //     log.info(Base64.getEncoder().encodeToString(b1));
    //     PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
    //     KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
    //     Signature signature = Signature.getInstance("SHA256withRSA");
    //     log.info("RSA");
    //     signature.initSign(keyFactory.generatePrivate(spec));
    //     signature.update(dataToHash.getBytes("UTF-8"));
    //     byte[] signedHash = signature.sign();
    //     return Base64.getEncoder().encodeToString(signedHash);
    // }

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

      public static char randomDecimalDigit() {
        char digits[] = {'0','1','2','3','4','5','6','7','8','9'};
        return digits[(int)Math.floor(Math.random() * 10)];
      }

      public static String randomDecimalString(int numberOfDigits) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < numberOfDigits; i++) {
            result.append(randomDecimalDigit());
        }
        return result.toString();
      }
}
