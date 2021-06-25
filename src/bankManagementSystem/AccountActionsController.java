package bankManagementSystem;

import datamodel.Admin;
import datamodel.Bank;
import datamodel.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AccountActionsController implements Initializable {
    @FXML
    private Label adminNameLabel;
    @FXML
    private Label birthdateLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane actionsWindow;
    @FXML
    private Label accountTypeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label accountIDLabel;
    @FXML
    private Button backButton;
    @FXML
    private  ImageView displayImageView;
    @FXML
    private  ImageView depositImageView;
    @FXML
    private  ImageView withdrawImageView;
    @FXML
    private  ImageView transferCashImageView;
    @FXML
    private  ImageView transactionHistoryImageView;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label accountNumberLabel;
    @FXML
    private Label nationalityLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private Label occupationLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL dd, yyyy");
        Account selectedAccount = Bank.getSelectedAccount();

        File logoFile = new File("resources/cropped_logo.png");
        File depositImageFile = new File("resources/deposit.png");
        File withdrawImageFile = new File("resources/with.png");
        File transferImageFile = new File("resources/trasfer.png");
        File transacImageFile = new File("resources/transaction-history.png");

        Image logoImage = new Image(logoFile.toURI().toString());
        Image depositImage = new Image(depositImageFile.toURI().toString());
        Image withdrawImage = new Image(withdrawImageFile.toURI().toString());
        Image transferImage = new Image(transferImageFile.toURI().toString());
        Image transacImage = new Image(transacImageFile.toURI().toString());

        logoImageView.setImage(logoImage);
        depositImageView.setImage(depositImage);
        withdrawImageView.setImage(withdrawImage);
        transferCashImageView.setImage(transferImage);
        transactionHistoryImageView.setImage(transacImage);
        displayImageView.setImage(selectedAccount.getImage());

        adminNameLabel.setText(Admin.loggedInAdmin().getName());
        customerNameLabel.setText(selectedAccount.getName());
        accountNumberLabel.setText(": " + selectedAccount.getAcctNumber());
        nationalityLabel.setText(": " + selectedAccount.getNationality());
        birthdateLabel.setText(": " + selectedAccount.getBirthDate().format(formatter));
        ageLabel.setText(": " + selectedAccount.getAge());
        genderLabel.setText(": " + selectedAccount.getGender());
        contactLabel.setText(": " + selectedAccount.getContactNumber());
        occupationLabel.setText(": " + selectedAccount.getOccupation());
        emailLabel.setText(": " + selectedAccount.getEmail());

        accountIDLabel.setText(": " + selectedAccount.getAcctNumber());
        balanceLabel.setText(": " + selectedAccount.getBalance());
        accountTypeLabel.setText(": " + selectedAccount.getAcctType());
    }

    @FXML
    private void backButtonHandler() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("index.fxml")));
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }

    @FXML
    private void depositButtonHandler() {
        showTransactionDialog(Bank.DEPOSIT);
    }

    @FXML
    private void withdrawButtonHandler() {
        showTransactionDialog(Bank.WITHDRAW);
    }

    @FXML
    private void transferCashButtonHandler() {
        showTransactionDialog(Bank.TRANSFER_CASH);
    }

    @FXML
    private void logoutButtonHandler() throws IOException {
        Admin.loggedInAdmin().logout(logoutButton, getClass());
    }

    @FXML
    private void transactionHistoryButtonHandler() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(actionsWindow.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("transactionHistory.fxml"));

        dialog.getDialogPane().setContent(fxmlLoader.load());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void showTransactionDialog(String transacType) {
        String dialogFxml = null;

        switch (transacType) {
            case Bank.DEPOSIT:
                dialogFxml = "depositDialog.fxml";
                break;
            case Bank.WITHDRAW:
                dialogFxml = "withdrawDialog.fxml";
                break;
            case Bank.TRANSFER_CASH:
                dialogFxml = "transferDialog.fxml";
                break;
        }

        if (dialogFxml == null) return;

        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(actionsWindow.getScene().getWindow());

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(dialogFxml));

            dialog.getDialogPane().setContent(fxmlLoader.load());

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            boolean errorFlag = false;

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Error occured. Transaction failed.");
                error.setContentText("Please fill all the necessary fields.");
                switch (transacType) {
                    case Bank.DEPOSIT:
                        DepositDialogController depositDialogController = fxmlLoader.getController();
                        errorFlag = depositDialogController.deposit();
                        break;
                    case Bank.WITHDRAW:
                        WithdrawDialogController withdrawDialogController = fxmlLoader.getController();
                        errorFlag = withdrawDialogController.withdraw();
                        break;
                    case Bank.TRANSFER_CASH:
                        TransferDialogController transferDialogController = fxmlLoader.getController();
                        errorFlag = transferDialogController.transferCash();
                        break;
                }

                balanceLabel.setText(": " + Bank.getSelectedAccount().getBalance());
                if (!errorFlag) error.show();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
