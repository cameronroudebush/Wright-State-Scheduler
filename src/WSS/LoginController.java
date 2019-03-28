package WSS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class is used to control the FXML for the main login display
 *
 * @author Cameron Roudebush
 */
public class LoginController implements Initializable {

    private String UID;
    private String PIN;
    PrintStream log;
    @FXML
    private TextField uid;
    @FXML
    private TextField pin;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Text capsLockWarning;

    /**
     * This is the first class ever actually ran so we get to start it all!
     */
    public LoginController() {
        try {
            // Create the log file
            this.log = new PrintStream(new File("log.txt"));
        } catch (FileNotFoundException ex) {
            System.out.println("Error, file could not be created.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Force the UID to be a certain format
        TextFormatter<TextField> userFormatter = new TextFormatter<>(e -> {
            if (e.getControlNewText().length() > 9) {
                return null;
            }
            return e;
        });
        uid.setTextFormatter(userFormatter);
    }

    /**
     * This function handles the login button
     *
     * @param event Not used
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    protected void handleLoginButtonAction(ActionEvent event) throws IOException, InterruptedException {
        // Make the progress indicator visible
        progressIndicator.setVisible(true);
        // Create a service to run the login page
        Service<Boolean> ser = new Service<Boolean>() {
            @Override
            protected Task createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws InterruptedException {
                        // Check for empty login info
                        if (uid.getText().isEmpty() || pin.getText().isEmpty()) {
                            Platform.runLater(() -> {
                                progressIndicator.setVisible(false);
                                log.println("Empty login");
                                Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
                                regError.setHeaderText("Empty login");
                                regError.showAndWait();
                            });
                            return true;
                        } else {
                            // Attempt to login to wings express
                            WingsExpressConnector connector = new WingsExpressConnector(pin.getText(), uid.getText(), log);
                            int loginTest = connector.loginTestOnly();
                            // If the login test results as 1 then the login info was wrong
                            if (loginTest == 1) {
                                Platform.runLater(() -> {
                                    progressIndicator.setVisible(false);
                                    log.println("Incorrect login");
                                    Alert regError = new Alert(Alert.AlertType.ERROR, "You seem to have miss typed your login info.");
                                    regError.setHeaderText("Incorrect login");
                                    regError.showAndWait();
                                });
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        };
        ser.setOnSucceeded((WorkerStateEvent currentEvent) -> {
            Boolean processValue = ser.getValue();
            if (processValue == false) {
                try {
                    // The login was successful
                    progressIndicator.setVisible(false);
                    log.println("Sucessful login");
                    UID = uid.getText();
                    PIN = pin.getText();
                    // Load the main login page
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.this.getClass().getResource("/fxml/WSS_Main.fxml"));
                    Parent newPane = fxmlLoader.load();
                    MainController controller = fxmlLoader.<MainController>getController();
                    controller.setInfo(UID, PIN, log);
                    Scene scene = new Scene(newPane, 640, 450);
                    Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    appStage.setScene(scene);
                    appStage.show();
                } catch (IOException ex) {
                    log.println("IO error for fxml" + ex);
                }
            }
        });
        ser.start();
    }
}
