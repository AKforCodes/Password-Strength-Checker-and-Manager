import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

/**
 * Utility class for image operations, such as converting an Image to a BufferedImage.
 */
public class ImageUtils {
    /**
     * Converts a given Image to a BufferedImage. This is useful when you need
     * to manipulate images at the pixel level or save them in certain formats.
     *
     * @param img the Image to convert
     * @return a BufferedImage representation of the input Image
     */
    public static BufferedImage toBufferedImage(Image img) {
        // If the image is already a BufferedImage, just return it.
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a BufferedImage with transparency (TYPE_INT_ARGB)
        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the given Image onto the new BufferedImage
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}
