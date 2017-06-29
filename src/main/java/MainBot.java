/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class MainBot {

    public static void main(String argv[]) throws Exception {

        //System.out.println("INSTAGRAM:");
        //InstBot instBot = new InstBot();
        //instBot.showMetrics();

        System.out.println("TWITTER:");
        TwitterBot twitterBot = new TwitterBot();
        //twitterBot.showMetrics();

        //Default params
        double[] params = {1,7,0.1,1.5};

        //twitterBot.FollowLikeComment("alo_oficial", 1, true, false, params);
        //twitterBot.FollowLikeComment("MercedesAMGF1",10, true, false, params);
        //twitterBot.FollowLikeComment("MassaFelipe19",1,true ,true, params);
        //twitterBot.unfollowUsers(1,3);
    }
}
