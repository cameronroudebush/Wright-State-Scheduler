package WSS;

import java.io.PrintStream;
import java.util.Stack;
import javafx.application.Platform;
import javafx.scene.control.Alert;

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

    public ScheduleWaiter(Clock currentTime, String scheduleDate, String scheduleTime, String pin, String uid, int semester, Stack<String> crns, PrintStream log) {
        this.currentTime = currentTime;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = crns;
        this.log = log;
    }

    public String getContent() {
        return content;
    }

    private void comparisonWaiter() {
        while (!currentTime.getCurrentDateAndTime().substring(14, 24).equals(scheduleDate)) {
            try {
                log.println("Hit wait command on date.");
                log.print(currentTime.getCurrentDateAndTime() + scheduleDate);
                log.println();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        while (!currentTime.getCurrentDateAndTime().substring(25, currentTime.getCurrentDateAndTime().length()).equals(scheduleTime)) {
            try {
                log.println("Hit wait command on time.");
                log.print(currentTime.getCurrentDateAndTime() + scheduleTime);
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
                            if (content.contains("Registration Add Errors")) {
                                Alert regError = new Alert(Alert.AlertType.ERROR, "There was an error adding the crn's. Please check with WingsExpress to see what didn't get added. This is normally due to a miss-typed crn.");
                                regError.setHeaderText("Registration Add Error");
                                regError.showAndWait();
                            }
                            if (content.contains("Corequisite")) {
                                Alert coReqError = new Alert(Alert.AlertType.ERROR, "There was some sort of error adding the crn's. You seemed to have forgotten a corequisite. Please check with WingsExpress to resolve this.");
                                coReqError.setHeaderText("Corequisite Error");
                                coReqError.showAndWait();;
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
