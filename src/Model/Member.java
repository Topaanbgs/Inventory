package Model;

import Controller.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Member {
    private int memberID;
    private String name;
    private String nim;
    private String contact;
    private String password;

    public Member(String name, String nim, String contact, String password) {
        this.name = name;
        this.nim = nim;
        this.contact = contact;
        this.password = password;
    }

    public int getMemberID() {
        return memberID;
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

    public static boolean login(int memberID, String password) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM member WHERE memberid = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, memberID);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(Member newMember) throws SQLException {
    Connection connection = DatabaseConnection.getConnection();
    String query = "INSERT INTO member (name, nim, contact, password) VALUES (?, ?, ?, ?)";
    
    try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, newMember.getName());
        statement.setString(2, newMember.getNim());
        statement.setString(3, newMember.getContact());
        statement.setString(4, newMember.getPassword());
        
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newMember.memberID = generatedKeys.getInt(1); // Ambil ID yang dihasilkan
                }
            }
        }
        return rowsInserted > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    private static int generateUniqueMemberID() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }
}
