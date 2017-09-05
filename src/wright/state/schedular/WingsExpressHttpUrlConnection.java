package wright.state.schedular;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WingsExpressHttpUrlConnection {

    private List<String> cookies;
    private HttpsURLConnection connection;
    private final String USER_AGENT = "Mozilla/5.0";
    private static Queue<String> data = new LinkedList();
    private boolean termSelected = false;
    private boolean test = false;

    public static void main(String[] args) throws Exception {
        String loginUrl = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_ValLogin";
        String wings = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu";
        String scheduleUrl = "https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout";
        WingsExpressHttpUrlConnection http = new WingsExpressHttpUrlConnection();
        CookieHandler.setDefault(new CookieManager());

        data.offer(""); //UID
        data.offer(""); //password
        data.offer("201780");
        data.offer("11121");

        //Get Login Data
        String loginPage = http.getPageContent(loginUrl);
        String postParams = http.getFormParams(loginPage, data);
        //login
        http.sendPost(loginUrl, postParams, "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_WWWLogin", "9", "en-US,en;q=0.8","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        
        //Get Term Params
        String schedulePage = http.getPageContent(scheduleUrl);
        String termParams = http.getFormParams(schedulePage, data);
        //Enter correct Term (i.e. 201780 (Fall Term of 2017))
        http.sendPost(scheduleUrl, termParams, scheduleUrl, "14", "en-US,en;q=0.5","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        
        //Get CRN Params
        schedulePage = http.getPageContent(scheduleUrl);
        termParams = http.getFormParams(schedulePage, data);
        //Enter CRN Params and send a post request to apply them
        http.sendPost("https://wingsexpress.wright.edu/pls/PROD/bwckcoms.P_Regs", termParams, scheduleUrl, "2650", "en-US,en;q=0.5","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    }

    private void sendPost(String url, String postParams, String referer, String contentLength, String language, String accept) throws Exception {
        URL obj = new URL(url);
        connection = (HttpsURLConnection) obj.openConnection();
        
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "wingsexpress.wright.edu");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", accept);
        connection.setRequestProperty("Accept-Language", language);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", contentLength);
                connection.setRequestProperty("Referer", referer);
        this.cookies.forEach((cookie) -> {
            connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        });
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");


        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Send post request
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(postParams);
        out.flush();
        out.close();

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post paramters: " + postParams);
        System.out.println("Response Code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }

    private String getPageContent(String url) throws Exception {
        URL obj = new URL(url);
        connection = (HttpsURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");
        connection.setUseCaches(false);

        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            this.cookies.forEach((cookie) -> {
                connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            });
        }
        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to: " + url);
        System.out.println("Response Code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        setCookies(connection.getHeaderFields().get("Set-Cookie"));
        return response.toString();
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public String getFormParams(String html, Queue<String> data) throws Exception {
        System.out.println("Extracting form's data...");
        Document doc = Jsoup.parse(html);

        //Wright State Form ID
        Elements inputElements = doc.getAllElements();
        List<String> paramList = new ArrayList<>();
        int i = 1;
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value;

            if (key.equals("sid")) {
                value = data.poll();
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
            if (key.equals("PIN")) {
                value = data.poll();
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
            if ((key.equals("term_in")) && !termSelected) {
                value = data.poll();
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                termSelected = true;
            }
            key = inputElement.attr("id");
            if (key.equals("crn_id" + i)) {
                if (!data.isEmpty()) {
                    i++;
                    value = data.poll();
                    paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                }
            }
        }
        StringBuilder result = new StringBuilder();
        paramList.forEach((param) -> {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&").append(param);
            }
        });
        return result.toString();
    }
}
