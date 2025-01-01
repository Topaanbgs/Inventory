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
    
    // Constructor dengan validasi parameter
    public TransaksiPengembalian(String id_member, String id_barang, 
                                Date tgl_peminjaman, Date tgl_pengembalian, 
                                String status) {
        super(id_member, id_barang, tgl_peminjaman, tgl_pengembalian, status);
        validateDates(tgl_peminjaman, tgl_pengembalian);
    }
    
    public TransaksiPengembalian(int id_transaksi) {
        super(null, null, null, null, "kembali");
        if (id_transaksi <= 0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }
        setId_transaksi(id_transaksi);
        loadTransaksiData();
        calculateDendaIfNeeded();
    }

    private void validateDates(Date tgl_peminjaman, Date tgl_pengembalian) {
        if (tgl_peminjaman == null || tgl_pengembalian == null) {
            throw new IllegalArgumentException("Tanggal peminjaman dan pengembalian tidak boleh kosong");
        }
        if (tgl_pengembalian.before(tgl_peminjaman)) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh sebelum tanggal peminjaman");
        }
    }

    private void calculateDendaIfNeeded() {
        if (getTgl_dikembalikan() != null) {
            calculateDenda();
        }
    }

    private void loadTransaksiData() {
        String query = "SELECT t.*, d.jml_denda FROM transaksi t " +
                      "LEFT JOIN denda d ON t.id_transaksi = d.id_transaksi " +
                      "WHERE t.id_transaksi = ?";
                      
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, getId_transaksi());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    loadDataFromResultSet(rs);
                } else {
                    throw new SQLException("Transaksi tidak ditemukan");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading transaksi data: " + e.getMessage(), e);
        }
    }

    private void loadDataFromResultSet(ResultSet rs) throws SQLException {
        setId_member(rs.getString("id_member"));
        setId_barang(rs.getString("id_barang"));
        setTgl_peminjaman(rs.getDate("tgl_peminjaman"));
        setTgl_pengembalian(rs.getDate("tgl_pengembalian"));
        setTgl_dikembalikan(rs.getDate("tgl_dikembalikan"));
        setStatus(rs.getString("status"));
        this.totalDenda = rs.getDouble("jml_denda");
    }

    private void calculateDenda() {
        Date tglDikembalikan = getTgl_dikembalikan();
        Date tglPengembalian = getTgl_pengembalian();
        
        if (tglDikembalikan == null || tglPengembalian == null) {
            totalDenda = 0.0;
            return;
        }

        if (tglDikembalikan.after(tglPengembalian)) {
            long diffInMillis = tglDikembalikan.getTime() - tglPengembalian.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            totalDenda = diffInDays * DENDA_PER_HARI;
        } else {
            totalDenda = 0.0;
        }
    }

    public boolean prosesKembali(Date tgl_dikembalikan) {
        if (tgl_dikembalikan == null) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh kosong");
        }
        
        Date tglSekarang = new Date();
        if (tgl_dikembalikan.after(tglSekarang)) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh melebihi tanggal sekarang");
        }

        setTgl_dikembalikan(tgl_dikembalikan);
        setStatus("kembali");
        calculateDenda();
        return updateTransaksiPengembalian();
    }

    private boolean updateTransaksiPengembalian() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            boolean success = updateTransaksiStatus(conn) && insertDendaIfNeeded(conn);
            
            if (success) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
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
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean updateTransaksiStatus(Connection conn) throws SQLException {
        String updateQuery = "UPDATE transaksi SET status = ?, tgl_dikembalikan = ? WHERE id_transaksi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, getStatus());
            stmt.setDate(2, new java.sql.Date(getTgl_dikembalikan().getTime()));
            stmt.setInt(3, getId_transaksi());
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean insertDendaIfNeeded(Connection conn) throws SQLException {
        if (totalDenda > 0) {
            String dendaQuery = "INSERT INTO denda (id_transaksi, jml_denda) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(dendaQuery)) {
                stmt.setInt(1, getId_transaksi());
                stmt.setDouble(2, totalDenda);
                return stmt.executeUpdate() > 0;
            }
        }
        return true;
    }

    public static List<TransaksiPengembalian> getAllTransaksiKembali() {
        List<TransaksiPengembalian> transaksiList = new ArrayList<>();
        String query = "SELECT id_transaksi FROM transaksi WHERE status = 'kembali'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                try {
                    TransaksiPengembalian transaksi = new TransaksiPengembalian(rs.getInt("id_transaksi"));
                    transaksiList.add(transaksi);
                } catch (RuntimeException e) {
                    // Log error but continue processing other records
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transaksi kembali: " + e.getMessage(), e);
        }
        
        return transaksiList;
    }

    public double getTotalDenda() {
        return totalDenda;
    }
}