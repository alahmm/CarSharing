package carsharing;

import java.io.File;
import java.sql.*;
import java.util.*;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/carsharing";

    //  Database credentials
    static final String USER = "user";
    static final String PASS = "1919";
    public static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
    public static void main(String[] args) throws SQLException {

        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            conn = DriverManager.getConnection(DB_URL);
            //STEP 3: Execute a query
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS COMPANY";
            stmt.executeUpdate(sql);

            sql =  "CREATE TABLE COMPANY (ID INTEGER PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255) UNIQUE NOT NULL)";
            stmt.executeUpdate(sql);
            Scanner scanner = new Scanner(System.in);
            int i = 0;
            while (true) {
                System.out.println("1. Log in as a manager\n" +
                        "0. Exit");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    return;
                } else {
                    while (true) {
                        System.out.println("\n1. Company list\n" +
                                "2. Create a company\n" +
                                "0. Back");
                        choice = Integer.parseInt(scanner.nextLine());
                        if (choice == 0) {
                            break;
                        } else if (choice == 2) {
                            System.out.println("Enter the company name:");
                            sql = String.format("INSERT INTO COMPANY" +
                                    "(ID,NAME) VALUES (%d, '%s')", ++i, scanner.nextLine());
                            stmt.executeUpdate(sql);
                            System.out.println("The company was created!\n");
                        } else {
                            ResultSet companies = stmt.executeQuery("SELECT * FROM COMPANY");
                            Map<Integer, String> mapOfCompanies = new HashMap<>();

                            while (companies.next()) {
                                mapOfCompanies.put(
                                        companies.getInt("ID"),
                                        companies.getString("NAME"));
                                System.out.println(companies.getInt("ID") + ". " +
                                        companies.getString("NAME"));

                            }
                            companies.close();
                            if (mapOfCompanies.isEmpty()) {
                                System.out.println("The company list is empty");
                            }
                        }
                    }
                }

            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        // STEP 4: Clean-up environment
        catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException ignored) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        }
    }
}