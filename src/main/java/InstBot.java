import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class InstBot extends SocialBot {

    private String userId;
    private Instagram instagram;

    InstBot(String token, String secret, String userId) {
        super("Instagram");
        Token accessToken = new Token(token, secret);
        instagram = new Instagram(accessToken);
        this.userId = userId;
    }
    @Override
    public List<String> getFollowerIds() {
        List<String> follower = new ArrayList<String>();
        try {
            for(UserFeedData user : instagram.getUserFollowedByList(userId).getUserList()) {
                follower.add(user.getId());
            }
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return follower;
    }

    @Override
    public List<String> getFollowerIds(String userId) {
        return null;
    }

    @Override
    public boolean followUser(String userId) {
        return false;
    }

    @Override
    public boolean likePost(String postId) {
        return false;
    }

    @Override
    public boolean doComment(String postId, String userName, String comment) {
        return false;
    }

    @Override
    public String getUserId(String screenName) {
        return null;
    }

    @Override
    public boolean isNotMyFriend(String userId) {
        return false;
    }

    @Override
    public boolean isActive(String userId, int daysAgo) {
        return false;
    }

    @Override
    public boolean validFFRation(String userId, double[] ffRation) {
        return false;
    }

    @Override
    public void unfollowUsers(int number, int dayLimit) {}

    public void showMetrics() {
        System.out.println("#Follower:" + getFollowerCount());
        System.out.println("#Following:" + getFollowingCount());
        System.out.println("#Posts:" + getPostCount());
        System.out.println("#Likes:" + getLikesCount());
        System.out.println("#Comments:" + getCommentCount());
    }

    @Override
    public String getScreenName(String user_id) {
        String userName = "";
        try {
            return instagram.getUserInfo(user_id).getData().getFullName();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return userName;
    }

    @Override
    public String getRandomPostId(String userId) {
        return null;
    }

    @Override
    public int getFollowerCount() {
        int followerCount = 0;
        try {
            UserFeed feed = instagram.getUserFollowList(userId);
            return feed.getUserList().size();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return followerCount;
    }

    @Override
    public int getFollowingCount() {
        int followingCount = 0;
        try {
            UserFeed feed = instagram.getUserFollowedByList(userId);
            return feed.getUserList().size();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return followingCount;
    }

    @Override
    public int getCommentCount() {
        int commentCount = 0;
        try {
            MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
            for (MediaFeedData media : mediaFeed.getData()) {
                commentCount += media.getComments().getCount();
            }
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return commentCount;
    }

    @Override
    public int getLikesCount() {
        int count = 0;
        try {
            MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
            for (MediaFeedData media : mediaFeed.getData()) {
                count += media.getLikes().getCount();
            }
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int getPostCount() {
        int postCount = 0;
        try {
            MediaFeed mediaFeed = instagram.getRecentMediaFeed(userId);
            return mediaFeed.getData().size();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return postCount;
    }

    @Override
    public int getPostCount(String userId) {
        return 0;
    }
}
