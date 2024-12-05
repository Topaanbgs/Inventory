package Model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private int id;
    private String name;
    private int quantity;
    private String desc;

    // Constructor
    public Inventory(int id, String name, int quantity, String desc) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.desc = desc;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    // Method untuk menambahkan barang baru ke inventaris
    public static Inventory addItem(int id, String name, int quantity, String desc) {
        System.out.println("Barang '" + name + "' berhasil ditambahkan ke inventaris.");
        return new Inventory(id, name, quantity, desc);
    }

    // Method untuk menghapus barang dari inventaris
    public void removeItem() {
        System.out.println("Barang '" + name + "' berhasil dihapus dari inventaris.");
    }

    // Method untuk memperbarui jumlah barang
    public void updateQuantity(int qty) {
        if (quantity + qty >= 0) {
            quantity += qty;
            System.out.println("Jumlah barang '" + name + "' berhasil diperbarui menjadi: " + quantity);
        } else {
            System.out.println("Transaksi gagal. Stok barang tidak mencukupi.");
        }
    }

    // Method untuk mengecek ketersediaan barang
    public boolean checkAvailability() {
        if (quantity > 0) {
            System.out.println("Barang '" + name + "' tersedia untuk dipinjam.");
            return true;
        } else {
            System.out.println("Barang '" + name + "' tidak tersedia untuk dipinjam.");
            return false;
        }
    }

    // Main method untuk uji coba
    public static void main(String[] args) {
        // Menambahkan barang baru ke inventaris
        Inventory item1 = Inventory.addItem(101, "Laptop", 5, "Laptop untuk presentasi");
        Inventory item2 = Inventory.addItem(102, "Proyektor", 2, "Proyektor multimedia");

        // Mengecek ketersediaan barang
        item1.checkAvailability();
        item2.checkAvailability();

        // Memperbarui jumlah barang
        item1.updateQuantity(-1); // Meminjam 1 laptop
        item2.updateQuantity(-2); // Meminjam 2 proyektor

        // Mengecek ketersediaan barang setelah update
        item1.checkAvailability();
        item2.checkAvailability();

        // Menghapus barang dari inventaris
        item2.removeItem();
    }

    public int getStock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public AbstractStringBuilder getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setStock(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static class AbstractStringBuilder {

        public AbstractStringBuilder() {
        }
    }
}
