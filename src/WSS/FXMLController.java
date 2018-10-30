package WSS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

/**
 * This class is used to control the FXML for the UI displays
 *
 * @author Cameron Roudebush
 */
public class FXMLController {

    private String UID;
    private String PIN;
    PrintStream log;
    @FXML
    private TextField uid;
    @FXML
    private TextField pin;

    public FXMLController() {
        try {
            this.log = new PrintStream(new File("log.txt"));
        } catch (FileNotFoundException ex) {
            System.out.println("Error, file could not be created.");
        }
    }

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        if (uid.getText().isEmpty() || pin.getText().isEmpty()) {
            log.println("Empty login");
            Alert regError = new Alert(AlertType.ERROR, "Either your password or uid box is empty.");
            regError.setHeaderText("Empty login");
            regError.showAndWait();
        } else {
            WingsExpressConnector connector = new WingsExpressConnector(pin.getText(), uid.getText(), log);
            int loginTest = connector.loginTest();
            if (loginTest == 1) {
                log.println("Incorrect login");
                Alert regError = new Alert(Alert.AlertType.ERROR, "You seem to have miss typed your login info.");
                regError.setHeaderText("Incorrect login");
                regError.showAndWait();
            } else {
                log.println("Sucessful login");
                UID = uid.getText();
                PIN = pin.getText();
            }
        }
    }
}
