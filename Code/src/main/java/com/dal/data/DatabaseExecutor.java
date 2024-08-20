package com.dal.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseExecutor {
    public void executeQuery(String remoteDBURL, String userQuery, String userDetails) {
        System.out.println("dburl is -"+remoteDBURL);
        System.out.println("userDetails username is -"+userDetails.split("##")[0]);
        System.out.println("userDetails password is -"+userDetails.split("##")[1]);
        try (Connection remoteConn = DriverManager.getConnection(remoteDBURL, userDetails.split("##")[0], userDetails.split("##")[1])) {
            try (PreparedStatement statement = remoteConn.prepareStatement(userQuery)) {
                boolean hasResultSet = statement.execute();
                if (hasResultSet) {
                    try (ResultSet resultSet = statement.getResultSet()) {
                        System.out.println("value of resultset -"+statement.getResultSet());
                    }
                } else {
                    int updatedRows = statement.getUpdateCount();
                    System.out.println("Query executed successfully. Updated rows: " + updatedRows);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
