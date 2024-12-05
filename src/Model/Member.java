package Model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String id;
    private String name;
    private String NIM;
    private String contactInfo;
    private final List<String> borrowedItems; // Untuk menyimpan daftar barang yang dipinjam.

    // Constructor
    public Member(String id, String name, String NIM, String contactInfo) {
        this.id = id;
        this.name = name;
        this.NIM = NIM;
        this.contactInfo = contactInfo;
        this.borrowedItems = new ArrayList<>();
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNIM() {
        return NIM;
    }

    public void setNIM(String NIM) {
        this.NIM = NIM;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // Method untuk mendaftarkan anggota
    public void register() {
        System.out.println("Anggota dengan ID: " + id + " dan nama: " + name + " telah berhasil terdaftar.");
    }

    // Method untuk menambahkan item ke daftar pinjaman
    public void borrowItem(String item) {
        borrowedItems.add(item);
        System.out.println("Item '" + item + "' telah berhasil dipinjam oleh " + name);
    }

    // Method untuk menampilkan daftar barang yang dipinjam
    public void viewBorrowedItems() {
        System.out.println("Daftar barang yang sedang dipinjam oleh " + name + ":");
        if (borrowedItems.isEmpty()) {
            System.out.println("Tidak ada barang yang sedang dipinjam.");
        } else {
            for (String item : borrowedItems) {
                System.out.println("- " + item);
            }
        }
    }

    // Main method untuk uji coba
    public static void main(String[] args) {
        Member member1 = new Member("001", "Ali", "12345678", "ali@example.com");
        member1.register();
        member1.borrowItem("Laptop");
        member1.borrowItem("Proyektor");
        member1.viewBorrowedItems();
    }

    public Object getPassword() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
