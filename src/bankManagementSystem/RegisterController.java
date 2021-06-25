package bankManagementSystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private  ImageView shieldImageView;
    @FXML
    private  TextField firstNameTextFrield;
    @FXML
    private  TextField lastNameTextFrield;
    @FXML
    private  TextField userNameTextFrield;
    @FXML
    private  PasswordField setPasswordField;
    @FXML
    private  PasswordField confirmPasswordField;

    public void initialize() {
        File shieldFile = new File("resources/shield.png");
        Image shieldImage = new Image(shieldFile.toURI().toString());
        shieldImageView.setImage(shieldImage);
    }

    private boolean isEmpty(String firstName, String lastName, String username, String password, String confirmPass) {
        return firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank() || confirmPass.isBlank();
    }

    public boolean createAccount() {
        String firstName = firstNameTextFrield.getText();
        String lastName = lastNameTextFrield.getText();
        String username = userNameTextFrield.getText();
        String password = setPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (isEmpty(firstName, lastName, username, password, confirmPass)) {
            registrationMessageLabel.setText("Please fill all the fields.");
            registrationMessageLabel.setTextFill(Color.web("#ff7575"));
            return false;
        } else registrationMessageLabel.setText("");

        return insertToDatabase(firstName, lastName, username, password, confirmPass);
    }

    private boolean insertToDatabase(String firstName, String lastName, String username, String password, String confirmPass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            if (!password.equals(confirmPass)) {
                confirmPasswordLabel.setText("Password does not matched!");
                return false;
            }

            confirmPasswordLabel.setText("");

            String sql = "INSERT INTO users (firstName, lastName, userName, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, username);
            stmt.setString(4, password);

            int operation  = stmt.executeUpdate();

            if (operation != -1) {
                registrationMessageLabel.setText("User has registered successfully!");
                registrationMessageLabel.setTextFill(Color.web("#227bff"));
                firstNameTextFrield.clear();
                lastNameTextFrield.clear();
                userNameTextFrield.clear();
                setPasswordField.clear();
                confirmPasswordField.clear();
                firstNameTextFrield.requestFocus();
                return true;
            } else registrationMessageLabel.setText("");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
