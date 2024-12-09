package Model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String id;
    private String name;
    private String NIM;
    private String contactInfo;
    private String password; // Tambahan untuk menyimpan password
    private final List<String> borrowedItems; // Untuk menyimpan daftar barang yang dipinjam.

    // Static list untuk menyimpan semua data member yang terdaftar
    private static final List<Member> registeredMembers = new ArrayList<>();

    // Constructor
    public Member(String id, String name, String NIM, String contactInfo, String password) {
        this.id = id;
        this.name = name;
        this.NIM = NIM;
        this.contactInfo = contactInfo;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method untuk mendaftarkan anggota
    public void register() {
        registeredMembers.add(this); // Menyimpan data member ke dalam daftar
        System.out.println("Anggota dengan ID: " + id + " dan nama: " + name + " telah berhasil terdaftar.");
    }

    // Method untuk login
    public static Member login(String id, String password) {
        for (Member member : registeredMembers) {
            if (member.getId().equals(id) && member.getPassword().equals(password)) {
                System.out.println("Login berhasil. Selamat datang, " + member.getName() + "!");
                return member; // Mengembalikan objek Member yang berhasil login
            }
        }
        System.out.println("Login gagal. ID atau password salah.");
        return null; // Mengembalikan null jika login gagal
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
}