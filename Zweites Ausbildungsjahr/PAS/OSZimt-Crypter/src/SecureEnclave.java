import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import javax.crypto.*;
import javax.crypto.spec.*;
/**
 * 
 */

/**
 * @author mahd
 *
 */
public class SecureEnclave {
	
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
	
    public byte[] encrypt(String password, String plaintext) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[cipher.getBlockSize()];
            secureRandom.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedData = new byte[salt.length + iv.length + encryptedText.length];
            System.arraycopy(salt, 0, encryptedData, 0, salt.length);
            System.arraycopy(iv, 0, encryptedData, salt.length, iv.length);
            System.arraycopy(encryptedText, 0, encryptedData, salt.length + iv.length, encryptedText.length);

            return encryptedData;
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
        }
        return null;
    }

    public String decrypt(String password, byte[] encryptedData) {
        try {
            byte[] salt = Arrays.copyOfRange(encryptedData, 0, 16);
            byte[] iv = Arrays.copyOfRange(encryptedData, 16, 16 + 16);
            byte[] encryptedText = Arrays.copyOfRange(encryptedData, 16 + 16, encryptedData.length);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decryptedText = cipher.doFinal(encryptedText);

            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Decryption error: " + e.getMessage());
        }
        return null;
    }
}
