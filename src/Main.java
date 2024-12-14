import View.LoginForm;
import View.RegisterForm;
import View.PeminjamanForm;
import View.InvoicePeminjamanForm;
import View.InvoicePeminjamanForm;
import Model.Member;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}