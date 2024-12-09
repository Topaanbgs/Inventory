package Model;

import java.util.Date;

public class Transaction {
    private final int transactionId;
    private final Date borrowDate;
    private Date returnDate;
    private final Inventory item;
    private final Member member;

    // Constructor
    public Transaction(int transactionId, Inventory item, Member member) {
        this.transactionId = transactionId;
        this.borrowDate = new Date(); // Mengambil tanggal saat ini
        this.returnDate = null; // Default null, akan diisi saat barang dikembalikan
        this.item = item;
        this.member = member;
    }

    // Getter dan Setter
    public int getTransactionId() {
        return transactionId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Inventory getItem() {
        return item;
    }

    public Member getMember() {
        return member;
    }

    // Method untuk mencatat peminjaman barang
    public void borrowItem() {
        if (item.checkAvailability()) {
            item.updateQuantity(-1); // Mengurangi stok barang
            member.borrowItem(item.getName()); // Menambahkan barang ke daftar pinjaman anggota
            System.out.println("Transaksi peminjaman berhasil: " + 
                "Anggota " + member.getName() + " meminjam barang " + item.getName());
        } else {
            System.out.println("Barang tidak tersedia untuk dipinjam.");
        }
    }

    // Method untuk mencatat pengembalian barang
    public void returnItem() {
        this.returnDate = new Date(); // Mengambil tanggal saat ini sebagai tanggal pengembalian
        item.updateQuantity(1); // Menambahkan stok barang
        System.out.println("Barang '" + item.getName() + "' telah dikembalikan oleh " + member.getName());
    }

    // Method untuk menghitung denda keterlambatan pengembalian barang
    public double calculateLateFee() {
        if (returnDate == null) {
            System.out.println("Barang belum dikembalikan. Tidak ada denda.");
            return 0.0;
        }
        long diffInMillies = Math.abs(returnDate.getTime() - borrowDate.getTime());
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        double lateFee = 0.0;
        if (diffInDays > 7) { // Asumsi batas waktu peminjaman adalah 7 hari
            lateFee = (diffInDays - 7) * 5000; // Denda Rp5.000 per hari keterlambatan
        }
        System.out.println("Denda keterlambatan: Rp " + lateFee);
        return lateFee;
    }
}
