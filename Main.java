import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.Login;

/**
 * Main entry point for the Library Management System.
 * Sets the Look and Feel and launches the Login window.
 */
public class Main {

    public static void main(String[] args) {
        // Set Nimbus Look and Feel for a modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus Look and Feel not available, using default.");
        }

        // Launch the Login window on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Login loginWindow = new Login();
            loginWindow.setVisible(true);
        });
    }
}
