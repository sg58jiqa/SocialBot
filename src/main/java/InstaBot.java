import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.exceptions.InstagramException;

/**
 * Created by bar on 06.06.17.
 */
public class InstaBot {

    private Token accessToken = new Token("5434445380.393ca10.113d56f3f8814e069e5e4f49b9fa79d0", "393ca102ec3048d1ad12e9aa22c46ae2");
    private String userId = "5434445380";
    private Instagram instagram;

    public InstaBot() {
        instagram = new Instagram(accessToken);
    }

    //Returns the number of Follower.
    public int getFollowerCount() throws InstagramException {
        UserFeed feed = instagram.getUserFollowList(userId);
        return feed.getUserList().size();
    }
    //Returns the number of Following.
    public int getFollowingCount() throws InstagramException {
        UserFeed feed = instagram.getUserFollowedByList(userId);
        return feed.getUserList().size();
    }
    //Returns the number of comments of the given user.
    public int getCommentCount() throws InstagramException {
        int count = 0;

        MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
        for (MediaFeedData media : mediaFeed.getData()) {
            count += media.getComments().getCount();
        }
        return count;
    }
    //Returns the number of comments of the given user.
    public int getLikesCount() throws InstagramException {
        int count = 0;

        MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
        for (MediaFeedData media : mediaFeed.getData()) {
            count += media.getLikes().getCount();
        }
        return count;
    }
    //Returns the number of post of the given user.
    public int getPostCount() throws InstagramException {
        MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
        return mediaFeed.getData().size();
    }
}
