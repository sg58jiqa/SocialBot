/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class MainBot {

    public static void main(String argv[]) throws Exception {

//        System.out.println("INSTAGRAM:");
//        InstBot instBot = new InstBot();
//
//        System.out.println("#Follower:" + instBot.getFollowerCount());
//        System.out.println("#Following:" + instBot.getFollowingCount());
//        System.out.println("#Posts:" + instBot.getPostCount());
//        System.out.println("#Likes:" + instBot.getLikesCount());
//        System.out.println("#Comments:" + instBot.getCommentCount() + "\n\n");
//
//        instBot.checkFollower();
//        instBot.showNotFollowingAnymore();

        System.out.println("TWITTER:");
        TwitterBot twitterBot = new TwitterBot();

//        System.out.println("#Follower:" + twitterBot.getFollowerCount());
//        System.out.println("#Following:" + twitterBot.getFollowingCount());
//        System.out.println("#Tweets:" + twitterBot.getPostCount());
//        System.out.println("#Likes:" + twitterBot.getLikesCount());
//        System.out.println("#Comments:" + twitterBot.getCommentCount());
//        System.out.println("#Mentions:" + twitterBot.getMentionsCount());
//        System.out.println("#Retweets:" + twitterBot.getRetweetCount());

        //twitterBot.checkMyFollower();
        //twitterBot.showNotFollow();
        //twitterBot.FollowLikeComment("alo_oficial", 20, false, false);
        twitterBot.showNewFollower("507672047_Follower_follow_false_false");
        //twitterBot.FollowLikeComment("MercedesAMGF1",13, true, false);
        twitterBot.showNewFollower("26235265_Follower_follow_true_false");
        //twitterBot.FollowLikeComment(100,true ,true);
    }
}
