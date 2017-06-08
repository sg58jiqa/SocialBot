
/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class MainBot {

    public static void main(String argv[]) throws Exception {

        InstBot instBot = new InstBot();

        System.out.println("#Follower:" + instBot.getFollowerCount());
        System.out.println("#Following:" + instBot.getFollowingCount());
//        System.out.println("#Comments:" + instBot.getCommentCount());
//        System.out.println("#Likes:" + instBot.getLikesCount());
        System.out.println("#Posts:" + instBot.getPostCount());

        instBot.checkFollower();
        instBot.showNotFollowingAnymore();
    }

}
