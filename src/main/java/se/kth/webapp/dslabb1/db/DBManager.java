package se.kth.webapp.dslabb1.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    private static DBManager instance;
    private Connection connection;

    private static DBManager getInstance(){
        if(instance == null){
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://78.72.148.32:3306/webshop?user=admin&password=admin");
        } catch (Exception e) {e.printStackTrace();}
    }

    public static Connection getConnection(){
        return getInstance().connection;
    }
}
