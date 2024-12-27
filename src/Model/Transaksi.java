package Model;

import Controller.DatabaseConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaksi {
    private String idTransaksi;
    private String memberId;
    private String idBarang;
    private Date tanggalPinjam;
    private Date tanggalKembali;
    private String status; // "pinjam" atau "kembali"

    // Constructor yang memanggil generateUniqueId() jika idTransaksi tidak diberikan
    public Transaksi(String memberId, String idBarang, Date tanggalPinjam, Date tanggalKembali, String status) {
        this.idTransaksi = generateUniqueId();  // Generate ID otomatis
        this.memberId = memberId;
        this.idBarang = idBarang;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
    }

    // Constructor jika sudah memiliki idTransaksi
    public Transaksi(String idTransaksi, String memberId, String idBarang, Date tanggalPinjam, Date tanggalKembali, String status) {
        this.idTransaksi = idTransaksi;
        this.memberId = memberId;
        this.idBarang = idBarang;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
    }

    // Getter dan Setter
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public Date getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(Date tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(Date tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Method untuk mencatat transaksi peminjaman
    public static void addTransaksi(Transaksi transaksi) {
        // Memeriksa apakah barang sudah dipinjam
        if (isBarangAlreadyLoaned(transaksi.idBarang)) {
            System.out.println("Barang sudah dipinjam oleh orang lain!");
            return; // Kembalikan jika barang sudah dipinjam
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO transaksi (id_transaksi, member_id, id_barang, tanggal_pinjam, tanggal_kembali, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, transaksi.idTransaksi);
                stmt.setString(2, transaksi.memberId);
                stmt.setString(3, transaksi.idBarang);
                stmt.setDate(4, new java.sql.Date(transaksi.tanggalPinjam.getTime()));
                stmt.setDate(5, new java.sql.Date(transaksi.tanggalKembali.getTime()));
                stmt.setString(6, transaksi.status);
                stmt.executeUpdate();
                System.out.println("Transaksi berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk memeriksa apakah barang sudah dipinjam
    public static boolean isBarangAlreadyLoaned(String idBarang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM transaksi WHERE id_barang = ? AND status = 'pinjam'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, idBarang);
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Jika ada record dengan status "pinjam", berarti barang sudah dipinjam
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Barang tidak dipinjam
    }

    // Generate unique transaction ID
    public static String generateUniqueId() {
        // Generate a unique transaction ID using UUID
        return "T" + UUID.randomUUID().toString();
    }

    // Menambahkan method untuk mengambil nama member berdasarkan memberId
    public String getNamaMember() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nama FROM member WHERE member_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.memberId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nama");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Member";
    }

    // Menambahkan method untuk mengambil nama barang berdasarkan idBarang
    public String getNamaBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nama_barang FROM barang WHERE id_barang = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.idBarang);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nama_barang");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Item";
    }
}
