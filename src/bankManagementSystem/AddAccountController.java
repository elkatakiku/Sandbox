package bankManagementSystem;

import datamodel.Bank;
import datamodel.NumberField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AddAccountController {
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<String> daysComboBox;
    @FXML
    private ComboBox<String> yearsComboBox;
    @FXML
    private ImageView displayImageView;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ToggleGroup accountTypeGroup;
    @FXML
    private TextField initialTransactionField;
    @FXML
    private ToggleGroup sexToggleGroup;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField contactNumberTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nationalityTextField;
    @FXML
    private TextField occupationTextField;

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;
    private String nationality;
    private String occupation;
    private String balance;
    private String sex;
    private String type;
    private LocalDate dateOfBirth;
    private File imagePath;
    private int nextID;

    private final int ADULT_AGE = 18;

    private Connection con;
    private PreparedStatement stmt;

    public void initialize() {
        File userIconFile = new File("resources/user.png");
        displayImageView.setImage(new Image(userIconFile.toURI().toString()));

        nextID = getNextAccountID();
        contactNumberTextField.textProperty().addListener(new NumberField(contactNumberTextField));
        initialTransactionField.textProperty().addListener(new NumberField(initialTransactionField));
        monthComboBox.setValue(LocalDate.now().getMonth().toString());
        daysComboBox.setValue(String.valueOf(LocalDate.now().getDayOfMonth()));
        yearsComboBox.setValue(String.valueOf(LocalDate.now().getYear()));
        populateDateComboBoxes();
    }

    private String monthCase(String str) {
        String[] characters = str.split("");
        for (int i = 0; i < characters.length; i++) if (i != 0) characters[i] = characters[i].toLowerCase();

        return String.join("", characters);
    }

    private void populateDateComboBoxes() {
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        monthComboBox.setItems(FXCollections.observableArrayList(months));


        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) days.add(String.valueOf(i));
        daysComboBox.setItems(FXCollections.observableArrayList(days));

        int currentYear = LocalDate.now().getYear();
        ArrayList<String> years = new ArrayList<>();

        for (int i = currentYear; i >= currentYear-100; i--) years.add(String.valueOf(i));

        yearsComboBox.setItems(FXCollections.observableArrayList(years));
    }

    public boolean processResult() {
        firstName = firstNameTextField.getText();
        lastName = lastNameTextField.getText();
        contactNumber = contactNumberTextField.getText();
        email = emailTextField.getText();
        nationality = nationalityTextField.getText();
        occupation = occupationTextField.getText();
        balance = initialTransactionField.getText();
        sex = sexToggleGroup.getSelectedToggle() != null ? ((RadioButton) sexToggleGroup.getSelectedToggle()).getText() : null;
        type = accountTypeGroup.getSelectedToggle() != null ? ((RadioButton) accountTypeGroup.getSelectedToggle()).getText() : null;

        String date = monthCase(monthComboBox.getValue()) + " " + daysComboBox.getValue() + ", " + yearsComboBox.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);
        dateOfBirth = LocalDate.parse(date, formatter);

        Period period = Period.between(dateOfBirth, LocalDate.now());
        if ((period.getYears() < ADULT_AGE) || isEmptyInputs() || !validEmail()) return false;

        return insertToDatabase(balance, type, firstName, lastName, sex, contactNumber,
                email, nationality, occupation, dateOfBirth, imagePath);
    }

    public boolean isEmptyInputs() {
        return firstName.isBlank() || lastName.isBlank() || contactNumber.isBlank() || email.isBlank() || nationality.isBlank()
                || occupation.isBlank() || balance.isBlank() || sex.isEmpty() || type.isEmpty() || dateOfBirth == null || imagePath == null;
    }

    private boolean validEmail() {
        return email.contains("@") & email.contains(".");
    }

    private boolean insertToDatabase(String balance, String type, String firstName,
                                  String lastName, String sex, String contactNumber,
                                  String email, String nationality, String occupation,
                                  LocalDate dateOfBirth, File imagePath) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String sql = "INSERT INTO customer (balance, accountType, " +
                    "firstName, lastName, nationality, " +
                    "gender, contactNumber, occupation," +
                    "email, dateOfBirth, image) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(balance));
            stmt.setString(2, type);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, nationality);
            stmt.setString(6, sex);
            stmt.setString(7, contactNumber);
            stmt.setString(8, occupation);
            stmt.setString(9, email);
            stmt.setString(10, dateOfBirth.toString());
            stmt.setBinaryStream(11, new FileInputStream(imagePath));

            if (stmt.executeUpdate() != -1) {
                if (insertInitialTransaction()) {
                    Bank.getInstance().loadAccountItems();
                    return true;
                }
            }
        } catch (ClassNotFoundException | SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    private int getNextAccountID() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String sql = "SELECT AUTO_INCREMENT AS nextID FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"bankmanagement\" AND TABLE_NAME = \"customer\"";

            stmt = con.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) return resultSet.getInt("nextID");
        } catch (ClassNotFoundException | SQLException e) { e.printStackTrace(); }
        return -1;
    }

    private boolean insertInitialTransaction() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/bankmanagement", "root", "");

            String insertTransaction = "INSERT INTO transaction (accountID, amount, transacType) VALUES (?, ?, ?)";

            stmt = con.prepareStatement(insertTransaction);
            stmt.setInt(1, nextID);
            stmt.setInt(2, Integer.parseInt(initialTransactionField.getText()));
            stmt.setString(3, "Deposit");

            if (stmt.executeUpdate() != -1) return true;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void uploadImageButtonHandeler() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //Show open file dialog
        this.imagePath = fileChooser.showOpenDialog(null);

        if (this.imagePath != null) displayImageView.setImage(new Image(this.imagePath.toURI().toString()));
    }
}
