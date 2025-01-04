package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Controller.DatabaseConnection;

public class TransaksiPengembalian extends Transaksi {
    private static final double DENDA_PER_HARI = 5000.0;
    private double totalDenda = 0.0;
    
    public TransaksiPengembalian(int id_member, String id_barang, 
                                Date tgl_peminjaman, Date tgl_pengembalian, 
                                String status) {
        super(id_member, id_barang, tgl_peminjaman, tgl_pengembalian, status);
        validateDates(tgl_peminjaman, tgl_pengembalian);
    }

    public TransaksiPengembalian(int id_transaksi) {
        super(0, null, null, null, "kembali");
        if (id_transaksi <= 0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }
        setId_transaksi(id_transaksi);
        loadTransaksiData();
        calculateTotalDenda();
    }

    private void validateDates(Date tgl_peminjaman, Date tgl_pengembalian) {
        if (tgl_peminjaman == null || tgl_pengembalian == null) {
            throw new IllegalArgumentException("Tanggal peminjaman dan pengembalian tidak boleh kosong");
        }
        if (tgl_pengembalian.before(tgl_peminjaman)) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh sebelum tanggal peminjaman");
        }
    }

    private void loadTransaksiData() {
        String query = "SELECT t.*, SUM(d.jml_denda) as total_denda " +
                      "FROM transaksi t " +
                      "LEFT JOIN denda d ON t.id_transaksi = d.id_transaksi " +
                      "WHERE t.id_transaksi = ? " +
                      "GROUP BY t.id_transaksi";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, getId_transaksi());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    setId_member(rs.getInt("id_member"));
                    setId_barang(rs.getString("id_barang"));
                    setTgl_peminjaman(rs.getDate("tgl_peminjaman"));
                    setTgl_pengembalian(rs.getDate("tgl_pengembalian"));
                    setTgl_dikembalikan(rs.getDate("tgl_dikembalikan"));
                    setStatus(rs.getString("status"));
                    this.totalDenda = rs.getDouble("total_denda");
                } else {
                    throw new SQLException("Transaksi tidak ditemukan");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading transaksi data: " + e.getMessage(), e);
        }
    }

    private void calculateTotalDenda() {
        String query = "SELECT SUM(jml_denda) as total_denda FROM denda WHERE id_transaksi = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, getId_transaksi());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.totalDenda = rs.getDouble("total_denda");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total denda: " + e.getMessage(), e);
        }
    }

    private double calculateItemDenda(Date tglDikembalikan, Date tglPengembalian) {
        if (tglDikembalikan == null || tglPengembalian == null) {
            return 0.0;
        }

        if (tglDikembalikan.after(tglPengembalian)) {
            long diffInMillis = tglDikembalikan.getTime() - tglPengembalian.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            return diffInDays * DENDA_PER_HARI;
        }
        
        return 0.0;
    }

public boolean prosesKembaliParsial(String idBarang, Date tglDikembalikan) {
    if (tglDikembalikan == null) {
        throw new IllegalArgumentException("Tanggal pengembalian tidak boleh kosong");
    }

    Date tglSekarang = new Date();
    if (tglDikembalikan.after(tglSekarang)) {
        throw new IllegalArgumentException("Tanggal pengembalian tidak boleh melebihi tanggal sekarang");
    }

    // Get specific item's return date
    String sqlGetTglPengembalian = """
        SELECT tgl_pengembalian 
        FROM transaksi 
        WHERE id_transaksi = ? AND id_barang = ?""";
        
    Date itemTglPengembalian = null;
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlGetTglPengembalian)) {
        ps.setInt(1, getId_transaksi());
        ps.setString(2, idBarang);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                itemTglPengembalian = rs.getDate("tgl_pengembalian");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error getting item return date: " + e.getMessage());
    }

    setTgl_dikembalikan(tglDikembalikan);
    setStatus("kembali");

    double itemDenda = calculateItemDenda(tglDikembalikan, itemTglPengembalian);
    return updateTransaksiPengembalianParsial(idBarang, itemDenda);
}

    private boolean updateTransaksiPengembalianParsial(String idBarang, double itemDenda) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String updateQuery = "UPDATE transaksi SET status = ?, tgl_dikembalikan = ? " +
                               "WHERE id_transaksi = ? AND id_barang = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, getStatus());
                stmt.setDate(2, new java.sql.Date(getTgl_dikembalikan().getTime()));
                stmt.setInt(3, getId_transaksi());
                stmt.setString(4, idBarang);
                
                boolean success = stmt.executeUpdate() > 0;

                if (success && itemDenda > 0) {
                    success &= insertDenda(conn, idBarang, itemDenda);
                }

                if (success) {
                    conn.commit();
                    calculateTotalDenda();
                } else {
                    conn.rollback();
                }

                return success;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Error updating transaksi: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean insertDenda(Connection conn, String idBarang, double itemDenda) 
            throws SQLException {
        String query = "INSERT INTO denda (id_transaksi, id_barang, jml_denda, status_pembayaran) " +
                      "VALUES (?, ?, ?, 'belum')";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, getId_transaksi());
            stmt.setString(2, idBarang);
            stmt.setDouble(3, itemDenda);
            return stmt.executeUpdate() > 0;
        }
    }

    public double getTotalDenda() {
        return totalDenda;
    }

    public static List<TransaksiPengembalian> getAllTransaksiKembali() {
        List<TransaksiPengembalian> transaksiList = new ArrayList<>();
        String query = "SELECT DISTINCT t.id_transaksi FROM transaksi t " + 
                      "WHERE t.status = 'kembali'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    TransaksiPengembalian transaksi = new TransaksiPengembalian(rs.getInt("id_transaksi"));
                    transaksiList.add(transaksi);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transaksi kembali: " + e.getMessage(), e);
        }

        return transaksiList;
    }
}