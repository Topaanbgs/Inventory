package Model;

public class Member {
    private static int idCounter = 1;
    private String id;
    private String name;
    private String nim;
    private String contact;
    private String password;

    public Member(String name, String nim, String contact, String password) {
        this.id = generateID();
        this.name = name;
        this.nim = nim;
        this.contact = contact;
        this.password = password;
    }

    private String generateID() {
        return String.format("%03d", idCounter++);
    }

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