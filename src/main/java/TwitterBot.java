import com.google.common.primitives.Longs;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.exit;
import static org.apache.commons.lang3.math.IEEE754rUtils.min;

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
                .setOAuthConsumerKey("1TkNHb6dTTUvQE4uVGaYFchhb")
                .setOAuthConsumerSecret("DXf1dHPgmFge8G4eXMP7h1c24JqSdFYR8RZIYF4IGKeM8vTsxt")
                .setOAuthAccessToken("861603997886689280-wbwCOCjXgcwo148lTvKwXGjS792PiEB")
                .setOAuthAccessTokenSecret("cRfhdO8t5mth6BGOjcPJCdASjvCcXq6QuE6bOZUn8u53t");

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        try {
            user = twitter.showUser(twitter.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getScreenName(String userId) {
        String userName = "";
        try {
            User user = twitter.showUser(Long.valueOf(userId));
            userName = user.getScreenName();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return userName;
    }

    @Override
    public String getRandomPostId(String userId) {
        Status status = null;
        Random random = new Random();
        try {
            List<Status> tweets = twitter.getUserTimeline(Long.parseLong(userId));
            if (tweets.size() == 1) {
                status = tweets.get(0);
            } else if (tweets.size() > 0) {
                int tweetAtPosition = random.nextInt(tweets.size() - 1);
                System.out.println("Tweet " + tweetAtPosition);
                status = tweets.get(tweetAtPosition);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (status == null) {
            System.out.println("No tweets found!");
            exit(1);
        }
        return Long.toString(status.getId());
    }

    @Override
    public List<String> getFollowerIds() {
        List<String> followerIds = new ArrayList<String>();
        try {
            long[] ids = twitter.getFollowersIDs(user.getId(), -1).getIDs();
            for (long id : ids) {
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
            long[] ids = twitter.getFollowersIDs(Long.parseLong(userId), -1, 1000).getIDs();
            System.out.println("Extract follower ...");
            for (long id : ids) {
                followerIds.add(Long.toString(id));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return followerIds;
    }

    @Override
    public boolean followUser(String userId) {
        try {
            twitter.createFriendship(Long.parseLong(userId));
            DBCollection friendshipDate = super.db.getCollection("FriendshipDate");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            friendshipDate.save(new BasicDBObject("_id", Long.parseLong(userId)).append("date", formatter.format(cal.getTime())));
        } catch (TwitterException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean likePost(String tweetId) {
        try {
            twitter.createFavorite(Long.parseLong(tweetId));
        } catch (TwitterException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean doComment(String tweetId, String screenName, String comment) {
        StatusUpdate statusUpdate = new StatusUpdate(comment + " @" + screenName);
        statusUpdate.setInReplyToStatusId(Long.parseLong(tweetId));
        try {
            twitter.updateStatus(statusUpdate);
        } catch (TwitterException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
    public int getPostCount(String userId) {
        try {
            List<Status> tweets = twitter.getUserTimeline(Long.parseLong(userId));
            if (tweets.size() > 0) {
                System.out.println("PostCount:" + tweets.size());
                return tweets.size();
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("PostCount to low!");
        return 0;
    }

    @Override
    public int getLikesCount() {
        return user.getFavouritesCount();
    }

    @Override
    public String getUserId(String screenName) {
        String userId = "";
        try {
            User user = twitter.showUser(screenName);
            userId = String.valueOf(user.getId());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return userId;
    }

    @Override
    public boolean isNotMyFriend(String userId) {
        boolean isFriend = false;
        try {
            Relationship relationship = twitter.showFriendship(Long.parseLong(userId), user.getId());
            isFriend = relationship.isSourceFollowedByTarget();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return !isFriend;
    }

    @Override
    public boolean isActive(String userId, int daysAgo) {
        try {
            User user = twitter.showUser(Long.parseLong(userId));
            Date tweetDate = user.getStatus().getCreatedAt();
            if (tweetDate.after(getMaxDate(daysAgo))) {
                System.out.println("User is active!");
                return true;
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Not active!");
        return false;
    }

    @Override
    public boolean validFFRation(String userId, double[] ffRation) {
        try {
            int numFollower = twitter.getFollowersIDs(Long.parseLong(userId)).getIDs().length;
            int numFriends = twitter.getFriendsIDs(Long.parseLong(userId)).getIDs().length;

            if (numFriends > 0) {
                double currentFFRation = numFollower / numFriends;
                if (ffRation[0] < currentFFRation && currentFFRation < ffRation[1]) {
                    System.out.println(currentFFRation);
                    System.out.println("User have a valid FF-Ration!");
                    return true;
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Invalid FF-Ration");
        return false;
    }

    @Override
    public void unfollowUsers(int number, int dayLimit) {
        List<Long> unfollowIds = getUnfollowIds();
        for (int i = 0; i < min(number, unfollowIds.size()-1); ++i) {
            try {
                twitter.destroyFriendship(unfollowIds.get(i));
                System.out.println("Unfollow user " + ":" + getScreenName(Long.toString(unfollowIds.get(i))) + "(" + i + ")");
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            waitSomeTime(600);
        }
    }

    public void showMetrics() {
        System.out.println("#Follower:" + getFollowerCount());
        System.out.println("#Following:" + getFollowingCount());
        System.out.println("#Tweets:" + getPostCount());
        System.out.println("#Likes:" + getLikesCount());
        System.out.println("#Comments:" + getCommentCount());
        System.out.println("#Mentions:" + getMentionsCount());
        System.out.println("#Retweets:" + getRetweetCount());
    }

    private int getRetweetCount() {
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
    private int getMentionsCount() {
        int mentionCount = 0;
        try {
            mentionCount = twitter.getMentionsTimeline().size();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return mentionCount;
    }
    private List<Long> getUnfollowIds() {
        List<Long> unfollowIds = new ArrayList<Long>();
        try {
            List<Long> followersIDs = Longs.asList(twitter.getFollowersIDs(user.getId(), -1).getIDs());
            List<Long> friendsIDs = Longs.asList(twitter.getFriendsIDs(user.getId(), -1).getIDs());
            for (long id : friendsIDs) {
                if (!followersIDs.contains(id)) {
                    unfollowIds.add(id);
                }
            }
        } catch(TwitterException e){
            e.printStackTrace();
        }
        System.out.println(unfollowIds.size() + " users to unfollow!");
        return  unfollowIds;
    }
    private List<Long> getUnfollowIds(int days) {
        List<Long> unfollowIds = new ArrayList<Long>();
        try {
            List<Long> followersIDs = Longs.asList(twitter.getFollowersIDs(user.getId(), -1).getIDs());
            List<Long> friendsIDs = Longs.asList(twitter.getFriendsIDs(user.getId(), -1).getIDs());
            DBCollection friendshipDate = super.db.getCollection("FriendshipDate");
            if (friendshipDate.find().count() > 0) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                for (long id : friendsIDs) {
                    DBObject obj = friendshipDate.findOne(new BasicDBObject("_id", id));
                    if (obj != null) {
                        try {
                            Date date = formatter.parse(obj.get("date").toString());
                            if (getMaxDate(days).after(date) && !followersIDs.contains(id)) {
                                unfollowIds.add(id);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            } catch(TwitterException e){
                e.printStackTrace();
            }
        System.out.println(unfollowIds.size() + " users to unfollow!");
        return  unfollowIds;
    }
    private Date getMaxDate(int dayLimit) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayLimit);
        return cal.getTime();
    }
}

