import View.LoginForm;
import View.RegisterForm;
import View.DashboardForm;
import View.PeminjamanForm;
import View.TransaksiForm;
import Model.Member;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Memastikan GUI dijalankan di thread utama untuk Swing
        SwingUtilities.invokeLater(() -> {
            // Menampilkan form Login sebagai tampilan awal
            new LoginForm().setVisible(true);
        });
    }
}