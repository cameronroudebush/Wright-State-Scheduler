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
    
    public static void main(String[] args) throws Exception{
        String url = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_WWWLogin";
        String wingsExpress = "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu&msg=WELCOME+Welcome,+Cameron+A.+Roudebush,+to+WINGS+Express!Apr+05,+201702%3A07+pm";
        WingsExpressHttpUrlConnection http = new WingsExpressHttpUrlConnection();
        //enable cookies
        CookieHandler.setDefault(new CookieManager());
        
        //Send GET request for forms data
        String page = http.getPageContent(url);
        String postParams = http.getFormParams(page, "blah", "blah");
        
        //2 Construct above content and send post rquest
        //authentication
        http.sendPost(url, postParams);
        
        //sucess
        System.out.println(http.getPageContent(url));
    }
    
    private void sendPost(String url, String postParams) throws Exception {
        URL obj = new URL(url);
        connection = (HttpsURLConnection) obj.openConnection();
        
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "wingsexpress.wright.edu");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Referer", "https://wingsexpress.wright.edu/pls/PROD/twbkwbis.P_ValLogin");
        for (String cookie : this.cookies){
            connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", "31");
        
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
        StringBuffer response = new StringBuffer();;
        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        System.out.println("Response: " + response.toString());
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
        StringBuffer response = new StringBuffer();
        
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
    
    public String getFormParams(String html, String username, String password) throws Exception{
        System.out.println("Extracting form's data...");
        Document doc =  Jsoup.parse(html);
                
        //Wright State Form ID
        Elements inputElements = doc.getElementsByTag("input");
        List<String> paramList = new ArrayList<>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");
            
            if (key.equals("sid")){
                value = username;
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }else if (key.equals("PIN")){
                value = password;
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
        return result.toString();
    }
}
