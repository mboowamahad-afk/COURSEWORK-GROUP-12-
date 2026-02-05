
package studentregistrationapp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class StudentRegistrationApp extends Application {

    private static int counter = 1;

    @Override
    public void start(Stage stage) {

        // ===== TEXT FIELDS =====
        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField confirmEmail = new TextField();

        PasswordField password = new PasswordField();
        PasswordField confirmPassword = new PasswordField();

        // ===== DOB COMBO BOXES =====
        ComboBox<Integer> yearBox = new ComboBox<>();
        ComboBox<String> monthBox = new ComboBox<>();
        ComboBox<Integer> dayBox = new ComboBox<>();

        for (int y = 1960; y <= LocalDate.now().getYear(); y++) {
            yearBox.getItems().add(y);
        }

        monthBox.getItems().addAll(
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        );

        // ===== GENDER =====
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);

        // ===== DEPARTMENT =====
        ToggleGroup deptGroup = new ToggleGroup();
        RadioButton civil = new RadioButton("Civil");
        RadioButton cse = new RadioButton("CSE");
        RadioButton electrical = new RadioButton("Electrical");
        RadioButton ec = new RadioButton("E&C");
        RadioButton mech = new RadioButton("Mechanical");

        civil.setToggleGroup(deptGroup);
        cse.setToggleGroup(deptGroup);
        electrical.setToggleGroup(deptGroup);
        ec.setToggleGroup(deptGroup);
        mech.setToggleGroup(deptGroup);

        // ===== TEXT AREA =====
        TextArea output = new TextArea();
        output.setEditable(false);

        // ===== BUTTONS =====
        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");

        // ===== SUBMIT ACTION =====
        submit.setOnAction(e -> {

            String fn = firstName.getText().trim();
            String ln = lastName.getText().trim();
            String em = email.getText().trim();
            String cem = confirmEmail.getText().trim();
            String pw = password.getText();
            String cpw = confirmPassword.getText();

            StringBuilder errors = new StringBuilder();

            if (fn.isEmpty() || ln.isEmpty())
                errors.append("Name required\n");

            if (!em.matches(".+@.+\\..+") || !em.equals(cem))
                errors.append("Invalid or mismatched email\n");

            if (!pw.matches("(?=.*[A-Za-z])(?=.*\\d).{8,20}") || !pw.equals(cpw))
                errors.append("Invalid or mismatched password\n");

            if (yearBox.getValue() == null || monthBox.getValue() == null)
                errors.append("DOB required\n");

            if (genderGroup.getSelectedToggle() == null)
                errors.append("Select gender\n");

            if (deptGroup.getSelectedToggle() == null)
                errors.append("Select department\n");

            if (errors.length() > 0) {
                new Alert(Alert.AlertType.ERROR, errors.toString()).show();
                return;
            }

            int year = yearBox.getValue();
            int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
            LocalDate dob = LocalDate.of(year, month, 1);

            int age = Period.between(dob, LocalDate.now()).getYears();
            if (age < 16 || age > 60) {
                new Alert(Alert.AlertType.ERROR, "Age must be 16–60").show();
                return;
            }

            String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
            String dept = ((RadioButton) deptGroup.getSelectedToggle()).getText();

            String id = LocalDate.now().getYear() + "-" + String.format("%05d", counter++);
            String record = id + " | " + fn + " " + ln + " | " + gender + " | " + dept +
                    " | " + LocalDate.now() + " | " + em;

            output.appendText(record + "\n");

            try (FileWriter fw = new FileWriter("students.csv", true)) {
                fw.write(record + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        cancel.setOnAction(e -> stage.close());

        // ===== LAYOUT =====
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        grid.add(new Label("First Name"), 0, 0); grid.add(firstName, 1, 0);
        grid.add(new Label("Last Name"), 0, 1); grid.add(lastName, 1, 1);
        grid.add(new Label("Email"), 0, 2); grid.add(email, 1, 2);
        grid.add(new Label("Confirm Email"), 0, 3); grid.add(confirmEmail, 1, 3);
        grid.add(new Label("Password"), 0, 4); grid.add(password, 1, 4);
        grid.add(new Label("Confirm Password"), 0, 5); grid.add(confirmPassword, 1, 5);

        grid.add(new Label("DOB"), 0, 6);
        grid.add(yearBox, 1, 6);
        grid.add(monthBox, 2, 6);

        VBox genderBox = new VBox(5, male, female);
        VBox deptBox = new VBox(5, civil, cse, electrical, ec, mech);

        grid.add(new Label("Gender"), 0, 7); grid.add(genderBox, 1, 7);
        grid.add(new Label("Department"), 0, 8); grid.add(deptBox, 1, 8);

        HBox buttons = new HBox(10, submit, cancel);
        grid.add(buttons, 1, 9);

        BorderPane root = new BorderPane();
        root.setLeft(grid);
        root.setCenter(output);

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("New Student Registration Form");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
