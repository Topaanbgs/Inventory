package Model;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static final Map<String, String> inventory = new HashMap<>();

    // Static initializer untuk mengisi data
    static {
        inventory.put("Kamera Sony", "ID001");
        inventory.put("Kamera Canon", "ID002");
        inventory.put("Proyektor Epson", "ID003");
        inventory.put("Proyektor Yaber", "ID004");
        inventory.put("Lensa 80-200mm", "ID005");
        inventory.put("Lensa 55-200mm", "ID006");
        inventory.put("Memory Card 4gb", "ID007");
        inventory.put("Memory Card 16gb", "ID008");
    }

    // Method untuk mendapatkan ID Barang berdasarkan Nama Barang
    public static String getIdByName(String namaBarang) {
        return inventory.getOrDefault(namaBarang, "ID_UNKNOWN");
    }
}
