package Model;

public class Member {
    private static int idCounter = 1; // Variabel statis untuk menghitung ID
    private String id;
    private String name;
    private String nim;
    private String contact;
    private String password;

    // Constructor
    public Member(String name, String nim, String contact, String password) {
        this.id = generateID(); // Menghasilkan ID dengan format 001, 002, dll.
        this.name = name;
        this.nim = nim;
        this.contact = contact;
        this.password = password;
    }

    // Menghasilkan ID dengan padding nol di depan
    private String generateID() {
        return String.format("%03d", idCounter++); // Format angka menjadi 3 digit dengan padding nol
    }

    // Getter untuk Member
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNim() {
        return nim;
    }

    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }
}