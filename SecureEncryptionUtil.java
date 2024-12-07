import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class SecureEncryptionUtil {
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;

    /**
     * Derive AES key from password using PBKDF2.
     */
    public static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Encrypt data using AES.
     */
    public static String encrypt(String data, char[] password, byte[] salt) throws Exception {
        SecretKey key = deriveKey(password, salt);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        byte[] encryptedWithIV = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIV, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, encryptedWithIV, IV_LENGTH, encrypted.length);
        return Base64.getEncoder().encodeToString(encryptedWithIV);
    }

    /**
     * Decrypt data using AES.
     */
    public static String decrypt(String encryptedData, char[] password, byte[] salt) throws Exception {
        byte[] encryptedWithIV = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(encryptedWithIV, 0, iv, 0, IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] encrypted = new byte[encryptedWithIV.length - IV_LENGTH];
        System.arraycopy(encryptedWithIV, IV_LENGTH, encrypted, 0, encrypted.length);
        SecretKey key = deriveKey(password, salt);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

    /**
     * Generate a random salt.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
}
