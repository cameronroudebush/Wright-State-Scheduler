package wright.state.schedular;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WingsExpressHttpUrlConnection {
    private List<String> cookies;
    private HttpsURLConnection connection;
    private final String USER_AGENT = "Mozilla/5.0";
//    private static String responseUrl;
    
    public static void main(String[] args) throws Exception{
        String loginUrl = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_ValLogin";
        String wings = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu";
        String scheduleUrl = "https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout";
        WingsExpressHttpUrlConnection http = new WingsExpressHttpUrlConnection();
        //enable cookies
        CookieHandler.setDefault(new CookieManager());
        
        //Send GET request for forms data
        String loginPage = http.getPageContent(loginUrl);
        String postParams = http.getFormParams(loginPage, "", "");
        
        //2 Construct above content and send post rquest
        //authentication
        String responseUrl = http.sendPost(loginUrl, postParams, "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_WWWLogin", "9", "en-US,en;q=0.8");
        if (responseUrl.contains("content")){
            System.out.println("Login information incorrect. Exiting...");
            System.exit(0);
        }
        System.out.println("Redirect URL: " + responseUrl);
        
        String schedulePage = http.getPageContent(scheduleUrl);
        String termParams = http.getFormParams(schedulePage, "201780", "");
        
        responseUrl = http.sendPost(scheduleUrl, termParams, "https://wingsexpress.wright.edu/pls/PROD/WsuStuRegistration.P_adddropbackout", "14", "en-US,en;q=0.5");
        
        
        schedulePage = http.getPageContent(scheduleUrl);
        System.out.println(schedulePage);
    }
    
    private String sendPost(String url, String postParams, String referer, String contentLength, String language) throws Exception {
        URL obj = new URL(url);
        connection = (HttpsURLConnection) obj.openConnection();
        
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "wingsexpress.wright.edu");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        
        connection.setRequestProperty("Accept-Language", language);
        connection.setRequestProperty("Referer", referer);
        
        
        this.cookies.forEach((cookie) -> {
            connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        });
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", contentLength);
        
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
        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        if (url.equals("https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_ValLogin")){
        return "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu&" + response.substring(108,response.length()-16);
        }
        else return "";
    }
    
    
    private String getPageContent(String url) throws Exception{
        URL obj = new URL(url);
        connection = (HttpsURLConnection) obj.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null){
            this.cookies.forEach((cookie) -> {
                connection.addRequestProperty("Cookie", cookie.split(";",1)[0]);
            });
        }
        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to: " + url);
        System.out.println("Response Code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        setCookies(connection.getHeaderFields().get("Set-Cookie"));
        
        return response.toString();
    }
    
    public void setCookies(List<String> cookies){
        this.cookies = cookies;
    }
    public List<String> getCookies(){
        return cookies;
    }
    
    public String getFormParams(String html, String valueOne, String valueTwo) throws Exception{
        System.out.println("Extracting form's data...");
        Document doc = Jsoup.parse(html);
                
        //Wright State Form ID
        Elements inputElements = doc.getAllElements();
        List<String> paramList = new ArrayList<>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value;
            
            if (key.equals("sid")){
                value = valueOne;
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }if (key.equals("PIN")){
                value = valueTwo;
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }if (key.equals("term_in")){
                value = valueOne;
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
        }
        //Build paramters list
        StringBuilder result = new StringBuilder();
        paramList.forEach((param) -> {
            if (result.length() == 0){
                result.append(param);
            } else {
                result.append("&").append(param);
            }
        });
        System.out.println(paramList.toString());
        return result.toString();
    }
}
