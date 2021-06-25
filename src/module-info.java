module BankManagementSystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.java;
    requires java.sql;

    opens bankManagementSystem;
    opens datamodel;
}