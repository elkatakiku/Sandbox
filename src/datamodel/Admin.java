package datamodel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Admin {
    private static Admin admin = new Admin();
    private String name;

    public static Admin loggedInAdmin() {
        return admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void logout(Button logoutButton, Class currentClass) throws IOException {
        ((Stage) logoutButton.getScene().getWindow()).close();

        Stage loginStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(currentClass.getResource("login.fxml")));
        loginStage.setTitle("Sandbox");
        loginStage.setScene(new Scene(root, 600, 350));
        loginStage.setResizable(false);
        loginStage.show();
    }
}
