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
    public static void CompanyAdder(String sql, Scanner scanner, Statement stmt, int i) throws SQLException {
        System.out.println("Enter the company name:");
        sql = String.format("INSERT INTO COMPANY" +
                "(ID,NAME) VALUES (%d, '%s')", i, scanner.nextLine());
        stmt.executeUpdate(sql);
        System.out.println("The company was created!\n");
    }
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            conn = DriverManager.getConnection(DB_URL);
            //STEP 3: Execute a query
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS CUSTOMER";
            //stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS CAR";
            //stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS COMPANY";
            //stmt.executeUpdate(sql);


            sql =  "CREATE TABLE IF NOT EXISTS COMPANY (ID INTEGER PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255) UNIQUE NOT NULL)";
            stmt.executeUpdate(sql);
            sql =  "CREATE TABLE IF NOT EXISTS CAR (ID INTEGER PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255) UNIQUE NOT NULL," +
                    "COMPANY_ID INT NOT NULL," +
                    "Constraint fk_company FOREIGN KEY (COMPANY_ID)" +
                    "REFERENCES COMPANY(ID)" +
                    ")";
            stmt.executeUpdate(sql);

            sql =  "CREATE TABLE IF NOT EXISTS CUSTOMER (ID INTEGER PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255) UNIQUE NOT NULL," +
                    "RENTED_CAR_ID INT DEFAULT 0," +
                    "Constraint fk_car FOREIGN KEY (RENTED_CAR_ID)" +
                    "REFERENCES CAR(ID)" +
                    ")";

            //"constraint pk_car PRIMARY KEY (ID, COMPANY_ID),"+
            stmt.executeUpdate(sql);
            Scanner scanner = new Scanner(System.in);
            int i = 1;
            int l = 1;
            int idCustomer = 1;
            int id = 1;
            while (true) {
                System.out.println("1. Log in as a manager\n" +
                        "2. Log in as a customer\n" +
                        "3. Create a customer\n" +
                        "0. Exit");

                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    return;
                } else if (choice == 3) {
                    System.out.println("Enter the customer name:");
                    sql = String.format("INSERT INTO CUSTOMER" +
                            " VALUES (%d, '%s', null)", idCustomer, scanner.nextLine());
                    idCustomer++;
                    stmt.executeUpdate(sql);
                    System.out.println("The customer was added!");
                } else if (choice == 2) {
                    List<String> nameOfCustomers = new ArrayList<>();
                    try (ResultSet customers = stmt.executeQuery("SELECT * FROM CUSTOMER")) {
                        while (customers.next()) {
                            nameOfCustomers.add(customers.getString("NAME"));
                        }
                    }
                    if (nameOfCustomers.isEmpty()) {
                        System.out.println("The customer list is empty!");
                    } else {
                        int n = 1;
                        System.out.println("Customer list:");
                        for (String name :nameOfCustomers
                        ) {
                            System.out.println(n +". " + name);
                            n++;
                        }
                        System.out.println("0. Back");
                        n = Integer.parseInt(scanner.nextLine());
                        int idOfCustomer = n;
                        sql = String.format("SELECT * from CUSTOMER where ID = %d", n);
                        try (ResultSet resultSet = stmt.executeQuery(sql)) {
                            resultSet.next();
                            n = resultSet.getInt("RENTED_CAR_ID");
                        }
                        int newN = n;
                        Map<Integer, Integer> mapOfRentedCars = new HashMap<>();
                        while (true) {
                            System.out.println("1. Rent a car\n" +
                                    "2. Return a rented car\n" +
                                    "3. My rented car\n" +
                                    "0. Back");

                            int choiceCustomer = Integer.parseInt(scanner.nextLine());
                            if (choiceCustomer == 3) {
                                if (!mapOfRentedCars.containsKey(idCustomer)) {
                                    System.out.println("You didn't rent a car!");
                                } else {
                                    String nameOfCar = "";
                                    //mapOfRentedCars.remove(idCustomer);
                                    mapOfRentedCars.put(idCustomer, mapOfRentedCars.get(idCustomer));
                                    sql = String.format("SELECT * FROM CAR WHERE ID = %d", mapOfRentedCars.get(idCustomer));
                                    try (ResultSet resultSet = stmt.executeQuery(sql)) {
                                        resultSet.next();
                                        nameOfCar = resultSet.getString(2);
                                        n = resultSet.getInt(3);
                                    }

                                    System.out.println("Your rented car:");
                                    System.out.println(nameOfCar);
                                    System.out.println("Company:");
                                    sql = String.format("SELECT * FROM COMPANY WHERE ID = %d", n);
                                    try (ResultSet resultSet = stmt.executeQuery(sql)) {
                                        resultSet.next();
                                        nameOfCar = resultSet.getString(2);
                                    }
                                    System.out.println(nameOfCar);

                                }
                            } else if (choiceCustomer == 2) {
                                if (!mapOfRentedCars.containsKey(idCustomer)) {
                                    System.out.println("You didn't rent a car!");
                                } else if (mapOfRentedCars.get(idCustomer) == null) {
                                    System.out.println("You've returned a rented car!");
                                }else {
                                    sql = String.format("UPDATE CUSTOMER SET RENTED_CAR_ID" +
                                            " = %d WHERE ID = %d", null, idOfCustomer);
                                    stmt.executeUpdate(sql);
                                    System.out.println("You've returned a rented car!");
                                    //mapOfRentedCars.remove(idCustomer);

                                    //mapOfRentedCars.put(idOfCustomer, 0);
                                }

                            } else if (choiceCustomer == 1) {
                                if (mapOfRentedCars.containsKey(idCustomer)) {
                                    System.out.println("You've already rented a car!");
                                } else {
                                    System.out.println("Choose a company:");

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
                                    } else {
                                        System.out.println("0. Back");

                                        choice = Integer.parseInt(scanner.nextLine());
                                        sql = String.format("SELECT * FROM CAR WHERE " +
                                                "(COMPANY_ID = %d)", choice);
                                        ResultSet cars = stmt.executeQuery(sql);
                                        Map<Integer, String> mapOfCars = new HashMap<>();
                                        while (cars.next()) {
                                            mapOfCars.put(
                                                    cars.getInt("ID"),
                                                    cars.getString("NAME"));
                                        }
                                        cars.close();

                                        int j = 1;
                                        System.out.println("Choose a car:");
                                        for (Map.Entry<Integer, String> map : mapOfCars.entrySet()
                                        ) {
                                            if (!mapOfRentedCars.containsValue(map.getKey())) {
                                                System.out.println(j + ". " + map.getValue());
                                                j++;
                                            }
                                        }
                                        if (!mapOfRentedCars.isEmpty()) {
                                            Map.Entry<Integer,Integer> entry = mapOfRentedCars.entrySet().iterator().next();
                                            int key = entry.getKey();

                                            int value = entry.getValue();
                                            mapOfCars.remove(value);
                                        }

                                        System.out.println("0. Back");
                                        j = Integer.parseInt(scanner.nextLine());
                                        sql = String.format("UPDATE CUSTOMER SET RENTED_CAR_ID" +
                                                " = %d WHERE ID = %d", j, idOfCustomer);
                                        stmt.executeUpdate(sql);
                                        /**
                                         * (TO DO) delete this car from the table !
                                         */
                                        System.out.println("You rented '" + mapOfCars.get(j) + "'");
                                        mapOfRentedCars.put(idOfCustomer, j);

                                    }
                                }
                            }


                        }
                    }

                } else if (choice == 1) {
                    while (true) {
                        System.out.println("\n1. Company list\n" +
                                "2. Create a company\n" +
                                "0. Back");
                        choice = Integer.parseInt(scanner.nextLine());
                        if (choice == 0) {
                            break;
                        } else if (choice == 2) {

                            CompanyAdder(sql, scanner, stmt, l);
                            l++;
                        } else {
                            System.out.println("\nChoose the company:");
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
                            } else {
                                System.out.println("0. Back");
                                choice = Integer.parseInt(scanner.nextLine());
                                while (true) {
                                    if (choice == 0) {
                                        break;
                                    } else {
                                        id = choice;
                                        System.out.printf("'%s' company%n", mapOfCompanies.get(id));
                                        while (true) {
                                            System.out.println("\n1. Car list\n" +
                                                    "2. Create a car\n" +
                                                    "0. Back");
                                            choice = Integer.parseInt(scanner.nextLine());
                                            sql = String.format("SELECT * FROM CAR WHERE " +
                                                    "(COMPANY_ID = %d)", id);
                                            ResultSet cars = stmt.executeQuery(sql);
                                            Map<Integer, String> mapOfCars = new HashMap<>();
                                            while (cars.next()) {
                                                mapOfCars.put(
                                                        cars.getInt("ID"),
                                                        cars.getString("NAME"));
                                            }
                                            cars.close();
                                            if (choice == 0) {
                                                break;
                                            } else if (choice == 2) {
                                                System.out.println("\nEnter the car name:");
                                                sql = String.format("INSERT INTO CAR" +
                                                        " VALUES (%d, '%s', %d)", i, scanner.nextLine(), id);
                                                i++;
                                                stmt.executeUpdate(sql);
                                                System.out.println("The car was added!");
                                            } else if (choice == 1){
                                                int j = 0;
                                                for (Map.Entry<Integer, String> map : mapOfCars.entrySet()
                                                ) {
                                                    j++;
                                                    System.out.println(j + ". " + map.getValue());
                                                }
                                                if (mapOfCars.isEmpty()) {
                                                    System.out.println("The car list is empty!");
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {

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