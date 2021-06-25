package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import java.sql.*;
import java.time.LocalDate;

public class Bank {
    private static final Bank bank = new Bank();
    private static Account account;

    private ObservableList<Account> accounts;

    public static final String WITHDRAW = "Withdraw";
    public static final String DEPOSIT = "Deposit";
    public static final String TRANSFER_CASH = "Transfer Cash";
    public static final String RECIEVE_CASH = "Recieve";
    public static final String SEND_CASH = "Send";

    private Connection con;
    private PreparedStatement stmt;

    private Bank() {}

    public static Bank getInstance() {
        return bank;
    }

    public void loadAccountItems() {
        accounts = FXCollections.observableArrayList();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            PreparedStatement stmt = con.prepareStatement("SELECT * FROM customer");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                try {
                    Account newAccount = new Account(
                            resultSet.getString("acctNumber"),
                            resultSet.getString("firstName") + " " + resultSet.getString("lastName"),
                            resultSet.getString("accountType"),
                            resultSet.getInt("balance"),
                            resultSet.getString("nationality"),
                            resultSet.getString("gender"),
                            resultSet.getString("contactNumber"),
                            resultSet.getString("occupation"),
                            LocalDate.parse(resultSet.getString("dateOfBirth")),
                            resultSet.getString("email"),
                            new Image(resultSet.getBinaryStream("image"))
                    );

                    newAccount.loadTransactions();
                    accounts.add(newAccount);
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getCause();
                }
            }
        } catch (ClassNotFoundException | SQLException e) { e.printStackTrace(); }
    }

    public boolean updateSelectedAccount(Account account) {
        Account foundAcccount = findAccount(account.getAcctNumber());
        if (foundAcccount != null) {
            Bank.account = foundAcccount;
            return true;
        }

        return false;
    }

    public boolean newTransaction(String accountID, int amount, String transacType) {
        return newTransaction(accountID, amount, transacType, null);
    }

    public boolean newTransaction(String accountID, int amount, String transacType, String transferType) {
        if (amount < 0 && (transacType.isEmpty() || transacType.isBlank())) return false;
        else if (transacType.equals(WITHDRAW) && amount>account.getBalance()) return false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String insertTransaction = "INSERT INTO transaction (accountID, amount, transacType, transferType) VALUES (?, ?, ?, ?)";
            String updateBalance = "UPDATE customer SET balance = ? WHERE acctNumber = ?";

            stmt = con.prepareStatement(insertTransaction);
            stmt.setInt(1, Integer.parseInt(accountID));
            stmt.setInt(2, amount);
            stmt.setString(3, transacType);
            stmt.setString(4, transferType);

            PreparedStatement updateStmt = con.prepareStatement(updateBalance);

            Account updateAccount = findAccount(accountID);

            if (transacType.equals(DEPOSIT) || RECIEVE_CASH.equals(transferType)) updateStmt.setInt(1, updateAccount.getBalance() + amount);
            else if (transacType.equals(WITHDRAW) || SEND_CASH.equals(transferType)) updateStmt.setInt(1, updateAccount.getBalance() - amount);
            else {
                stmt.close();
                return false;
            }

            updateStmt.setInt(2, Integer.parseInt(accountID));

            if (stmt.executeUpdate() != 0 & updateStmt.executeUpdate() != 0) {
                Bank.getInstance().loadAccountItems();
                if (Bank.getInstance().updateSelectedAccount(updateAccount)) {
                    stmt.close();
                    return true;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean transferCash(String sender, String receiver, int amount) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String insertTransfer = "INSERT INTO transfercash (senderAcct, recieverAcct, amount) VALUES (?, ?, ?)";

            stmt = con.prepareStatement(insertTransfer, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, Integer.parseInt(sender));
            stmt.setInt(2, Integer.parseInt(receiver));
            stmt.setInt(3, amount);

            if (stmt.executeUpdate() != 0) {
                stmt.close();
                return true;
            } else return false;
        } catch (ClassNotFoundException | SQLException e) { e.printStackTrace(); }

        return false;
    }

    public Account findAccount(String accountID) {
        int start = 0;
        int end = accounts.size() - 1;

        int searchID = Integer.parseInt(accountID);

        while (true) {
            int mid = end + (start - end) / 2;
            if (end < start) return null;

            int midID = Integer.parseInt(accounts.get(mid).getAcctNumber());

            if (midID == searchID) return accounts.get(mid);
            else if (midID > searchID) end = mid - 1;
            else start = mid + 1;
        }
    }

    public boolean deleteAccount(Account selectedAccount) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String sql = "DELETE FROM customer WHERE acctNumber = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, selectedAccount.getAcctNumber());

            if (stmt.executeUpdate() != -1) return true;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Account getSelectedAccount() {
        return account;
    }

    public static void selectedAccount(Account account) {
        Bank.account = account;
    }

    public ObservableList<Account> getCustomers() {
        return accounts;
    }

    public void setCustomers(ObservableList<Account> accounts) {
        this.accounts = accounts;
    }
}
