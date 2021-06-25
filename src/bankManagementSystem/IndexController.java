package bankManagementSystem;

import datamodel.Admin;
import datamodel.Bank;
import datamodel.Account;
import datamodel.NumberField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class IndexController {
    @FXML
    private Label adminNameLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private AnchorPane mainWindow;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button selectCustomerAccountButton;
    @FXML
    private TableView<Account> accountDisplayTableView;
    @FXML
    private TableColumn<Account, String> bankAccountIDColumn;
    @FXML
    private TableColumn<Account, Integer> availableBalanceColumn;
    @FXML
    private TableColumn<Account, String> accountTypeColumn;

    @FXML
    private void initialize() {
        File logoFile = new File("resources/cropped_logo.png");
        Image logoImage = new Image(logoFile.toURI().toString());
        logoImageView.setImage(logoImage);

        adminNameLabel.setText(Admin.loggedInAdmin().getName());

        accountDisplayTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        bankAccountIDColumn.setCellValueFactory(new PropertyValueFactory<>("acctNumber"));
        availableBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("acctType"));
        accountDisplayTableView.setItems(Bank.getInstance().getCustomers());

        searchTextField.textProperty().addListener(new NumberField(searchTextField));
    }

    @FXML
    private void selectCustomerAccountButtonHandler() throws IOException {
        Account selectedAccount = accountDisplayTableView.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Help");
            info.setHeaderText("No account selected");
            info.setContentText("To select an account, Select a row of user then press Select.");
            info.show();
            return;
        }

        Bank.selectedAccount(selectedAccount);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("accountActions.fxml")));
        Stage stage = (Stage) selectCustomerAccountButton.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    @FXML
    private void addAccountButtonHandler() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addAccount.fxml"));
        dialog.getDialogPane().setContent(fxmlLoader.load());

        dialog.setTitle("Add New Account");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Alert error = new Alert(Alert.AlertType.ERROR);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            AddAccountController controller = fxmlLoader.getController();
            if (!controller.processResult()) {
                error.setTitle("Error");
                error.setHeaderText("Creating account failed");
                error.setContentText("Please fill all the fields and input valid data to create a new account.");
                error.show();
            }
            accountDisplayTableView.setItems(Bank.getInstance().getCustomers());
        }
    }

    @FXML
    private void deleteButtonHandler() {
        Account selectedAccount = accountDisplayTableView.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Help");
            info.setHeaderText("Delete");
            info.setContentText("To delete an account, select an account in the table then press Delete.");
            info.show();
            return;
        }

        if (Bank.getInstance().deleteAccount(selectedAccount)) {
            Bank.getInstance().loadAccountItems();
            accountDisplayTableView.setItems(Bank.getInstance().getCustomers());
        }
    }

    @FXML
    private void searchButtonHandler() {
        String toBeSeachedID = searchTextField.getText();

        if (toBeSeachedID.isBlank()) return;

        Account searchedAccount = Bank.getInstance().findAccount(toBeSeachedID);
        if (searchedAccount != null) accountDisplayTableView.getSelectionModel().select(searchedAccount);
        else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Account not found");
            error.setContentText("No account matched the account ID.");
            error.show();
        }
        searchTextField.clear();
    }

    @FXML
    private void logoutButtonHandler() throws IOException {
        Admin.loggedInAdmin().logout(logoutButton, getClass());
    }

}
