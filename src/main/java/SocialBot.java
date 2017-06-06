
/**
 * Created by Simon on 03.06.2017.
 * simon.ganz@gmx.de
 */
public class SocialBot {

    public static void main(String argv[]) throws Exception {

        InstaBot instaBot = new InstaBot();

        System.out.println("#Follower:" + instaBot.getFollowerCount());
        System.out.println("#Following:" + instaBot.getFollowingCount());
        System.out.println("#Comments:" + instaBot.getCommentCount());
        System.out.println("#Likes:" + instaBot.getLikesCount());
        System.out.println("#Posts:" + instaBot.getPostCount());

    }

}
