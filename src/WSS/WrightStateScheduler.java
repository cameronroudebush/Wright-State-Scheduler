package WSS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
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
        main.add(new Label("Enter CRN's Below:"), 0, 0, 5, 1);
        ArrayList<TextField> crnBoxes = new ArrayList();
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
        main.add(new Label("Enter your UID:"), 0, 3, 2, 1);
        TextField userName = new TextField();
        userName.setPromptText("U00123456");
        TextFormatter<TextField> userFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 9) {
                return null;
            }
            return e;
        });
        userName.setTextFormatter(userFormatter);
        main.add(userName, 1, 3, 2, 1);
        main.add(new Label("Enter your PIN:"), 0, 4, 2, 1);
        PasswordField password = new PasswordField();
        main.add(password, 1, 4, 2, 1);
        main.add(new Label("Enter your schedule date:"), 3, 3, 3, 1);
        TextField scheduleDate = new TextField();
        TextFormatter<TextField> dateFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 10) {
                return null;
            }
            return e;
        });
        scheduleDate.setTextFormatter(dateFormatter);
        scheduleDate.setPromptText("MM/DD/YYYY");
        main.add(scheduleDate, 5, 3, 1, 1);
        main.add(new Label("Enter your schedule time:"), 3, 4, 4, 1);
        TextField scheduleTime = new TextField();
        TextFormatter<TextField> timeFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 8) {
                return null;
            }
            return e;
        });
        scheduleTime.setTextFormatter(timeFormatter);
        scheduleTime.setPromptText("HH:MM:SS");
        main.add(scheduleTime, 5, 4, 1, 1);
        RadioButton spring = new RadioButton("Spring");
        RadioButton fall = new RadioButton("Fall");
        RadioButton summer = new RadioButton("Summer");
        main.add(new Label("Select Semester:"), 7, 3, 3, 1);
        main.add(spring, 6, 4);
        main.add(summer, 7, 4);
        main.add(fall, 8, 4);
        ToggleGroup semesterButtons = new ToggleGroup();
        summer.setToggleGroup(semesterButtons);
        spring.setToggleGroup(semesterButtons);
        fall.setToggleGroup(semesterButtons);
        TextField time = new TextField();
        time.setEditable(false);
        main.add(time, 7, 0, 3, 1);
        Timer timer = new Timer();
        Clock clock = new Clock(time);
        timer.schedule(clock, 0, 1000);
        Button schedule = new Button("Schedule!");
        schedule.setMinWidth(305);
        main.add(schedule, 3, 5, 6, 1);
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
                    String dateTime = clock.getCurrentDateAndTime().substring(14, clock.getCurrentDateAndTime().length());
//            while (!dateTime.substring(0,10).equals(scheduleDate.getText())){
//                try {
//                    System.out.println("Hit wait command on date.");
//                    Thread.sleep(60000);
//                } catch (InterruptedException ex) {
//                    System.out.println("Error in waiting");
//                }
//            }
//            while (!dateTime.substring(12,dateTime.length()).equals("07:00:00")){
//                try {
//                    System.out.println(dateTime.substring(12,dateTime.length()));
//                    System.out.println("Hit wait command on time.");
//                    Thread.sleep(60000);
//                } catch (InterruptedException ex) {
//                    System.out.println("Error in waiting");
//                }
//            }
                    try {
                        PrintStream log = new PrintStream(new File("log.txt"));
                        String scheduleYear = scheduleDate.getText().substring(scheduleDate.getText().length() - 4, scheduleDate.getText().length());
                        WingsExpressConnector connector = new WingsExpressConnector(password.getText(), userName.getText(), scheduleYear + semester, crns, log);
                        if (connector.loginTest()) {
                            Alert regError = new Alert(AlertType.ERROR, "You seem to have miss typed your login info.");
                            regError.setHeaderText("Incorrect login");
                            regError.showAndWait();
                        } else {
                            Thread t = new Thread(connector);
                            t.start();
                            t.join();
                            String content = connector.getContent();
                            if (content.contains("Registration Add Errors")) {
                                Alert regError = new Alert(AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn.");
                                regError.setHeaderText("Registration Add Error");
                                regError.showAndWait();
                            }
                            if (content.contains("Corequisite")) {
                                Alert coReqError = new Alert(AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                                coReqError.setHeaderText("Corequisite Error");
                                coReqError.showAndWait();;
                            }
                        }
                    } catch (InterruptedException | FileNotFoundException ex) {
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
