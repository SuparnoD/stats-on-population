package com.napier.sem;

import java.sql.*;

public class App {

    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Returns the population of the world to the user
        System.out.println("The world population is: " + a.getWorldPopulation());

        // Disconnect from database
        a.disconnect();
    }

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load Database driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public int getWorldPopulation() {
        int worldPopulation = 0;

        try{
            Statement stmt = con.createStatement();
            String strSelect =
                    "select sum(Population) from(select population from world.city union all select population from world.country) as population";
            ResultSet rset = stmt.executeQuery(strSelect);
            if(rset.next()){
                worldPopulation = rset.getInt("sum(Population)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return worldPopulation;
    }
}
