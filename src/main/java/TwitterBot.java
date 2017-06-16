import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class TwitterBot extends SocialBot {

    private Twitter twitter;
    private User user;

    TwitterBot() {
        super("Twitter");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("FXdDLoKhQWnWsGmATUfOLmlZb")
                .setOAuthConsumerSecret("KqVImljOLJ2kI7f3lqKuL50cpIJPRs4uoE0lRDPiAr8wkDpn5m")
                .setOAuthAccessToken("861603997886689280-kB8qOsGs4kpyhAe78U5CiM2jkf4LJaT")
                .setOAuthAccessTokenSecret("aGWjf5MdYts3LxihcbR8x9yWeDZBP3e6SPdlqtaC3y0xr");

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        try {
            user = twitter.showUser(twitter.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsername(String userId) {
        String userName = "";
        try {
            User user = twitter.showUser(Long.valueOf(userId));
            userName = user.getName();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return userName;
    }
    @Override
    public String getPostId(String userId) {
        Status status = null;
        try {
            User user = twitter.showUser(Long.parseLong(userId));
            status = user.getStatus();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (status.equals(null)) {
            return null;
        }
        return Long.toString(status.getId());
    }

    @Override
    public List<String> getFollowerIds() {
        List<String> followerIds = new ArrayList<String>();
        try {
            for (long id : twitter.getFollowersIDs(-1).getIDs()) {
                followerIds.add(Long.toString(id));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return followerIds;
    }
    @Override
    public List<String> getFollowerIds(String userId) {
        List<String> followerIds = new ArrayList<String>();
        try {
            for (long id : twitter.getFollowersIDs(Long.parseLong(userId),-1, 100).getIDs()) {
                followerIds.add(Long.toString(id));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return followerIds;
    }
    @Override
    public void followUser(String userId) {
        try {
            twitter.createFriendship(Long.parseLong(userId));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void likePost(String tweetId) {
        if (tweetId.equals(null)) {
            return;
        }
        try {
            twitter.createFavorite(Long.parseLong(tweetId));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void doComment(String tweetId, String comment) {
            StatusUpdate statusUpdate = new StatusUpdate(comment);
        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getFollowerCount() {
        return user.getFollowersCount();
    }

    @Override
    public int getFollowingCount() {
        return user.getFriendsCount();
    }
    @Override
    public int getCommentCount() {
        return 0;
    }

    @Override
    public int getPostCount() {
        return user.getStatusesCount();
    }

    @Override
    public int getLikesCount() {
        return user.getFavouritesCount();
    }

    public int getRetweetCount() {
        int retweetCount = 0;
        try {
            Paging paging = new Paging(1, 100);
            for (Status status : twitter.getUserTimeline(user.getId(), paging)) {
                retweetCount += status.getRetweetCount();
            }
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
        return retweetCount;
    }
    public int getMentionsCount() {
        int mentionCount = 0;
        try {
            mentionCount = twitter.getMentionsTimeline().size();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return mentionCount;
    }
}
