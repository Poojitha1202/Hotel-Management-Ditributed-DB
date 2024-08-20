package com.dal.data;

import java.util.Scanner;

public class Sprint2 {

    private static final String DATABASE_NAME1="hotel_guest_management";
    private static final String DATABASE_NAME2="business_operations";
    private static final String DATABASE_IP1="104.154.95.157";
    private static final String DATABASE_IP2="34.123.28.213";

    private static final String USER_DETAILS1= "root##group13@";
    private static final String USER_DETAILS2= "root##group13@";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your database query: ");
        String userQuery = scanner.nextLine();

        String database1URL = "jdbc:mysql://"+DATABASE_IP1+":3306/"+DATABASE_NAME1;
        String database2URL= "jdbc:mysql://"+DATABASE_IP2+":3306/"+DATABASE_NAME2;

        QueryParser queryParser = new QueryParser();
        String tableName = queryParser.extractTableName(userQuery);
        System.out.println("tableName parsed is "+tableName);
        if (tableName != null) {
            SiteIpFetcher siteIpFetcher = new SiteIpFetcher();
            String siteName = siteIpFetcher.fetchSiteIP(database1URL, tableName, USER_DETAILS1);
System.out.println("siteName fetched is "+siteName);
            if (siteName != null) {
                String remoteDBURLWithIP = siteName.equals(DATABASE_NAME1)? database1URL:database2URL;
                String userDetails = siteName.equals(DATABASE_NAME1)?USER_DETAILS1:USER_DETAILS2;
                        DatabaseExecutor databaseExecutor = new DatabaseExecutor();
                databaseExecutor.executeQuery(remoteDBURLWithIP, userQuery,userDetails);
            } else {
                System.out.println("Table name not found in the globalDataCatalog.");
            }
        } else {
            System.out.println("Failed to extract table name from query.");
        }
    }
}
