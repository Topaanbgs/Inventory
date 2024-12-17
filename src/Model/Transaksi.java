package Model;

public class Transaksi {
    private String idTransaksi;
    private String namaMember;
    private String tanggalPeminjaman;
    private String tanggalPengembalian;
    private String namaBarang;

    public Transaksi(String idTransaksi, String namaMember, String tanggalPeminjaman, 
                     String tanggalPengembalian, String namaBarang) {
        this.idTransaksi = idTransaksi;
        this.namaMember = namaMember;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalian = tanggalPengembalian;
        this.namaBarang = namaBarang;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getNamaMember() {
        return namaMember;
    }

    public String getTanggalPeminjaman() {
        return tanggalPeminjaman;
    }

    public String getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    public String getNamaBarang() {
        return namaBarang;
    }
}