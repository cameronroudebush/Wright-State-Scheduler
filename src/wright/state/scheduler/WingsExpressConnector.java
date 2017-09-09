package wright.state.scheduler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class WingsExpressConnector {
    private final String pin;
    private final String uid;
    private final String semester;
    private final Stack<String> crns;
    
    public WingsExpressConnector(String pin, String uid, String semester, Stack<String> crns) {
        this.pin = pin;
        this.uid = uid;
        this.semester = semester;
        this.crns = crns;
    }
    
    public void pluginCrns (){
            System.out.println(pin + uid + semester + crns);
            try{
            //Generate the web client
            WebClient webClient = new WebClient();
            //Load the login page
            HtmlPage page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_GenMnu");
            //Locate the UserID box from xml
            HtmlInput userBox = page.getFirstByXPath("//*[@id='UserID']");
            //Plugin UID
            userBox.setValueAttribute(uid);
            //Locate the Pin box from xml
            HtmlInput pinBox = (HtmlInput) page.getFirstByXPath("//*[@id=\"PIN\"]/input");
            //Plugin Pin
            pinBox.setValueAttribute(pin);
            //Locate submit login button from xml
            HtmlSubmitInput submitButton = page.getFirstByXPath("/html/body/div[4]/form/p/input[1]");
            //Click submit button
            submitButton.click();
            //change page to add or drop classes page
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout");
            //Locate the drop down box for semester selection
            HtmlSelect semesterDropDown = page.getFirstByXPath("//*[@id=\"term_id\"]");
            //Select proper semester
            HtmlOption semesterOption = semesterDropDown.getOptionByValue(semester);
            semesterOption.setSelected(true);
            //Locate submit semester button
            HtmlSubmitInput submitSemester = page.getFirstByXPath("/html/body/div[4]/form/input");
            //Click the submit semester button
            submitSemester.click();
            //Change pages to actual add or drop for classes
            page = webClient.getPage("https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout");
            ArrayList<HtmlInput> crnBoxes = new ArrayList();
            //Load all 10 crn boxes into an arrayList for later use
            for (int i = 1; i < 10; i++){
                crnBoxes.add((HtmlInput) page.getFirstByXPath("//*[@id=\"crn_id"+i+"\"]"));
            }
            //Add any CRNs we have into the given boxes
            while (!crns.isEmpty()){
                int i = 1;
                crnBoxes.get(i).setValueAttribute(crns.pop());
                i++;
            }
            //Locate the submit button for CRNs
            HtmlSubmitInput submitCrns = page.getFirstByXPath("/html/body/div[4]/form/input[19]");
            //Click the submit CRNs buttons
            page = submitCrns.click();
            //The following two lines are used to print out the html to the console for testing
            WebResponse response = page.getWebResponse();
            String content = response.getContentAsString();
            System.out.println(content);
            }catch (IOException e){
                System.out.println("Error: Unable to load wingsexpress webpages.");
            }
    }
}
