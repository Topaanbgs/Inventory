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
        super(0, null, null, null, "kembali");  // id_member diatur ke 0 untuk memastikan ID valid
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
        setId_member(rs.getInt("id_member"));  // Menggunakan int untuk id_member
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

    public boolean prosesKembaliParsial(String idBarang, Date tglDikembalikan) {
        if (tglDikembalikan == null) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh kosong");
        }

        Date tglSekarang = new Date();
        if (tglDikembalikan.after(tglSekarang)) {
            throw new IllegalArgumentException("Tanggal pengembalian tidak boleh melebihi tanggal sekarang");
        }

        setTgl_dikembalikan(tglDikembalikan);
        setStatus("kembali");
        calculateDenda();

        return updateTransaksiPengembalianParsial(idBarang);
    }

    private boolean updateTransaksiPengembalianParsial(String idBarang) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String updateQuery = "UPDATE transaksi SET status = ?, tgl_dikembalikan = ? " +
                                 "WHERE id_transaksi = ? AND id_barang = ?";

            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, getStatus());
            stmt.setDate(2, new java.sql.Date(getTgl_dikembalikan().getTime()));
            stmt.setInt(3, getId_transaksi());
            stmt.setString(4, idBarang);

            boolean success = stmt.executeUpdate() > 0;

            if (success && totalDenda > 0) {
                success &= insertDendaIfNeeded(conn);
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return success;

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

    public static List<TransaksiPengembalian> getAllReturnedItems(int idTransaksi) {
        List<TransaksiPengembalian> transaksiList = new ArrayList<>();
        String query = "SELECT * FROM transaksi WHERE id_transaksi = ? AND status = 'kembali'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idTransaksi);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                try {
                    TransaksiPengembalian transaksi = new TransaksiPengembalian(rs.getInt("id_transaksi"));
                    transaksiList.add(transaksi);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching returned items: " + e.getMessage(), e);
        }

        return transaksiList;
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