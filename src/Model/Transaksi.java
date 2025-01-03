package Model;

import Controller.DatabaseConnection;
import java.util.Date;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class Transaksi {
    private int id_transaksi;
    private int id_member;
    private String id_barang; 
    private Date tgl_peminjaman;
    private Date tgl_pengembalian;
    private String status;   
    private Date tgl_dikembalikan;

    private static class TransactionSequenceGenerator {
        private static final Object lock = new Object();
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");
        
        private static String currentDate = "";
        private static int currentSequence = 0;
        
        public static int generateUniqueTransaksiID(boolean isNewTransaction) {
            synchronized (lock) {
                if (isNewTransaction) {
                    String today = DATE_FORMAT.format(new Date());
                    
                    if (!today.equals(currentDate)) {
                        currentDate = today;
                        currentSequence = getLastSequenceFromDB(today);
                    }
                    
                    currentSequence++;
                    if (currentSequence > 999) {
                        throw new RuntimeException("Sequence limit exceeded for today");
                    }
                }
                return Integer.parseInt(currentDate + String.format("%03d", currentSequence));
            }
        }
        
        private static int getLastSequenceFromDB(String date) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT id_transaksi FROM transaksi " +
                              "WHERE CAST(id_transaksi AS CHAR) LIKE ? " +
                              "ORDER BY id_transaksi DESC LIMIT 1";
                
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, date + "%");
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String lastId = String.valueOf(rs.getInt("id_transaksi"));
                    return Integer.parseInt(lastId.substring(lastId.length() - 3));
                }
                return 0;
                
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    public Transaksi() {}

    public Transaksi(int id_member, String id_barang, Date tgl_peminjaman, Date tgl_pengembalian, String status) {
        this.id_transaksi = TransactionSequenceGenerator.generateUniqueTransaksiID(true);
        this.id_member = id_member;
        this.id_barang = id_barang;
        this.tgl_peminjaman = tgl_peminjaman;
        this.tgl_pengembalian = tgl_pengembalian;
        this.status = status;
    }

    // Getter dan Setter untuk setiap field
    public int getId_transaksi() {
        return id_transaksi;
    }

    public void setId_transaksi(int id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public int getId_member() {
        return id_member;
    }

    public void setId_member(int id_member) {
        this.id_member = id_member;
    }

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public Date getTgl_peminjaman() {
        return tgl_peminjaman;
    }

    public void setTgl_peminjaman(Date tgl_peminjaman) {
        this.tgl_peminjaman = tgl_peminjaman;
    }

    public Date getTgl_pengembalian() {
        return tgl_pengembalian;
    }

    public void setTgl_pengembalian(Date tgl_pengembalian) {
        this.tgl_pengembalian = tgl_pengembalian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getTgl_dikembalikan() {
        return tgl_dikembalikan;
    }
    
    public void setTgl_dikembalikan(Date tgl_dikembalikan) {
        this.tgl_dikembalikan = tgl_dikembalikan;
    }

    public boolean addMultipleTransaksi(List<String> barangIds, List<Date> tanggalPinjams, List<Date> tanggalKembalis) {
    try (Connection connection = DatabaseConnection.getConnection()) {
        connection.setAutoCommit(false);
        
        String query = "INSERT INTO transaksi (id_transaksi, id_member, id_barang, tgl_peminjaman, tgl_pengembalian, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Use single transaction ID for all items
            this.id_transaksi = TransactionSequenceGenerator.generateUniqueTransaksiID(true);
            
            for (int i = 0; i < barangIds.size(); i++) {
                statement.setInt(1, this.id_transaksi);
                statement.setInt(2, this.id_member);
                statement.setString(3, barangIds.get(i));
                statement.setDate(4, new java.sql.Date(tanggalPinjams.get(i).getTime()));
                statement.setDate(5, new java.sql.Date(tanggalKembalis.get(i).getTime()));
                statement.setString(6, "pinjam");
                statement.addBatch();
            }
            
            statement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            if (e instanceof BatchUpdateException) {
                // Handle case where item is already borrowed
                throw new SQLException("Barang sudah dipinjam");
            }
            throw e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public static List<Transaksi> getTransaksiByIdTransaksi(int idTransaksi) {
        List<Transaksi> transaksiList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM transaksi WHERE id_transaksi = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idTransaksi);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaksi transaksi = new Transaksi();
                transaksi.setId_transaksi(rs.getInt("id_transaksi"));
                transaksi.setId_member(rs.getInt("id_member"));
                transaksi.setId_barang(rs.getString("id_barang"));
                transaksi.setTgl_peminjaman(rs.getDate("tgl_peminjaman"));
                transaksi.setTgl_pengembalian(rs.getDate("tgl_pengembalian"));
                transaksi.setTgl_dikembalikan(rs.getDate("tgl_dikembalikan"));
                transaksi.setStatus(rs.getString("status"));
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaksiList;
    }

    public static List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM transaksi";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaksi transaksi = new Transaksi();
                transaksi.setId_transaksi(rs.getInt("id_transaksi"));
                transaksi.setId_member(rs.getInt("id_member"));
                transaksi.setId_barang(rs.getString("id_barang"));
                transaksi.setTgl_peminjaman(rs.getDate("tgl_peminjaman"));
                transaksi.setTgl_pengembalian(rs.getDate("tgl_pengembalian"));
                transaksi.setTgl_dikembalikan(rs.getDate("tgl_dikembalikan"));
                transaksi.setStatus(rs.getString("status"));
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaksiList;
    }

    public static boolean isBarangAlreadyLoaned(String id_barang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM transaksi WHERE id_barang = ? AND status = 'pinjam'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id_barang);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getNamaMember() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT name FROM member WHERE memberid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, this.id_member);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Member";
    }

    public String getNamaBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nama_barang FROM inventory WHERE inventoryid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.id_barang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nama_barang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Item";
    }
}