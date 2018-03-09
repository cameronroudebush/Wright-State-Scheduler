package WSS;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WrightStateScheduler extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane main = new GridPane();
        main.setPadding(new Insets(5, 5, 5, 5));
        main.setVgap(10);
        main.setHgap(20);

        TextField userName = new TextField();
        userName.setPromptText("U00123456");

        TextField time = new TextField();
        time.setEditable(false);

        Clock clock = new Clock(time);

        ArrayList<TextField> crnBoxes = new ArrayList();
        PasswordField password = new PasswordField();
        Timer timer = new Timer();
        timer.schedule(clock, 0, 1000);
        TextField scheduleDate = new TextField();
        scheduleDate.setPromptText("MM/DD/YYYY");

        TextField scheduleTime = new TextField();
        Tooltip scheduleTimeTooltip = new Tooltip();
        scheduleTimeTooltip.setText("Please use Military time.");
        scheduleTime.setPromptText("HH:MM:SS");
        scheduleTime.setTooltip(scheduleTimeTooltip);

        Button schedule = new Button("Schedule! (Later)");
        Tooltip scheduleLaterTooltip = new Tooltip();
        scheduleLaterTooltip.setText("This will schedule your classes at a specified time.");
        schedule.setMinWidth(305);
        schedule.setTooltip(scheduleLaterTooltip);

        Button scheduleNow = new Button("Schedule! (Now)");
        Tooltip scheduleNowTooltip = new Tooltip();
        scheduleNowTooltip.setText("This will schedule your classes immediately.");
        scheduleNow.setMinWidth(305);
        scheduleNow.setTooltip(scheduleNowTooltip);

        ToggleGroup semesterButtons = new ToggleGroup();
        RadioButton spring = new RadioButton("Spring");
        RadioButton fall = new RadioButton("Fall");
        RadioButton summer = new RadioButton("Summer");

        summer.setToggleGroup(semesterButtons);
        spring.setToggleGroup(semesterButtons);
        fall.setToggleGroup(semesterButtons);

        main.add(new Label("Select Semester:"), 7, 3, 3, 1);
        main.add(new Label("Enter CRN's Below:"), 0, 0, 5, 1);
        main.add(new Label("Enter your UID:"), 0, 3, 2, 1);
        main.add(new Label("Enter your PIN:"), 0, 4, 2, 1);
        main.add(new Label("Enter your schedule date:"), 3, 3, 3, 1);
        main.add(new Label("Enter your schedule time:"), 3, 4, 4, 1);

        for (int i = 0; i < 10; i++) {
            crnBoxes.add(new TextField());
            crnBoxes.get(i).setPromptText("12345");
            TextFormatter<TextField> tf = new TextFormatter<>(e -> {
                if (e.getControlNewText().length() > 5) {
                    return null;
                }
                return e;
            });
            crnBoxes.get(i).setTextFormatter(tf);
            main.add(crnBoxes.get(i), i, 1);
        }
        main.add(userName, 1, 3, 2, 1);
        main.add(password, 1, 4, 2, 1);
        main.add(scheduleDate, 5, 3, 1, 1);
        main.add(scheduleTime, 5, 4, 1, 1);
        main.add(spring, 6, 4);
        main.add(summer, 7, 4);
        main.add(fall, 8, 4);
        main.add(scheduleNow, 1, 5, 6, 1);
        main.add(schedule, 5, 5, 6, 1);
        main.add(time, 7, 0, 3, 1);

        //verifies user input is 9 digits long
        TextFormatter<TextField> userFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 9) {
                return null;
            }
            return e;
        });
        userName.setTextFormatter(userFormatter);

        TextFormatter<TextField> dateFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 10) {
                return null;
            }
            return e;
        });
        scheduleDate.setTextFormatter(dateFormatter);

        TextFormatter<TextField> timeFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 8) {
                return null;
            }
            return e;
        });
        scheduleTime.setTextFormatter(timeFormatter);
        PrintStream log = new PrintStream(new File("log.txt"));
        schedule.setOnAction(e -> {
            int emptyCrnBoxes = 0;
            for (int i = 0; i < crnBoxes.size(); i++) {
                if (crnBoxes.get(i).getText().isEmpty()) {
                    emptyCrnBoxes++;
                }
            }
            if (userName.getText().isEmpty() || password.getText().isEmpty()) {
                Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
                regError.setHeaderText("Empty login");
                regError.showAndWait();
            } else if (scheduleDate.getText().length() < 4) {
                Alert badDate = new Alert(AlertType.ERROR, "You must include a date following the MM/DD/YYYY format.");
                badDate.setHeaderText("Date format error");
                badDate.showAndWait();
            } else if (semesterButtons.getSelectedToggle() == null) {
                Alert noToggle = new Alert(AlertType.ERROR, "You must select a semester.");
                noToggle.setHeaderText("Semester Selection");
                noToggle.showAndWait();
            } else if (emptyCrnBoxes == 10) {
                Alert noToggle = new Alert(AlertType.ERROR, "You have not inserted any CRN's.");
                noToggle.setHeaderText("No CRN's");
                noToggle.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Submitting CRNs");
                alert.setHeaderText(null);
                ButtonType buttonYes = new ButtonType("Yes");
                ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonYes, buttonNo);
                alert.setContentText("You are about to register for classes.\n"
                        + "Please note that this program does not check for invalid CRNs.\n"
                        + "If you wish to proceed, press yes.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonYes) {
                    Stack<String> crns = new Stack();
                    for (int i = 0; i < 10; i++) {
                        if (!crnBoxes.get(i).getText().isEmpty()) {
                            crns.push(crnBoxes.get(i).getText());
                        }
                    }
                    int semester;
                    if (semesterButtons.getSelectedToggle() == fall) {
                        semester = 80;
                    } else if (semesterButtons.getSelectedToggle() == summer) {
                        semester = 40;
                    } else if (semesterButtons.getSelectedToggle() == spring) {
                        semester = 30;
                    } else {
                        semester = 0;
                    }
                    String scheduleYear = scheduleDate.getText().substring(scheduleDate.getText().length() - 4, scheduleDate.getText().length());
                    WingsExpressConnector connector = new WingsExpressConnector(password.getText(), userName.getText(), scheduleYear + semester, crns, log);
                    if (connector.loginTest()) {
                        Alert regError = new Alert(Alert.AlertType.ERROR, "You seem to have miss typed your login info.");
                        regError.setHeaderText("Incorrect login");
                        regError.showAndWait();
                    } else {
                        String dateTime = clock.getCurrentDateAndTime().substring(14, clock.getCurrentDateAndTime().length());
                        ScheduleWaiter waiter = new ScheduleWaiter(clock, scheduleDate.getText(), scheduleTime.getText(), password.getText(), userName.getText(), semester, crns, log);
                        Thread waiterThread = new Thread(waiter);
                        waiterThread.start();
                    }
                }
            }
        });

        scheduleNow.setOnAction(e -> {
            int emptyCrnBoxes = 0;
            for (int i = 0; i < crnBoxes.size(); i++) {
                if (crnBoxes.get(i).getText().isEmpty()) {
                    emptyCrnBoxes++;
                }
            }
            if (userName.getText().isEmpty() || password.getText().isEmpty()) {
                Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
                regError.setHeaderText("Empty login");
                regError.showAndWait();
            } else if (scheduleDate.getText().length() < 4) {
                Alert badDate = new Alert(AlertType.ERROR, "You must include a date following the MM/DD/YYYY format.");
                badDate.setHeaderText("Date format error");
                badDate.showAndWait();
            } else if (semesterButtons.getSelectedToggle() == null) {
                Alert noToggle = new Alert(AlertType.ERROR, "You must select a semester.");
                noToggle.setHeaderText("Semester Selection");
                noToggle.showAndWait();
            } else if (emptyCrnBoxes == 10) {
                Alert noToggle = new Alert(AlertType.ERROR, "You have not inserted any CRN's.");
                noToggle.setHeaderText("No CRN's");
                noToggle.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Submitting CRNs");
                alert.setHeaderText(null);
                ButtonType buttonYes = new ButtonType("Yes");
                ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonYes, buttonNo);
                alert.setContentText("You are about to register for classes.\n"
                        + "Please note that this program does not check for invalid CRNs.\n"
                        + "If you wish to proceed, press yes.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonYes) {
                    Stack<String> crns = new Stack();
                    for (int i = 0; i < 10; i++) {
                        if (!crnBoxes.get(i).getText().isEmpty()) {
                            crns.push(crnBoxes.get(i).getText());
                        }
                    }
                    int semester;
                    if (semesterButtons.getSelectedToggle() == fall) {
                        semester = 80;
                    } else if (semesterButtons.getSelectedToggle() == summer) {
                        semester = 40;
                    } else if (semesterButtons.getSelectedToggle() == spring) {
                        semester = 30;
                    } else {
                        semester = 0;
                    }
                    String scheduleYear = scheduleDate.getText().substring(scheduleDate.getText().length() - 4, scheduleDate.getText().length());
                    WingsExpressConnector connector = new WingsExpressConnector(password.getText(), userName.getText(), scheduleYear + semester, crns, log);
                    if (connector.loginTest()) {
                        Alert regError = new Alert(Alert.AlertType.ERROR, "You seem to have miss typed your login info.");
                        regError.setHeaderText("Incorrect login");
                        regError.showAndWait();
                    } else {
                        WingsExpressConnector connectorReal = new WingsExpressConnector(password.getText(), userName.getText(), scheduleYear + semester, crns, log);
                        Thread runner = new Thread(connectorReal);
                        runner.start();
                        try {
                            runner.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(WrightStateScheduler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        String content = connectorReal.getContent();
                        if (content.contains("Registration Add Errors")) {
                            Alert regError = new Alert(Alert.AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn.");
                            regError.setHeaderText("Registration Add Error");
                            regError.showAndWait();
                        }
                        if (content.contains("Corequisite")) {
                            Alert coReqError = new Alert(Alert.AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                            coReqError.setHeaderText("Corequisite Error");
                            coReqError.showAndWait();;
                        }
                    }
                }
            }
        });
        Scene scene = new Scene(main, 1000, 200);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Wright State Schedular");
        stage.show();
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }
}
