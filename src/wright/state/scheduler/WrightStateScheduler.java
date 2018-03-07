package wright.state.scheduler;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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
        main.setPadding(new Insets(5,5,5,5));
        main.setVgap(10);
        main.setHgap(20);
        main.add(new Label("Enter CRN's Below:"), 0, 0,5,1);
        ArrayList<TextField> textFields = new ArrayList();
        for (int i = 0; i < 10; i++){
            textFields.add(new TextField());
            main.add(textFields.get(i), i, 1);
        }
        main.add(new Label("Enter your UID:"), 0, 3,2,1);
        TextField userName = new TextField();
        userName.setPromptText("U00123456");
        main.add(userName, 1, 3,2,1);
        main.add(new Label("Enter your PIN:"), 0, 4,2,1);
        PasswordField password = new PasswordField();
        main.add(password, 1, 4,2,1);
        main.add(new Label("Enter your schedule date:"), 4, 3,3,1);
        TextField scheduleDate = new TextField();
        scheduleDate.setPromptText("MM/DD/YYYY");
        main.add(scheduleDate, 6, 3,2,1);
        main.add(new Label("Enter your desired year and select semester:"), 4, 4,4,1);
        TextField semesterDate = new TextField();
        semesterDate.setPromptText("2018");
        RadioButton spring = new RadioButton("Spring");
        RadioButton fall = new RadioButton("Fall");
        RadioButton summer = new RadioButton("Summer");
        main.add(fall, 9, 4);
        main.add(summer, 8, 4);
        main.add(spring, 10, 4);
        ToggleGroup semesterButtons = new ToggleGroup();
        summer.setToggleGroup(semesterButtons);
        spring.setToggleGroup(semesterButtons);
        fall.setToggleGroup(semesterButtons);
        fall.setSelected(true);
        main.add(semesterDate, 7, 4,1,1);
        TextField time = new TextField();
        time.setEditable(false);
        main.add(time, 7, 0,3,1);
        Timer timer = new Timer();
        Clock clock = new Clock(time);
        timer.schedule(clock, 0, 1000);
        Button schedule = new Button("Schedule!");
        schedule.setMinWidth(305);
        main.add(schedule, 3, 5,6,1);
        schedule.setOnAction(e ->{
            Stack<String> crns = new Stack();
            for (int i = 0; i < 10; i++){
                if(!textFields.get(i).getText().isEmpty()){
                    crns.push(textFields.get(i).getText());
                }
            }
            //added summer semester ability
            int semester;
            if (semesterButtons.getSelectedToggle() == fall){
                semester = 80;
            }else if (semesterButtons.getSelectedToggle() == summer) {
                semester = 40;
            } else {
                semester = 30;
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
            //runs the connector in a new thread
            Thread t = new Thread(new WingsExpressConnector(password.getText(), userName.getText(), semesterDate.getText()+semester ,crns));
            //made the connector a thread
            t.run();
        });
        
        Scene scene = new Scene(main, 900,190);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Wright State Schedular");
        stage.show();
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }
    
}
