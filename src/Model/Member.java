package Model;

import Controller.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Member {
    private int memberID;        // ID member yang otomatis ter-generate
    private String name;         // Nama member
    private String nim;          // Nomor Induk Mahasiswa (atau informasi lain yang relevan)
    private String contact;      // Kontak member
    private String password;     // Password member

    // Variabel statis untuk menyimpan data member yang sedang login
    private static Member loggedInMember;

    // Konstruktor untuk membuat objek Member baru (pada saat register)
    public Member(String name, String nim, String contact, String password) {
        this.name = name;
        this.nim = nim;
        this.contact = contact;
        this.password = password;
        this.memberID = generateUniqueMemberID();  // MemberID otomatis di-generate
    }

    // Getter dan Setter untuk Member
    public int getMemberID() {
        return memberID;
    }

    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }

    // Method untuk login, menyimpan member yang login ke dalam loggedInMember
    public static boolean login(int memberID, String password) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM member WHERE memberid = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Membuat objek Member dari data yang diambil dari database
                loggedInMember = new Member(
                    resultSet.getString("name"),
                    resultSet.getString("nim"),
                    resultSet.getString("contact"),
                    resultSet.getString("password")
                );
                loggedInMember.memberID = resultSet.getInt("memberid"); // Set memberID dari database
                return true; // Login berhasil
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Login gagal
    }

    // Method untuk mengambil member yang sedang login
    public static Member getLoggedInMember() {
        return loggedInMember;
    }

    // Method untuk logout, menghapus data member yang sedang login
    public static void logout() {
        loggedInMember = null;
    }

    // Method untuk register member baru
    public static boolean register(Member newMember) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "INSERT INTO member (name, nim, contact, password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, newMember.getName());
            statement.setString(2, newMember.getNim());
            statement.setString(3, newMember.getContact());
            statement.setString(4, newMember.getPassword());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newMember.memberID = generatedKeys.getInt(1); // Ambil ID yang dihasilkan dari database
                    }
                }
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk menghasilkan ID member yang unik secara acak
    private static int generateUniqueMemberID() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);  // ID antara 10000 sampai 99999
    }
}
