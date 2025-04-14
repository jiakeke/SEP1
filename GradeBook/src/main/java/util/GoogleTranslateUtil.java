package util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleTranslateUtil {
    private  static final String API_KEY;
    private static final Logger logger = LoggerFactory.getLogger(GoogleTranslateUtil.class);

    static {
        String key="";
        try(InputStream input=GoogleTranslateUtil.class.
                getClassLoader().getResourceAsStream("config.properties")){
            Properties prop=new Properties();
            if (input!=null){
                prop.load(input);
                key=prop.getProperty("google.api.key","");

            }else {
                logger.warn("config.properties not found in classpath");
            }
        }catch (IOException e){
            logger.error("Failed to load config.properties", e);
        }
        API_KEY=key;
    }

    public static String translate(String text,String targetLanguage) throws IOException {
        String urlstr="https://translation.googleapis.com/language/translate/v2?key="+API_KEY+
                "&q="+ URLEncoder.encode(text, StandardCharsets.UTF_8)+"&target="+targetLanguage;
        URL url=new URL(urlstr);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in =new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line=in.readLine())!=null){
            response.append(line);
        }
        in.close();

        String jsonResponse = response.toString();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray translations = jsonObject.getJSONObject("data").getJSONArray("translations");
        String translatedText = translations.getJSONObject(0).getString("translatedText");
        return translatedText;
    }

    public static void main(String[] args) {
        try {
            String text = "Welcome to the Grade Book!";
            System.out.println("中文：" + translate(text, "zh"));
            System.out.println("日文：" + translate(text, "ja"));
            System.out.println("英文：" + translate(text, "en"));
        } catch (IOException e) {
            logger.error("Error during translation", e);
        }
    }
}
