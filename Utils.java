import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Utils {
    // Parameters for PBKDF2
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    // AES parameters
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES_KEY_ALGORITHM = "AES";

    /**
     * Hash the password using PBKDF2 with HMAC SHA-256.
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[16];
            sr.nextBytes(salt);

            // Create PBEKeySpec
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Get SecretKeyFactory
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = skf.generateSecret(spec).getEncoded();

            // Return salt and hash encoded in Base64, separated by a colon
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verify the password against the stored hash.
     */
    public static boolean verifyPassword(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            // Create PBEKeySpec
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Get SecretKeyFactory
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] testHash = skf.generateSecret(spec).getEncoded();

            // Compare hashes
            return MessageDigest.isEqual(hash, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Derive AES key from password using PBKDF2.
     */
    public static String deriveAESKey(String password) {
        try {
            // Use a fixed salt for simplicity; in production, use a unique salt per user and store it securely
            byte[] salt = "FixedSaltValue123".getBytes(); // 16 bytes salt

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = skf.generateSecret(spec).getEncoded();

            // Return Base64 encoded key
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt data using AES.
     */
    public static String encryptAES(String data, String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Generate IV
            SecureRandom sr = new SecureRandom();
            byte[] iv = new byte[16];
            sr.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize Cipher
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes());

            // Prepend IV to encrypted data
            byte[] encryptedWithIV = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIV, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedWithIV, iv.length, encrypted.length);

            // Return as Base64 string
            return Base64.getEncoder().encodeToString(encryptedWithIV);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt data using AES.
     */
    public static String decryptAES(String encryptedData, String base64Key) {
        try {
            byte[] encryptedWithIV = Base64.getDecoder().decode(encryptedData);

            // Extract IV
            byte[] iv = new byte[16];
            System.arraycopy(encryptedWithIV, 0, iv, 0, 16);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Extract encrypted data
            byte[] encrypted = new byte[encryptedWithIV.length - 16];
            System.arraycopy(encryptedWithIV, 16, encrypted, 0, encrypted.length);

            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Initialize Cipher
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
