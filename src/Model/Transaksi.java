package Model;

import Controller.DatabaseConnection;
import java.util.Date;
import java.util.UUID;
import java.sql.*;

public class Transaksi {
   private String id_transaksi;
   private String id_member;
   private String id_barang; 
   private Date tgl_peminjaman;
   private Date tgl_pengembalian;
   private String status;

   public Transaksi(String id_member, String id_barang, Date tgl_peminjaman, Date tgl_pengembalian, String status) {
       this.id_transaksi = generateUniqueId();
       this.id_member = id_member;
       this.id_barang = id_barang;
       this.tgl_peminjaman = tgl_peminjaman;
       this.tgl_pengembalian = tgl_pengembalian;
       this.status = status;
   }

   public String getId_transaksi() {
       return id_transaksi;
   }

   public void setId_transaksi(String id_transaksi) {
       this.id_transaksi = id_transaksi;
   }

   public String getId_member() {
       return id_member;
   }

   public void setId_member(String id_member) {
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

   public static void addTransaksi(Transaksi transaksi) {
       if (isBarangAlreadyLoaned(transaksi.id_barang)) {
           System.out.println("Barang sudah dipinjam!");
           return;
       }

       try (Connection conn = DatabaseConnection.getConnection()) {
           String query = "INSERT INTO transaksi (id_transaksi, id_member, id_barang, tgl_peminjaman, tgl_pengembalian, status) VALUES (?, ?, ?, ?, ?, ?)";
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setString(1, transaksi.id_transaksi);
           stmt.setString(2, transaksi.id_member);
           stmt.setString(3, transaksi.id_barang);
           stmt.setDate(4, new java.sql.Date(transaksi.tgl_peminjaman.getTime()));
           stmt.setDate(5, new java.sql.Date(transaksi.tgl_pengembalian.getTime()));
           stmt.setString(6, transaksi.status);
           stmt.executeUpdate();
       } catch (SQLException e) {
           e.printStackTrace();
       }
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

   public static String generateUniqueId() {
       return "T" + UUID.randomUUID().toString();
   }

   public String getNamaMember() {
       try (Connection conn = DatabaseConnection.getConnection()) {
           String query = "SELECT nama FROM member WHERE id_member = ?";
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setString(1, this.id_member);
           ResultSet rs = stmt.executeQuery();
           if (rs.next()) {
               return rs.getString("nama");
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return "Unknown Member";
   }

   public String getNamaBarang() {
       try (Connection conn = DatabaseConnection.getConnection()) {
           String query = "SELECT nama_barang FROM inventory WHERE id_barang = ?";
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