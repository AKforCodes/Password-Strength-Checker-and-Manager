// PasswordEntry.java

import javax.swing.ImageIcon;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] logoBytes; // Serialized form of the logo
    private String website;
    private String email;
    private String encryptedPassword;

    // Transient because we reconstruct ImageIcon from logoBytes
    private transient ImageIcon logo;

    public PasswordEntry(ImageIcon logo, String website, String email, String encryptedPassword) {
        this.logo = logo;
        this.website = website;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.logoBytes = imageIconToBytes(logo);
    }

    // Getters and setters

    public ImageIcon getLogo() {
        if (logo == null && logoBytes != null) {
            logo = bytesToImageIcon(logoBytes);
        }
        return logo;
    }

    public void setLogo(ImageIcon logo) {
        this.logo = logo;
        this.logoBytes = imageIconToBytes(logo);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    // Utility methods to convert ImageIcon to byte array and vice versa

    private byte[] imageIconToBytes(ImageIcon icon) {
        if (icon == null) return null;
        try {
            BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = bufferedImage.createGraphics();
            icon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon bytesToImageIcon(byte[] bytes) {
        if (bytes == null) return null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(bais);
            return new ImageIcon(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
