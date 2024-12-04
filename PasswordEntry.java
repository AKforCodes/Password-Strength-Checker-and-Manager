import javax.swing.ImageIcon;
import java.io.Serializable;

public class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient ImageIcon logo; // Marked transient because ImageIcon is not serializable
    private String website;
    private String email;
    private String password; // Encrypted password

    public PasswordEntry(ImageIcon logo, String website, String email, String password) {
        this.logo = logo;
        this.website = website;
        this.email = email;
        this.password = password;
    }

    public ImageIcon getLogo() {
        return logo;
    }

    public void setLogo(ImageIcon logo) {
        this.logo = logo;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Handle serialization of the logo (optional)
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        if (logo != null) {
            out.writeBoolean(true);
            out.writeObject(logo.getDescription());
        } else {
            out.writeBoolean(false);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean hasLogo = in.readBoolean();
        if (hasLogo) {
            String logoDescription = (String) in.readObject();
            // You may need to handle loading the image again if necessary
            logo = new ImageIcon("default.png"); // Replace with actual loading logic
        }
    }
}
