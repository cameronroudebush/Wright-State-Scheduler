package WSS;



import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import javafx.scene.control.TextField;
import javax.swing.SwingUtilities;

public class Clock extends TimerTask {

    TextField currentDateAndTime;
    private PrintStream log;
    private String uid;

    public Clock(TextField timeField, PrintStream log, String uid) {
        this.currentDateAndTime = timeField;
        this.log = log;
        this.uid = uid;
    }

    public String getCurrentDateAndTime() {
        return currentDateAndTime.getText().substring(21, currentDateAndTime.getText().length());
    }

    
    @Override
    public void run() {
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DateFormat dateAndTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                    Date date = new Date();
                    try{
                    currentDateAndTime.setText("Welcome: " + uid.toUpperCase() +"   Current Time: " + dateAndTime.format(date));
                    }catch (Exception e){
                        log.println("An Error has occured within the timer");
                    }
                }
            });
        }
    }
}
