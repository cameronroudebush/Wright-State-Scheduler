package WSS;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import javafx.scene.control.TextField;
import javax.swing.SwingUtilities;

/**
 * This class is used as the clock to tell the current time.
 *  It is used to tell when scheduling should occur
 * @author Cameron Roudebush
 */
public class Clock extends TimerTask {

    TextField currentDateAndTime;
    private PrintStream log;
    private String uid;

    /**
     * The one and only constructor
     * @param timeField The field where the time needs displayed
     * @param uid The uid of the user
     * @param log The log print stream for errors
     */
    public Clock(TextField timeField, String uid, PrintStream log) {
        this.currentDateAndTime = timeField;
        this.uid = uid;
        this.log = log;
    }

    /**
     * Gets the current date and time in a pretty string
     * @return The current date and time in a pretty string
     */
    public String getCurrentDateAndTime() {
        return currentDateAndTime.getText().substring(21, currentDateAndTime.getText().length());
    }

    @Override
    public void run() {
        {
            SwingUtilities.invokeLater(() -> {
                DateFormat dateAndTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                Date date = new Date();
                try {
                    currentDateAndTime.setText("Welcome: " + uid.toUpperCase() + "   Current Time: " + dateAndTime.format(date));
                } catch (Exception e) {
                    log.println("An Error has occured within the timer");
                }
            });
        }
    }
}
