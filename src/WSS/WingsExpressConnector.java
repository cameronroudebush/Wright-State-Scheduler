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

public class WingsExpressConnector implements Runnable {

    private final String pin;
    private final String uid;
    private final String semester;
    private final Stack<String> crns;
    private String content;
    private PrintStream log;

    public WingsExpressConnector(String pin, String uid, String semester, Stack<String> crns, PrintStream log) {
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = crns;
        this.log = log;
    }

    public WingsExpressConnector(String pin, String uid, String semester, PrintStream log) {
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.log = log;
        this.crns = null;
    }

    public String getContent() {
        return content;
    }

    public void pluginCrns() {
        try {
            log.println("Running CRN plugin.");
            //Generate the web client
            WebClient webClient = new WebClient();
            //Load the login page
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu");
            log.println("Sucessfully connected to login page.");
            //Locate the UserID box from xml
            log.println("UID box located.");
            HtmlInput userBox = page.getFirstByXPath("//*[@id='UserID']");
            log.println(userBox);
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
        } catch (ElementNotFoundException | FailingHttpStatusCodeException | IOException e) {
            log.println(Arrays.toString(e.getStackTrace()));
        }
    }

    //returns 0 for no problems, 1 for a failed login, 2 for failed a hold, 3 for a failed financial acknowledment
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
            failureChecks = awknowledgementTest(webClient);
            if (failureChecks != 0) {
                log.println("Failed awknowlegement test.");
                return failureChecks;
            }
            log.println("Passed loginTest, holdTest, and awknowledgementTest");
            return 0;
        } catch (FailingHttpStatusCodeException | IOException e) {
            log.println("Exception caught: defaulting to failed login.");
            log.println(Arrays.toString(e.getStackTrace()));
            return 1;
        }
    }

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

    public int awknowledgementTest(WebClient webClient) {
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
