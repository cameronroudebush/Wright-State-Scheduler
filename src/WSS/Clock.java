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

    public Clock(TextField timeField, PrintStream log) {
        this.currentDateAndTime = timeField;
        this.log = log;
    }

    public String getCurrentDateAndTime() {
        return currentDateAndTime.getText();
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
                    currentDateAndTime.setText("Current Time: " + dateAndTime.format(date));
                    }catch (Exception e){
                        log.println("An Error has occured within the timer");
                    }
                }

            });
        }
    }
}
