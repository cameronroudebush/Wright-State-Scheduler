package WSS;

import java.io.PrintStream;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;

/**
 * This class is used as the meat for waiting to schedule classes
 *
 * @author Cameron Roudebush
 */
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

    /**
     * Constructor for setting information
     *
     * @param currentTime Our current time
     * @param scheduleDate The date we wish to schedule
     * @param scheduleTime The time we wish to schedule
     * @param pin The password of the user
     * @param uid The uid of the user
     * @param semester The semester selected by the user
     * @param crns All of the crns the user has inserted
     * @param log The log print stream for logging
     * @param indicator The progress indicator incase we need to use it
     */
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

    /**
     * This is used to get the content of the wings express page
     *
     * @return The content of the connection to wings express
     */
    public String getContent() {
        return content;
    }

    /**
     * This function is used to wait on the current thread for the schedule time
     */
    private void comparisonWaiter() {
        while (!currentTime.getCurrentDateAndTime().substring(14, 24).equals(scheduleDate)) {
            // Attempt to wait since we are at the wrong date
            try {
                log.println("Hit wait command on date: " + currentTime.getCurrentDateAndTime() + " " + scheduleDate);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        while (!currentTime.getCurrentDateAndTime().substring(25, currentTime.getCurrentDateAndTime().length()).equals(scheduleTime)) {
            // Attempt to wait since we are at the wrong time
            try {
                log.println("Hit wait command on time: " + currentTime.getCurrentDateAndTime() + " " + scheduleTime);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        try {
            // We are done waiting so move on
            String scheduleYear = scheduleDate.substring(scheduleDate.length() - 4, scheduleDate.length());
            WingsExpressConnector connector = new WingsExpressConnector(pin, uid, scheduleYear + semester, crns, log);
            {
                Thread t = new Thread(connector);
                t.start();
                t.join();
                content = connector.getContent();
                // Alert the user of the results
                Platform.runLater(() -> {
                    try {
                        progressIndicator.setVisible(false);
                        if (content.contains("Registration Add Errors")) {
                            errorFound = true;
                            Alert regError = new Alert(Alert.AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn or potentially a class being waitlisted.");
                            regError.setHeaderText("Registration Add Error");
                            regError.showAndWait();
                            log.print("Registration Add Error");
                        }
                        if (content.contains("Corequisite")) {
                            errorFound = true;
                            Alert coReqError = new Alert(Alert.AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                            coReqError.setHeaderText("Corequisite Error");
                            coReqError.showAndWait();
                            log.print("Corequisite Error");
                        }
                        if (!errorFound) {
                            Alert winner = new Alert(Alert.AlertType.CONFIRMATION, "The schedular seemed to have completed without any errors. We do recommend you double check with WingsExpress to be sure but it looks good on our end! \n Thanks for using WSS!");
                            winner.setHeaderText("You Made it!");
                            winner.setTitle("Congratulations!");
                            winner.showAndWait();
                            log.print("Everything finished successfully");
                        }
                    } catch (NullPointerException e) {
                        Alert coReqError = new Alert(Alert.AlertType.ERROR, "The date/semester combination you have selected does not work. Scheduling failed.");
                        coReqError.setHeaderText("Semester/Date selection error");
                        coReqError.showAndWait();
                        log.print("Semester/Date selection error");
                    }
                });
            }
        } catch (InterruptedException ex) {
            log.print("Exception caught: " + ex);
        }
    }

    @Override
    public void run() {
        comparisonWaiter();
    }

}
