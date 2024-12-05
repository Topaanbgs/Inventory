import Model.Inventory;
import Model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Main {
    private static final List<Member> memberList = new ArrayList<>(); // Daftar anggota
    private static final List<Inventory> inventoryList = new ArrayList<>(); // Daftar inventaris

    public static void main(String[] args) {
        // Inisialisasi data inventaris
        inventoryList.add(Inventory.addItem(101, "Laptop", 5, "Laptop untuk presentasi"));
        inventoryList.add(Inventory.addItem(102, "Proyektor", 2, "Proyektor multimedia"));

        // Inisialisasi data anggota
        memberList.add(new Member("001", "Ali", "12345678", "ali@example.com"));
        memberList.add(new Member("002", "Budi", "87654321", "budi@example.com"));

        // Menampilkan form login
        new LoginForm();
    }

    // Kelas LoginForm
    static class LoginForm extends JFrame {
        public LoginForm() {
            setTitle("Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);

            // Label login
            JLabel loginLabel = new JLabel("LOGIN");
            loginLabel.setBounds(150, 10, 100, 30);
            loginLabel.setFont(new Font("Arial", Font.BOLD, 18));
            loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(loginLabel);

            // Username label dan text field
            JLabel usernameLabel = new JLabel("Username");
            usernameLabel.setBounds(50, 60, 100, 25);
            add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setBounds(150, 60, 180, 25);
            add(usernameField);

            // Password label dan password field
            JLabel passwordLabel = new JLabel("Password");
            passwordLabel.setBounds(50, 100, 100, 25);
            add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(150, 100, 180, 25);
            add(passwordField);

            // Tombol login
            JButton loginButton = new JButton("LOGIN");
            loginButton.setBounds(150, 140, 180, 30);
            add(loginButton);

            // Link untuk registrasi
            JLabel registerLabel = new JLabel("<html><font color='red'>Belum Punya Akun? Silahkan Register</font></html>");
            registerLabel.setBounds(50, 180, 300, 25);
            add(registerLabel);

            // Tombol registrasi
            JButton registerButton = new JButton("REGISTER");
            registerButton.setBounds(150, 210, 180, 30);
            add(registerButton);

            // Action Listener untuk tombol login
            loginButton.addActionListener((ActionEvent e) -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validasi login
                boolean loginSuccess = false;
                for (Member member : memberList) {
                    if (member.getName().equals(username) && member.getPassword().equals(password)) {
                        loginSuccess = true;
                        JOptionPane.showMessageDialog(null, "Login Berhasil, Selamat Datang " + member.getName() + "!");
                        dispose(); // Menutup form login
                        runApp(member); // Menjalankan aplikasi utama
                        break;
                    }
                }

                if (!loginSuccess) {
                    JOptionPane.showMessageDialog(null, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Action Listener untuk tombol registrasi
            registerButton.addActionListener((ActionEvent e) -> {
                dispose(); // Menutup form login
                RegisterForm registerForm = new RegisterForm(); // Membuka form registrasi
            });

            setVisible(true);
        }

        private void runApp(Member member) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    // Kelas RegisterForm
    static class RegisterForm extends JFrame {
        public RegisterForm() {
            setTitle("Register");
            setSize(400, 350);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);

            // Label register
            JLabel registerLabel = new JLabel("REGISTER");
            registerLabel.setBounds(150, 10, 100, 30);
            registerLabel.setFont(new Font("Arial", Font.BOLD, 18));
            registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(registerLabel);

            // Nama label dan text field
            JLabel nameLabel = new JLabel("Nama");
            nameLabel.setBounds(50, 60, 100, 25);
            add(nameLabel);

            JTextField nameField = new JTextField();
            nameField.setBounds(150, 60, 180, 25);
            add(nameField);

            // NIM label dan text field
            JLabel nimLabel = new JLabel("NIM");
            nimLabel.setBounds(50, 100, 100, 25);
            add(nimLabel);

            JTextField nimField = new JTextField();
            nimField.setBounds(150, 100, 180, 25);
            add(nimField);

            // Kontak label dan text field
            JLabel contactLabel = new JLabel("Kontak");
            contactLabel.setBounds(50, 140, 100, 25);
            add(contactLabel);

            JTextField contactField = new JTextField();
            contactField.setBounds(150, 140, 180, 25);
            add(contactField);

            // Tombol register
            JButton registerButton = new JButton("REGISTER");
            registerButton.setBounds(150, 180, 180, 30);
            add(registerButton);

            // Link untuk login
            JLabel loginLabel = new JLabel("<html><font color='red'>Sudah Punya Akun? Silahkan Login</font></html>");
            loginLabel.setBounds(50, 220, 300, 25);
            add(loginLabel);

            // Tombol login
            JButton loginButton = new JButton("LOGIN");
            loginButton.setBounds(150, 250, 180, 30);
            add(loginButton);

            // Action Listener untuk tombol register
            registerButton.addActionListener((ActionEvent e) -> {
                String name = nameField.getText();
                String nim = nimField.getText();
                String contact = contactField.getText();

                if (!name.isEmpty() && !nim.isEmpty() && !contact.isEmpty()) {
                    memberList.add(new Member(nim, name, "defaultPassword", contact)); // Default password
                    JOptionPane.showMessageDialog(null, "Registrasi Berhasil! Silahkan Login.");
                    dispose(); // Menutup form registrasi
                    new LoginForm(); // Membuka form login
                } else {
                    JOptionPane.showMessageDialog(null, "Harap isi semua field!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Action Listener untuk tombol login
            loginButton.addActionListener((ActionEvent e) -> {
                dispose(); // Menutup form registrasi
                new LoginForm(); // Membuka form login
            });

            setVisible(true);
        }
    }
}