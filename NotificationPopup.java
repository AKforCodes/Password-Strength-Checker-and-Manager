import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple notification popup that appears at the bottom right corner and fades away after a set time.
 */
public class NotificationPopup extends JWindow {

    private static final int DISPLAY_TIME = 3000; // milliseconds
    private static final int FADE_INTERVAL = 50;  // milliseconds between opacity changes
    private static final float OPACITY_DECREMENT = 0.05f; // amount to decrease opacity each time

    public NotificationPopup(String message) {
        // Set up the label
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 200));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(label);

        pack();

        // Position at the bottom right corner
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarHeight = screenInsets.bottom;
        int x = screenSize.width - getWidth() - 20;
        int y = screenSize.height - getHeight() - taskBarHeight - 20;
        setLocation(x, y);

        setOpacity(1.0f);
        setAlwaysOnTop(true);
        setVisible(true);

        // Start the display timer
        Timer displayTimer = new Timer(DISPLAY_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start fade out
                fadeOut();
            }
        });
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    private void fadeOut() {
        // Set up a timer to fade out the window
        Timer fadeTimer = new Timer(FADE_INTERVAL, null);
        fadeTimer.addActionListener(new ActionListener() {
            float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= OPACITY_DECREMENT;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    fadeTimer.stop();
                    dispose();
                }
                setOpacity(Math.max(opacity, 0.0f));
            }
        });
        fadeTimer.start();
    }
}
