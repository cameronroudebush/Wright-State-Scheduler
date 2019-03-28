package WSS;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * This class is used as the connector to the wings express page This class is
 * vital for testing if the user has dealt with account restrictions and for
 * doing the actual registration
 *
 * @author Cameron Roudebush
 */
public class WingsExpressConnector implements Runnable {

    private final String pin;
    private final String uid;
    private final String semester;
    private final Stack<String> crns;
    private String content;
    private PrintStream log;

    /**
     * Wings express connector used for running the real deal of scheduling
     *
     * @param pin The users password
     * @param uid The users user name
     * @param semester The semester the user selected
     * @param crns The crns the user put in
     * @param log The log print stream for errors
     */
    public WingsExpressConnector(String pin, String uid, String semester, Stack<String> crns, PrintStream log) {
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = crns;
        this.log = log;
    }

    /**
     * Wings express connector used for running login tests
     *
     * @param pin The users password
     * @param uid The users user name
     * @param log The log print stream for errors
     */
    public WingsExpressConnector(String pin, String uid, PrintStream log) {
        this.pin = pin;
        this.uid = uid;
        this.semester = null;
        this.crns = null;
        this.log = log;
    }

    /**
     * Wings express connector used for running semester checks
     *
     * @param pin The users password
     * @param uid The users user name
     * @param semester The semester the user selected
     * @param log The print stream of the log
     */
    public WingsExpressConnector(String pin, String uid, String semester, PrintStream log) {
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = null;
        this.log = log;
    }

    /**
     * Returns the content from the web page
     *
     * @return The content of the web page
     */
    public String getContent() {
        return content;
    }

    /**
     * Puts in the crns for the user. This is the main function that actually
     * inserts data and registers the user on wings express.
     */
    public void pluginCrns() {
        try {
            log.println("Running CRN plugin");
            //Generate the web client
            WebClient webClient = new WebClient();
            //Load the login page
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu");
            log.println("Sucessfully connected to login page");
            //Locate the UserID box from xml
            HtmlInput userBox = page.getFirstByXPath("//*[@id='UserID']");
            log.println("UID box located" + userBox);
            //Plugin UID
            userBox.setValueAttribute(uid);
            log.println("UID inserted.");
            //Locate the Pin box from xml
            log.println("PIN box located.");
            HtmlInput pinBox = (HtmlInput) page.getFirstByXPath("//*[@id=\"PIN\"]/input");
            log.println(pinBox);
            //Plugin Pin
            pinBox.setValueAttribute(pin);
            log.println("PIN inserted.");
            //Locate submit login button from xml
            log.println("Located login button.");
            HtmlSubmitInput submitButton = page.getFirstByXPath("/html/body/div[4]/form/p/input[1]");
            log.println(submitButton);
            //Click submit button
            submitButton.click();
            log.println("Login button clicked. Redirecting to add or drop classes page.");
            //change page to add or drop classes page
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout");
            log.println("Redirected successfully.");
            //Locate the drop down box for semester selection
            log.println("Located semester drop down box.");
            HtmlSelect semesterDropDown = page.getFirstByXPath("//*[@id=\"term_id\"]");
            log.println(semesterDropDown);
            //Select proper semester
            log.println("Inserting semester option.");
            log.println(semester);
            try {
                HtmlOption semesterOption = semesterDropDown.getOptionByValue(semester);
                semesterOption.setSelected(true);
                log.println("Setting that semester option.");
                //Locate submit semester button
                log.println("Located submit semester selection button.");
                HtmlSubmitInput submitSemester = page.getFirstByXPath("/html/body/div[4]/form/input");
                log.println(submitSemester);
                //Click the submit semester button
                submitSemester.click();
                log.println("Clicking submit semester selection button.");
                //Change pages to actual add or drop for classes
                page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout");
                log.println("Redirecting to crn plug in page.");
                ArrayList<HtmlInput> crnBoxes = new ArrayList();
                //Load all 10 crn boxes into an arrayList for later use
                for (int i = 1; i < 10; i++) {
                    crnBoxes.add((HtmlInput) page.getFirstByXPath("//*[@id=\"crn_id" + i + "\"]"));
                }
                log.println("Found crnBoxes.");
                log.println(crnBoxes);
                //Add any CRNs we have into the given boxes
                log.println("Inserting CRN's given.");
                log.println(crns);
                if (crns.isEmpty()) {
                    log.println("Empty CRN stack.");
                }
                int i = 0;
                while (!crns.isEmpty()) {
                    crnBoxes.get(i).setValueAttribute(crns.pop());
                    i++;
                }
                //Locate the submit button for CRNs
                log.println("Located submit CRN's button.");
                HtmlSubmitInput submitCrns = page.getFirstByXPath("/html/body/div[4]/form/input[19]");
                log.println(submitCrns);
                //Click the submit CRNs buttons
                page = submitCrns.click();
                log.println("Clicking submit CRN's button.");
                WebResponse response = page.getWebResponse();
                content = response.getContentAsString();
                if (content.contains("Waitlisted")) {
                    log.println("Waitlisted class or classes found. Auto accepting.");
                    for (int b = 1; b < 10; b++) {
                        if (content.contains("waitaction_id" + b)) {
                            HtmlSelect waitListDropDown = page.getFirstByXPath("//*[@id=\"waitaction_id" + b + "\"]");
                            HtmlOption waitListOption = waitListDropDown.getOptionByValue("WL");
                            waitListOption.setSelected(true);
                        }
                    }
                    submitCrns = page.getFirstByXPath("/html/body/div[4]/form/input[19]");
                    submitCrns.click();
                    log.println("Clicking submit CRN's button for auto waitlist.");
                }
                if (content.contains("Registration Add Errors")) {
                    log.println("CRN registration add failure");
                }
                if (content.contains("Corequisite")) {
                    log.println("Corequisite error");
                }
                log.println("End of plugin CRN's.");
            } catch (NullPointerException ex) {
                log.println("Potential error in selecting the semester");
                log.println(Arrays.toString(ex.getStackTrace()));
            }
        } catch (ElementNotFoundException | FailingHttpStatusCodeException | IOException e) {
            log.println(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Runs only the login test. Used when user is first attempting to sign in.
     * Returns 0 for no problems, 1 for a failed login.
     *
     * @return The int of the results of the test
     */
    public int loginTestOnly() {
        try {
            log.println("Running login only test.");
            WebClient webClient = new WebClient();
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu");
            log.println("Sucessfully connected to login page.");
            log.println("UID box located.");
            HtmlInput userBox = page.getFirstByXPath("//*[@id='UserID']");
            log.println(userBox);
            userBox.setValueAttribute(uid);
            log.println("UID inserted.");
            log.println("PIN box located.");
            HtmlInput pinBox = (HtmlInput) page.getFirstByXPath("//*[@id=\"PIN\"]/input");
            log.println(pinBox);
            pinBox.setValueAttribute(pin);
            log.println("PIN inserted.");
            log.println("Located login button.");
            HtmlSubmitInput submitButton = page.getFirstByXPath("/html/body/div[4]/form/p/input[1]");
            log.println(submitButton);
            submitButton.click();
            log.println("Login button clicked. Redirecting to financial aid menu.");
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu");
            WebResponse response = page.getWebResponse();
            content = response.getContentAsString();
            if (!content.contains("Student and Financial Aid")) {
                log.println("Failed loginTest");
                return 1;
            }
            log.println("Passed login only test.");
            return 0;
        } catch (FailingHttpStatusCodeException | IOException e) {
            log.println("Exception caught: defaulting to failed login.");
            log.println(Arrays.toString(e.getStackTrace()));
            return 1;
        }
    }

    //
    /**
     * Runs only the login test. Used when user is first attempting to sign in.
     * Returns 0 for no problems, 1 for a failed login, 2 for failed a hold, 
     *  3 for a failed financial acknowledgment
     *
     * @return The int of the results of the test
     */
    public int loginTest() {
        try {
            log.println("Running login test.");
            WebClient webClient = new WebClient();
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu");
            log.println("Sucessfully connected to login page.");
            log.println("UID box located.");
            HtmlInput userBox = page.getFirstByXPath("//*[@id='UserID']");
            log.println(userBox);
            userBox.setValueAttribute(uid);
            log.println("UID inserted.");
            log.println("PIN box located.");
            HtmlInput pinBox = (HtmlInput) page.getFirstByXPath("//*[@id=\"PIN\"]/input");
            log.println(pinBox);
            pinBox.setValueAttribute(pin);
            log.println("PIN inserted.");
            log.println("Located login button.");
            HtmlSubmitInput submitButton = page.getFirstByXPath("/html/body/div[4]/form/p/input[1]");
            log.println(submitButton);
            submitButton.click();
            log.println("Login button clicked. Redirecting to financial aid menu.");
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu");
            WebResponse response = page.getWebResponse();
            content = response.getContentAsString();
            if (!content.contains("Student and Financial Aid")) {
                log.println("Failed loginTest");
                return 1;
            }
            int failureChecks = holdTest(webClient);
            if (failureChecks != 0) {
                log.println("Failed hold test.");
                return failureChecks;
            }
            failureChecks = acknowledgmentTest(webClient);
            if (failureChecks != 0) {
                log.println("Failed awknowlegement test.");
                return failureChecks;
            }
            log.println("Passed loginTest");
            return 0;
        } catch (FailingHttpStatusCodeException | IOException e) {
            log.println("Exception caught: defaulting to failed login.");
            log.println(Arrays.toString(e.getStackTrace()));
            return 1;
        }
    }

    /**
     * Runs the hold test to make sure the user doesn't have holds on their
     * account
     * Returns 2 for failed a hold or 0 for no problems
     * 
     * @param webClient The connection web client
     * @return The result of the test
     */
    public int holdTest(WebClient webClient) {
        try {
            log.println("Running hold test.");
            int failureCheck = 0;
            log.println("Redirecting to Registration Status page.");
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/bwskrsta.P_RegsStatusDisp");
            log.println("Redirected successfully.");
            log.println("Located semester drop down box.");
            HtmlSelect semesterDropDown = page.getFirstByXPath("//*[@id=\"term_id\"]");
            log.println(semesterDropDown);
            log.println("Inserting semester option.");
            log.println(semester);
            HtmlOption semesterOption = semesterDropDown.getOptionByValue(semester);
            semesterOption.setSelected(true);
            log.println("Setting that semester option.");
            log.println("Located submit semester selection button.");
            HtmlSubmitInput submitSemester = page.getFirstByXPath("/html/body/div[4]/form/input");
            log.println(submitSemester);
            submitSemester.click();
            log.println("Clicking submit semester selection button.");
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/bwskrsta.P_RegsStatusDisp");
            WebResponse response = page.getWebResponse();
            content = response.getContentAsString();
            if ((!content.contains("You have no Holds which prevent registration.")) || (content.contains("You have an Alternate PIN which prevents registration"))) {
                failureCheck = 2;
            }
            return failureCheck;
        } catch (IOException | FailingHttpStatusCodeException ex) {
            log.println(Arrays.toString(ex.getStackTrace()));
            return 2;
        }
    }

    /**
     * Runs the acknowledgment test to make sure the user has dealt with that
     * Returns 3 for a failed financial acknowledgment or 0 for no problems
     * 
     * @param webClient The connection client
     * @return The int of how the test turned out
     */
    public int acknowledgmentTest(WebClient webClient) {
        try {
            log.println("Running awknowledgement test.");
            int failureCheck = 0;
            log.println("Redirecting to Acknowledgement page.");
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/WsuStuReqAck.P_presentques");
            log.println("Redirected successfully.");
            WebResponse response = page.getWebResponse();
            content = response.getContentAsString();
            if (!content.contains("You have accepted the terms and conditions set forth in the detailed Statement of Financial Responsibility.")) {
                failureCheck = 3;
            }
            return failureCheck;
        } catch (IOException | FailingHttpStatusCodeException ex) {
            log.println(Arrays.toString(ex.getStackTrace()));
            return 3;
        }
    }

    @Override
    public void run() {
        pluginCrns();
    }
}
