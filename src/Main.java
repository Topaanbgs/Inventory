import View.LoginForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();           
                loginForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginForm.setLocationRelativeTo(null);
                loginForm.pack();
                loginForm.setVisible(true);
        });
    }
}