package bankManagementSystem;

import datamodel.Admin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class loginController {
    @FXML
    private Label errorLabel;
    @FXML
    private GridPane activeWindow;
    @FXML
    private ImageView logoImageView;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginBtn;

    public void initialize() {
        File logoFile = new File("resources/199828920_1343702626026134_8830085139917556061_n.png");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoImageView.setImage(logoImage);
    }

    public void login(ActionEvent event) {
        String uname = username.getText();
        String pass = password.getText();

        if (uname.equals("") && pass.equals("")) return;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE userName = ? and password = ?");
            stmt.setString(1, uname);
            stmt.setString(2, pass);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                try {
                    errorLabel.setVisible(false);
                    Admin.loggedInAdmin().setName(resultSet.getString("firstName") + " " + resultSet.getString("lastName"));

                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    stage.close();

                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
                    Stage registerStage = new Stage();
                    registerStage.setTitle("Sandbox");
                    registerStage.setScene(new Scene(root, 900, 600));
                    registerStage.setResizable(false);
                    registerStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getCause();
                }
            } else {
                errorLabel.setVisible(true);
            }

            username.clear();
            password.clear();
            username.requestFocus();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Database connection is not established.");
            error.setContentText("Check the server if its up and running and restart the app.");
            error.show();
        }
    }

    public void createAccountForm() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(activeWindow.getScene().getWindow());

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("register.fxml"));

            dialog.getDialogPane().setContent(fxmlLoader.load());

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                RegisterController registerController = fxmlLoader.getController();
                if (!registerController.createAccount()) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Error occured. Create admin account failed.");
                    error.setContentText("Please fill all the necessary fields and mind the values inputted.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
