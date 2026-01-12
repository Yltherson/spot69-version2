package com.spot69.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	//Connexion en Local

//	private static final String URL = "jdbc:mysql://localhost:3306/spot-69?useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8mb4&serverTimezone=UTC";
	private static final String URL = "jdbc:mysql://localhost:3306/Spot69?useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8mb4&serverTimezone=America/Port-au-Prince";
	private static final String USER = "root";
    private static final String PASSWORD = "";
    
////	Connexion a la vm
//	private static final String URL = "jdbc:mysql://localhost:3306/GestionDB?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8mb4&serverTimezone=America/Port-au-Prince";
//	private static final String USER = "administrator";
//	private static final String PASSWORD = "administrator@2026";


//	Connexion a la vm ebn local
//	private static final String URL = "jdbc:mysql://127.0.0.1:3307/Spot69?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8mb4&serverTimezone=UTC";
//	private static final String USER = "emanagement";
//	private static final String PASSWORD = "Emanagement2024@";



    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
     