package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Account {
    private String acctNumber;
    private String name;
    private String acctType;
    private String nationality;
    private String gender;
    private String contactNumber;
    private String occupation;
    private String email;
    private LocalDate birthDate;
    private int balance;
    private int age;
    private Image image;

    private final ObservableList<Transaction> transactions;
    private DateTimeFormatter formatter;

    public static class Transaction {
        private final int amount;
        private final String TRANSAC_TYPE;
        private final LocalDate transacDate;

        public Transaction(int amount, String TRANSAC_TYPE) {
            this(amount, TRANSAC_TYPE, LocalDate.now());
        }

        public Transaction(int amount, String TRANSAC_TYPE, LocalDate transacDate) {
            this.amount = amount;
            this.TRANSAC_TYPE = TRANSAC_TYPE;
            this.transacDate = transacDate;
        }

        public int getAmount() {
            return amount;
        }

        public String getTRANSAC_TYPE() {
            return TRANSAC_TYPE;
        }

        public LocalDate getTransacDate() {
            return transacDate;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "amount=" + amount +
                    ", TRANSAC_TYPE='" + TRANSAC_TYPE + '\'' +
                    ", transacDate=" + transacDate +
                    '}';
        }
    }

    public Account(String acctNumber, String name, String acctType, int initialAmount,
                   String nationality, String gender, String contactNumber, String occupation,
                   LocalDate birthDate, String email, Image image) {
        this.acctNumber = acctNumber;
        this.name = name;
        this.acctType = acctType;
        this.balance = initialAmount;
        this.nationality = nationality;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.occupation = occupation;
        this.birthDate = birthDate;
        this.email = email;
        this.image = image;

        LocalDate currentDate = LocalDate.now();
        Period p = Period.between(birthDate, currentDate);

        this.age = p.getYears();
        this.transactions = FXCollections.observableArrayList();

        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    }

    public void addNewTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void loadTransactions() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

        PreparedStatement stmt = con.prepareStatement("SELECT * FROM transaction WHERE accountID = ?");
        stmt.setString(1, this.acctNumber);
        ResultSet resultSet = stmt.executeQuery();

        while (resultSet.next()) {
            try {
                Transaction newTransaction = new Account.Transaction(
                        resultSet.getInt("amount"),
                        resultSet.getString("transacType"),
                        resultSet.getDate("transacDate").toLocalDate()
                );

                addNewTransaction(newTransaction);
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }
    }

    public void printTransactions() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }


//  SETTERS AND GETTERS

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public int getBalance() {
        return balance;
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "acctNumber='" + acctNumber + '\'' +
                ", name='" + name + '\'' +
                ", acctType='" + acctType + '\'' +
                ", nationality='" + nationality + '\'' +
                ", gender='" + gender + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", occupation='" + occupation + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                ", balance=" + balance +
                ", age=" + age +
                ", transactions=" + transactions +
                ", formatter=" + formatter +
                '}';
    }
}
