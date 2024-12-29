package Model;

import Controller.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Inventory {
    private String idBarang;
    private String namaBarang;
    private String status;

    public Inventory(String idBarang, String namaBarang, String status) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.status = status;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static boolean isBarangAvailable(String idBarang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM inventory WHERE id_barang = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, idBarang);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("status").equals("available");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateStatus(String idBarang, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE inventory SET status = ? WHERE id_barang = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, status);
                stmt.setString(2, idBarang);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}