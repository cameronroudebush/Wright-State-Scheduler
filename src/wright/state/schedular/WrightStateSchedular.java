package wright.state.schedular;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WrightStateSchedular extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane main = new GridPane();
        main.setPadding(new Insets(5,5,5,5));
        main.setVgap(10);
        main.setHgap(20);
        main.add(new Label("Please enter CRN's Below"), 0, 0,5,1);
        ArrayList<TextField> textFields = new ArrayList();
        for (int i = 0; i < 10; i++){
            textFields.add(new TextField());
            main.add(textFields.get(i), i, 1);
        }
        main.add(new Label("Please enter your UID."), 0, 3,3,1);
        TextField userName = new TextField();
        userName.setPromptText("U00123456");
        main.add(userName, 2, 3,2,1);
        main.add(new Label("Please enter your PIN."), 0, 4,3,1);
        PasswordField password = new PasswordField();
        main.add(password, 2, 4,2,1);
        main.add(new Label("Please enter your schedule date"), 4, 3,3,1);
        TextField scheduleDate = new TextField();
        scheduleDate.setPromptText("MM/DD/YYYY");
        main.add(scheduleDate, 7, 3,2,1);
        main.add(new Label("Please enter your desired semester."), 4, 4,3,1);
        TextField semesterSelect = new TextField();
        semesterSelect.setText("201780");
        main.add(semesterSelect, 7, 4,2,1);
        TextField time = new TextField();
        time.setEditable(false);
        main.add(time, 7, 0,3,1);
        Timer timer = new Timer();
        timer.schedule(new Clock(time), 0, 1000);
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
            WingsExpressConnector connector = new WingsExpressConnector(password.getText(), userName.getText(), semesterSelect.getText(),crns);
            connector.pluginCrns();
        });
        
        Scene scene = new Scene(main, 800,175);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Wright State Schedular");
        stage.show();
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }
    
}
