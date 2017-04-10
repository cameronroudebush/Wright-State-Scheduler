package wright.state.schedular;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import javafx.scene.control.TextField;
import javax.swing.SwingUtilities;

public class Clock extends TimerTask {

    TextField currentDateAndTime;

    public Clock(TextField timeField) {
        this.currentDateAndTime = timeField;
    }

    
    @Override
    public void run() {
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DateFormat dateAndTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date = new Date();
                    try{
                    currentDateAndTime.setText("Current Time: " + dateAndTime.format(date));
                    }catch (Exception e){
                        System.out.println("An Error has occured within the timer");
                    }
                }

            });
        }
    }
}
