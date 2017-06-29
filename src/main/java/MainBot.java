import java.util.HashMap;

/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class MainBot {

    public static void main(String argv[]) throws Exception {

        GetPropertyValues propertyValues = new GetPropertyValues();
        HashMap<String, String> configValues = propertyValues.getPropValues();

//        System.out.println("INSTAGRAM:");
//        InstBot instBot = new InstBot(configValues.get("token"), configValues.get("secret"), configValues.get("userId"));
//        instBot.showMetrics();

        System.out.println("TWITTER:");
        TwitterBot twitterBot = new TwitterBot(configValues.get("consumerKey"), configValues.get("consumerSecret"),
                configValues.get("accessToken"), configValues.get("accessTokenSecret"));
        //twitterBot.showMetrics();

        double[] params = {Double.parseDouble(configValues.get("numPosts")),
                Double.parseDouble(configValues.get("lastPost")),
                Double.parseDouble(configValues.get("ffRationMin")),
                Double.parseDouble(configValues.get("ffRationMax"))};

        //twitterBot.FollowLikeComment("MercedesAMGF1",10, true, false, params);
        //twitterBot.FollowLikeComment("MassaFelipe19",1,true ,true, params);

        //twitterBot.FollowLikeComment(configValues.get("rootUser"), 1, true, false, params);
        twitterBot.unfollowUsers(Integer.parseInt(configValues.get("numToUnfollow")),
                Integer.parseInt(configValues.get("timeToUnfollow")));
    }
}
