import View.LoginForm;
import View.RegisterForm;
import View.DashboardForm;
import View.PeminjamanForm;
import View.TransaksiForm;
import Model.Member;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // List global untuk menyimpan data member sementara
    public static final List<Member> memberList = new ArrayList<>();
    
    // Member yang sedang login
    public static Member currentMember = null;

    public static void main(String[] args) {
        // Memastikan GUI dijalankan di thread utama untuk Swing
        SwingUtilities.invokeLater(() -> {
            // Menampilkan form Login sebagai tampilan awal
            new LoginForm().setVisible(true);
        });
    }
}
