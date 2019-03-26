package WSS;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This is the main area for WSS and what begins the login page, and main program.
 *  From here you can't do much but load up the beginning content.
 * @author croudebush
 */
public class WrightStateScheduler extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
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
