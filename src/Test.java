
import Controller.DatabaseConnection;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
public class Test {
        public static void main(String[] args) {
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Koneksi ke database berhasil!");
            }
        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
    }
}
