package WSS;

import java.io.PrintStream;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;

public class ScheduleWaiter implements Runnable {
    
    private Clock currentTime;
    private final String scheduleDate;
    private final String scheduleTime;
    private final String pin;
    private final String uid;
    private final int semester;
    private final Stack<String> crns;
    private String content;
    private PrintStream log;
    private boolean errorFound = false;
    private ProgressIndicator progressIndicator;
    
    public ScheduleWaiter(Clock currentTime, String scheduleDate, String scheduleTime, String pin, String uid, int semester, Stack<String> crns,
             PrintStream log, ProgressIndicator indicator) {
        this.currentTime = currentTime;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = crns;
        this.log = log;
        this.progressIndicator = indicator;
    }
    
    public String getContent() {
        return content;
    }
    
    private void comparisonWaiter() {
        while (!currentTime.getCurrentDateAndTime().substring(14, 24).equals(scheduleDate)) {
            try {
//                log.println("Hit wait command on date.");
//                log.print(currentTime.getCurrentDateAndTime() + " " + scheduleDate);
                log.println();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        while (!currentTime.getCurrentDateAndTime().substring(25, currentTime.getCurrentDateAndTime().length()).equals(scheduleTime)) {
            try {
//                log.println("Hit wait command on time.");
//                log.print(currentTime.getCurrentDateAndTime() + " " + scheduleTime);
                log.println();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        try {
            String scheduleYear = scheduleDate.substring(scheduleDate.length() - 4, scheduleDate.length());
            WingsExpressConnector connector = new WingsExpressConnector(pin, uid, scheduleYear + semester, crns, log);
            {
                Thread t = new Thread(connector);
                t.start();
                t.join();
                content = connector.getContent();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progressIndicator.setVisible(false);
                            if (content.contains("Registration Add Errors")) {
                                errorFound = true;
                                Alert regError = new Alert(Alert.AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn or potentially a class being waitlisted.");
                                regError.setHeaderText("Registration Add Error");
                                regError.showAndWait();
                            }
                            if (content.contains("Corequisite")) {
                                errorFound = true;
                                Alert coReqError = new Alert(Alert.AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                                coReqError.setHeaderText("Corequisite Error");
                                coReqError.showAndWait();
                            }
                            if (!errorFound) {
                                Alert winner = new Alert(Alert.AlertType.CONFIRMATION, "The schedular seemed to have completed without any errors. We do recommend you double check with WingsExpress to be sure but it looks good on our end! \n Thanks for using WSS!");
                                winner.setHeaderText("You Made it!");
                                winner.setTitle("Congratulations!");
                                winner.showAndWait();
                            }
                        } catch (NullPointerException e) {
                            Alert coReqError = new Alert(Alert.AlertType.ERROR, "The date/semester combination you have selected does not work. Scheduling failed.");
                            coReqError.setHeaderText("Semester/Date selection error");
                            coReqError.showAndWait();;
                        }
                    }
                }); 
            }
        } catch (InterruptedException ex) {
        }
        
    }
    
    @Override
    public void run() {
        comparisonWaiter();
    }
    
}
