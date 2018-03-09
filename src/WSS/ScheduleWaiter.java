package WSS;

public class ScheduleWaiter implements Runnable {

    private Clock currentTime;
    private String scheduleDate;
    private String scheduleTime;

    public ScheduleWaiter(Clock currentTime, String scheduleDate, String scheduleTime) {
        this.currentTime = currentTime;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;
    }

    public void comparisonWaiter() {
        while (!currentTime.getCurrentDateAndTime().substring(14, 24).equals(scheduleDate)) {
            try {
                System.out.println("Hit wait command on date.");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
        while (!currentTime.getCurrentDateAndTime().substring(25, currentTime.getCurrentDateAndTime().length()).equals(scheduleTime)) {
            try {
                System.out.println("Hit wait command on time.");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }

    @Override
    public void run() {
        comparisonWaiter();
    }

}
