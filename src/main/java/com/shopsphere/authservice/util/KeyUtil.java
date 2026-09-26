package com.shopsphere.authservice.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {

  public static RSAPublicKey loadPublicKey(String path) {
    try {
      String publicKey = readKey(path, "PUBLIC KEY");

      byte[] decode = Base64.getDecoder().decode(publicKey);

      X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);

      KeyFactory factory = KeyFactory.getInstance("RSA");

      return (RSAPublicKey) factory.generatePublic(spec);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static RSAPrivateKey loadPrivateKey(String path) {
    try {
      String privateKey = readKey(path, "PRIVATE KEY");

      byte[] decodedKeyBytes = Base64.getDecoder().decode(privateKey);

      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKeyBytes);

      KeyFactory factory = KeyFactory.getInstance("RSA");

      return (RSAPrivateKey) factory.generatePrivate(keySpec);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private static String readKey(String path, String type) throws IOException {
    String key = Files.readString(Path.of(path));
    return key
      .replace("-----BEGIN " + type + "-----", "")
      .replace("-----END " + type + "-----", "")
      .replaceAll("\\s+", "");
  }
}
