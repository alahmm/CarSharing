

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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
    public static void main(String[] args) {

        String dirPath = "src/carsharing/db/carsharing.mv.db";
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

            sql =  "CREATE TABLE COMPANY (ID INTEGER not NULL, NAME VARCHAR(255))";
            stmt.executeUpdate(sql);
            if (args.length > 1) {
                File file = new File(dirPath + args[1]);
            } else {
                File file = new File(dirPath + "carsharing");

            }

/*            String fileName = "";
            if (args.length > 1) {
                fileName = args[1];
            } else {
                Random rand = new Random();
                fileName = generateString(rand, "newfile", 7);
            }*/
            //File file = new File("jdbc:h2:." + File.separator + "src" + "carsharing" +
            //       File.separator + "db" + File.separator + fileName);

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
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