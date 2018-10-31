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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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
        PrintStream log = new PrintStream(new File("log.txt"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        GridPane timePane = new GridPane();
        GridPane semesterPane = new GridPane();
        Parent main = FXMLLoader.load(getClass().getResource("/fxml/WSS_Login.fxml"));
        
        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Wright State Scheduler");
        stage.getIcons().add(new Image("/Media/Icon.png"));
        stage.show();
//        Alert welcome = new Alert(AlertType.INFORMATION, "Welcome to WSS! Your one stop shop to get some more sleep! "
//                + "Now we know that you won't be getting more sleep until you trust the program but thank you for being our beta testers! "
//                + "If everything goes well you could be saving yourself some sleep in no time. If you have any questions or concerns"
//                + " please email me at roudebushcameron@gmail.com. Also be ready with those log files as I will want them to see how everything went!"
//                + " \n\n Thanks again for your help! \n -Cameron ");
//        welcome.setHeaderText("Welcome!");
//        welcome.setTitle("WSS");
//        welcome.showAndWait();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        }
        );
    }
}
