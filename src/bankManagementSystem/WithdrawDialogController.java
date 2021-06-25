package bankManagementSystem;

import datamodel.Bank;
import datamodel.NumberField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class WithdrawDialogController {
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private TextField amountTextField;

    public void initialize() {
        accountNameLabel.setText(Bank.getSelectedAccount().getName());
        balanceLabel.setText(Bank.getSelectedAccount().getBalance() + "");
        amountTextField.textProperty().addListener(new NumberField(amountTextField));
        amountTextField.requestFocus();
    }

    public boolean withdraw() {
        if (!amountTextField.getText().isBlank())
            return Bank.getInstance().newTransaction(Bank.getSelectedAccount().getAcctNumber(), Integer.parseInt(amountTextField.getText()), Bank.WITHDRAW);
        return false;
    }
}
