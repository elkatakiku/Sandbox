package bankManagementSystem;

import datamodel.Bank;
import datamodel.Account;
import datamodel.NumberField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TransferDialogController {
    @FXML
    private TextField toAccountTextField;
    @FXML
    private TextField transferAmountTextField;
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label balanceLabel;

    public void initialize() {
        accountNameLabel.setText(Bank.getSelectedAccount().getName());
        balanceLabel.setText(Bank.getSelectedAccount().getBalance() + "");
        toAccountTextField.textProperty().addListener(new NumberField(toAccountTextField));
        transferAmountTextField.textProperty().addListener(new NumberField(transferAmountTextField));
        toAccountTextField.requestFocus();
    }

    public boolean transferCash() {
        if (transferAmountTextField.getText().isBlank() || toAccountTextField.getText().isBlank()) return false;

        String receiverID = toAccountTextField.getText();
        int amount = Integer.parseInt(transferAmountTextField.getText());

        Bank bank = Bank.getInstance();
        Account selectedAccount = Bank.getSelectedAccount();

        if (bank.findAccount(receiverID) != null) {
            if (bank.newTransaction(selectedAccount.getAcctNumber(), amount, Bank.TRANSFER_CASH, Bank.SEND_CASH)) {
                if (bank.transferCash(selectedAccount.getAcctNumber(), receiverID, amount)){
                    boolean flag = bank.newTransaction(receiverID, amount, Bank.TRANSFER_CASH, Bank.RECIEVE_CASH);
                    bank.updateSelectedAccount(selectedAccount);
                    return flag;
                }
            }
        }

        return false;
    }
}
