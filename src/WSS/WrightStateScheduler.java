package WSS;

import java.io.File;
import java.io.PrintStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WrightStateScheduler extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        GridPane timePane = new GridPane();
        GridPane semesterPane = new GridPane();
        GridPane main = new GridPane();
        main.setPadding(new Insets(5, 5, 5, 5));
        semesterPane.setHgap(10);
        timePane.setHgap(10);
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
        DatePicker scheduleDate = new DatePicker();
        scheduleDate.setEditable(false);
        scheduleDate.setValue(LocalDate.now());
        scheduleDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        ObservableList<String> hourOptions
                = FXCollections.observableArrayList(
                        "01", "02", "03", "04", "05", "06", "07",
                        "08", "09", "10", "11", "12"
                );
        ComboBox scheduleTimeHour = new ComboBox(hourOptions);
        ObservableList<String> options
                = FXCollections.observableArrayList(
                        "00", "01", "02", "03", "04", "05", "06", "07",
                        "08", "09", "10", "11", "12", "13",
                        "14", "15", "16", "17", "18", "19", "20",
                        "21", "22", "23", "24", "25", "26", "27",
                        "28", "29", "30", "31", "32", "33", "34",
                        "35", "36", "37", "38", "39", "40", "41",
                        "42", "43", "44", "45", "46", "47", "48",
                        "49", "50", "51", "51", "52", "53", "54",
                        "55", "56", "57", "58", "59"
                );
        ComboBox scheduleTimeMinute = new ComboBox(options);
        ComboBox scheduleTimeSeconds = new ComboBox(options);
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

        ToggleGroup meridiem = new ToggleGroup();
        RadioButton am = new RadioButton("AM");
        RadioButton pm = new RadioButton("PM");

        am.setToggleGroup(meridiem);
        pm.setToggleGroup(meridiem);
        meridiem.selectToggle(am);

        semesterPane.add(new Label("Select Semester:"), 1, 1);
        main.add(new Label("Enter CRN's Below:"), 0, 0, 5, 1);
        main.add(new Label("Enter your UID:"), 0, 3, 2, 1);
        main.add(new Label("Enter your PIN:"), 0, 4, 2, 1);
        main.add(new Label("Schedule date:"), 3, 3, 2, 1);
        main.add(new Label("Schedule time:"), 3, 4, 2, 1);

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
        main.add(scheduleDate, 4, 3, 2, 1);
        timePane.add(scheduleTimeHour, 1, 1, 1, 1);
        timePane.add(scheduleTimeMinute, 2, 1, 1, 1);
        timePane.add(scheduleTimeSeconds, 3, 1, 1, 1);
        timePane.add(am, 4, 1);
        timePane.add(pm, 5, 1);
        semesterPane.add(spring, 2, 1);
        semesterPane.add(summer, 3, 1);
        semesterPane.add(fall, 4, 1);
        main.add(semesterPane, 6, 3, 4, 1);
        main.add(timePane, 4, 4, 5, 1);
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

        PrintStream log = new PrintStream(new File("log.txt"));
        schedule.setOnAction(e -> {
            int emptyCrnBoxes = 0;
            String selectedMeridiem;
            String semesterString = "";
            int semester;
            if (semesterButtons.getSelectedToggle() == fall) {
                semester = 80;
                semesterString = "fall";
            } else if (semesterButtons.getSelectedToggle() == summer) {
                semester = 40;
                semesterString = "Summer";
            } else if (semesterButtons.getSelectedToggle() == spring) {
                semester = 30;
                semesterString = "Spring";
            } else {
                semester = 0;
            }
            if (meridiem.getSelectedToggle() == am) {
                selectedMeridiem = "AM";
            } else {
                selectedMeridiem = "PM";
            }

            if (scheduleTimeHour.getSelectionModel().getSelectedItem() == null || scheduleTimeMinute.getSelectionModel().getSelectedItem() == null || scheduleTimeSeconds.getSelectionModel().getSelectedItem() == null) {
                Alert tooLate = new Alert(AlertType.ERROR, "Please make sure to fill in all of the time drop down boxes!");
                tooLate.setHeaderText("Time error");
                tooLate.showAndWait();
            } else {
                String timeToSchedule = scheduleTimeHour.getSelectionModel().getSelectedItem().toString() + ":" + scheduleTimeMinute.getSelectionModel().getSelectedItem().toString() + ":"
                        + scheduleTimeSeconds.getSelectionModel().getSelectedItem().toString() + " " + selectedMeridiem;
                for (int i = 0; i < crnBoxes.size(); i++) {
                    if (crnBoxes.get(i).getText().isEmpty()) {
                        emptyCrnBoxes++;
                    }
                }
                DateFormat formater = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                Time currentTimeFormat = null;
                Time timeToScheduleFormat = null;
                try {
                    timeToScheduleFormat = new Time((formater.parse(scheduleDate.getValue().format(dateFormatter) + " " + timeToSchedule).getTime()));
                    currentTimeFormat = new Time((formater.parse(clock.getCurrentDateAndTime().substring(14, clock.getCurrentDateAndTime().length())).getTime()));
                } catch (ParseException ex) {
                    log.println("Parse error for checking dates");
                    log.print(ex.toString());
                    log.println();
                }

                if (userName.getText().isEmpty() || password.getText().isEmpty()) {
                    Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
                    regError.setHeaderText("Empty login");
                    regError.showAndWait();
                } else if (scheduleDate.getValue().format(dateFormatter).length() < 4) {
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
                } else if (timeToScheduleFormat.before(currentTimeFormat)) {
                    Alert tooLate = new Alert(AlertType.ERROR, "The time you have selected has already passed. Please select a new time.");
                    tooLate.setHeaderText("The time has passed");
                    tooLate.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Submitting CRNs");
                    alert.setHeaderText(null);
                    ButtonType buttonYes = new ButtonType("Yes");
                    ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonYes, buttonNo);
                    alert.setContentText("All preliminary tests have passed but please note this program does not test for valid CRN numbers. "
                            + "Currently you will be registering for the following date/semester and time: \n"
                            + scheduleDate.getValue().format(dateFormatter) + " " + semesterString + " " + timeToSchedule
                            + "\nIf you would like to continue with this info press yes. If something looks wrong press no and please correct it.");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonYes) {
                        Stack<String> crns = new Stack();
                        for (int i = 0; i < 10; i++) {
                            if (!crnBoxes.get(i).getText().isEmpty()) {
                                crns.push(crnBoxes.get(i).getText());
                            }
                        }
                        String scheduleYear = scheduleDate.getValue().format(dateFormatter).substring(6, 9);
                        WingsExpressConnector connector = new WingsExpressConnector(password.getText(), userName.getText(), scheduleYear + semester, crns, log);
                        if (connector.loginTest()) {
                            Alert regError = new Alert(Alert.AlertType.ERROR, "You seem to have miss typed your login info.");
                            regError.setHeaderText("Incorrect login");
                            regError.showAndWait();
                        } else {
                            String dateTime = clock.getCurrentDateAndTime().substring(14, clock.getCurrentDateAndTime().length());
                            ScheduleWaiter waiter = new ScheduleWaiter(clock, scheduleDate.getValue().format(dateFormatter), timeToSchedule, password.getText(), userName.getText(), semester, crns, log);
                            Thread waiterThread = new Thread(waiter);
                            waiterThread.start();
                        }
                    }
                }
            }
        }
        );

        scheduleNow.setOnAction(e
                -> {
            int emptyCrnBoxes = 0;
            String semesterString = "";
            int semester;
            if (semesterButtons.getSelectedToggle() == fall) {
                semester = 80;
                semesterString = "fall";
            } else if (semesterButtons.getSelectedToggle() == summer) {
                semester = 40;
                semesterString = "Summer";
            } else if (semesterButtons.getSelectedToggle() == spring) {
                semester = 30;
                semesterString = "Spring";
            } else {
                semester = 0;
            }
            for (int i = 0; i < crnBoxes.size(); i++) {
                if (crnBoxes.get(i).getText().isEmpty()) {
                    emptyCrnBoxes++;
                }
            }
            if (userName.getText().isEmpty() || password.getText().isEmpty()) {
                Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
                regError.setHeaderText("Empty login");
                regError.showAndWait();
            } else if (scheduleDate.getValue().format(dateFormatter).length() < 4) {
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
                alert.setTitle("Submit?");
                alert.setHeaderText(null);
                ButtonType buttonYes = new ButtonType("Yes");
                ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(buttonYes, buttonNo);
                alert.setContentText("All preliminary tests have passed but please note this program does not test for valid CRN numbers. "
                        + "Currently you will be registering for the following date/semester: \n"
                        + scheduleDate.getValue().format(dateFormatter) + " " + semesterString
                        + "\nIf you would like to continue with this info press yes. If something looks wrong press no and please correct it.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonYes) {
                    Stack<String> crns = new Stack();
                    for (int i = 0; i < 10; i++) {
                        if (!crnBoxes.get(i).getText().isEmpty()) {
                            crns.push(crnBoxes.get(i).getText());
                        }
                    }
                    String scheduleYear = scheduleDate.getValue().format(dateFormatter).substring(6, 10);
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
                            log.println(Arrays.toString(ex.getStackTrace()));
                        }
                        String content = connectorReal.getContent();
                        try {
                            if (content.contains("Registration Add Errors")) {
                                Alert regError = new Alert(Alert.AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn.");
                                regError.setHeaderText("Registration Add Error");
                                regError.showAndWait();
                            }
                            if (content.contains("Corequisite")) {
                                Alert coReqError = new Alert(Alert.AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                                coReqError.setHeaderText("Corequisite Error");
                                coReqError.showAndWait();
                            }
                        } catch (NullPointerException ex) {
                            Alert coReqError = new Alert(Alert.AlertType.ERROR, "The date/semester combination you have selected does not work. Scheduling failed.");
                            coReqError.setHeaderText("Semester/Date selection error");
                            coReqError.showAndWait();
                        }
                    }
                }
            }
        }
        );
        Scene scene = new Scene(main, 1000, 200);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Wright State Scheduler");
        stage.getIcons().add(new Image("/Media/Icon.png"));
        stage.show();
        Alert welcome = new Alert(AlertType.INFORMATION, "Welcome to WSS! Your one stop shop to get some more sleep! "
                + "Now we know that you won't be getting more sleep until you trust the program but thank you for being our beta testers! "
                + "If everything goes well you could be saving yourself some sleep in no time. If you have any questions or concerns"
                + " please email me at roudebushcameron@gmail.com. Also be ready with those log files as I will want them to see how everything went!"
                + " \n\n Thanks again for your help! \n -Cameron ");
        welcome.setHeaderText("Welcome!");
        welcome.setTitle("WSS");
        welcome.showAndWait();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        }
        );
    }
}
