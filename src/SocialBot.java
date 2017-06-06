import com.google.gson.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Simon on 03.06.2017.
 * simon.ganz@gmx.de
 */
public class SocialBot {

    public static void main(String argv[]) throws Exception {
        //getAccessToken();
        String accessToken = "5434445380.393ca10.113d56f3f8814e069e5e4f49b9fa79d0";

        //Print FollowerCount
        String followerUrl = "https://api.instagram.com/v1/users/self/follows?access_token=";
        System.out.printf("#Follower:" + getCount(parseURL(accessToken, followerUrl)));

        //Print FollowingCount
        String followingUrl = "https://api.instagram.com/v1/users/self/followed-by?access_token=";
        System.out.println("#Following:" + getCount(parseURL(accessToken, followingUrl)));

    }
    //Access the data field of the given JsonObject and returns the count
    private static int getCount(JsonObject jsonObject) throws IOException {

        JsonArray data = jsonObject.get("data").getAsJsonArray();
        return data.size();

    }
    //Parse the given URL and returns a JsonObject
    private static JsonObject parseURL(String accessToken, String url) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) (new URL(url + accessToken)).openConnection();
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url + accessToken);
        System.out.println("Response Code : " + responseCode);

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader(conn.getInputStream()));

        return root.getAsJsonObject();
    }
    private static void getAccessToken() throws IOException {
        String client_id = "393ca102ec3048d1ad12e9aa22c46ae2";
        String redirect_uri = "http://localhost:3000";
        //Print URL to get the access token
        URL url = new URL("https://api.instagram.com/oauth/authorize/?client_id=" + client_id
                + "&redirect_uri=" + redirect_uri
                + "&response_type=token"
                + "&scope=likes+comments+follower_list+relationships");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        System.out.println(conn.getURL());
    }
}
