package bankManagementSystem;

import datamodel.Bank;
import datamodel.Account;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class TransactionHistoryController {
    @FXML
    private TableView<Account.Transaction> transactionHistoryTableView;
    @FXML
    private TableColumn<Account.Transaction, String> transactionTypeColumn;
    @FXML
    private TableColumn<Account.Transaction, Integer> amountColumn;
    @FXML
    private TableColumn<Account.Transaction, LocalDate> transactionDateColumn;

    public void initialize() {
        transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("TRANSAC_TYPE"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transacDate"));
        transactionHistoryTableView.setItems(Bank.getSelectedAccount().getTransactions());
        transactionHistoryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
