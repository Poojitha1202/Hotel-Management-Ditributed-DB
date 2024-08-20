package com.dal.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SiteIpFetcher {
    public String fetchSiteIP(String localDBURL, String tableName, String userDetails) {
        System.out.println("dburl is -"+localDBURL);
        System.out.println("userDetails username is -"+userDetails.split("##")[0]);
        System.out.println("userDetails password is -"+userDetails.split("##")[1]);
        try (Connection localConn = DriverManager.getConnection(localDBURL, userDetails.split("##")[0], userDetails.split("##")[1])) {

            //String query = "SELECT SiteIP FROM globalDataCatalog WHERE tablename = ?";
            String query = "SELECT * FROM GlobalDataCatalog WHERE tablename = ?";
            try (PreparedStatement statement = localConn.prepareStatement(query)) {
                statement.setString(1, tableName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("siteIp - "+resultSet.getString("SiteIP"));
                        return resultSet.getString("SiteName");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

