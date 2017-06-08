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

    private String userId = "5434445380";
    private Instagram instagram;

    InstBot() {
        super("Instagram");
        Token accessToken = new Token("5434445380.393ca10.113d56f3f8814e069e5e4f49b9fa79d0", "393ca102ec3048d1ad12e9aa22c46ae2");
        instagram = new Instagram(accessToken);
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
    public String getUsername(String user_id) {
        String userName = "";
        try {
            return instagram.getUserInfo(user_id).getData().getFullName();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return userName;
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
}
