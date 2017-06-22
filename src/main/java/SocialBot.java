import com.mongodb.*;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public abstract class SocialBot {

    private MongoClient mongoClient;
    private DB db;
    private DBCollection collMyFollower;
    private Random randomize;

    public SocialBot(String network) {

        try{
            // Connect to mongodb server
            mongoClient = new MongoClient("localhost", 27017);

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");
            collMyFollower = db.getCollection(network + "Follower");
            //System.out.println("Load table " + network + "Follower successfully\n");

            randomize = new Random();

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void checkMyFollower() throws InstagramException {

        //Set all followMe values to 0.
        for(DBObject doc : collMyFollower.find()) {
            doc.put("followMe", 0);
            collMyFollower.save(doc);
        }

        //Insert the follower of the given user.
        for (String userId : getFollowerIds()) {
            BasicDBObject doc = new BasicDBObject("_id", userId).append("followMe", 1);
            collMyFollower.save(doc);
        }
    }
    //Prints all users not following you back anymore.
    public void showNotFollow() {
        System.out.println("\nDo not follow you anymore:");
        int numNotFollow = 0;
        for(DBObject doc : collMyFollower.find(new BasicDBObject("followMe", 0))) {
            System.out.println("Username:" + getScreenName(doc.get("_id").toString()));
            ++numNotFollow;
        }
        System.out.println("Total Number:" + numNotFollow);
    }
    //Load number of followers of the given user and follow or like or comment a post of each user.
    public void FollowLikeComment(String screenName, int number, boolean doLike, boolean doComment) {

        String rootUserId = getUserId(screenName);
        String collectionName = rootUserId + "_Follower" + "_follow" + "_" + doLike + "_" + doComment;

        extractFollower(rootUserId, number, collectionName );

        DBCollection collFollower = db.getCollection(collectionName);
        //collFollower.drop();
        DBCursor curs = collFollower.find(new BasicDBObject("follow", 0));

        //Remove users
//        while(curs.hasNext() && collFollower.count() > 100) {
//            collFollower.remove(curs.next());
//            System.out.println(collFollower.count());
//        }

        System.out.println(collFollower.count()
                + " follower from " + getScreenName(rootUserId) + " userId:" + rootUserId + "\n");

        while (curs.hasNext()){
            //Show how many users left
            System.out.println(collFollower.find(new BasicDBObject("follow", 0)).size() + " user left!");

            String userId = (String) curs.next().get("_id");

            //Follow
            if(!isMyFriend(userId)) {
                if(followUser(userId)) {
                    DBObject doc = new BasicDBObject("_id", userId).append("follow", 1);
                    collFollower.save(doc);
                    System.out.println(("\nFollow user:" + getScreenName(userId) + " userId:" + userId));
                } else {
                    DBObject doc = new BasicDBObject("_id", userId).append("follow", 0);
                    collFollower.save(doc);
                }
            }
            //Like or comment
            if (doLike || doComment) {
                String postId = getRandomPostId(userId);

                if (doLike) {
                    if(!likePost(postId)) {
                        DBObject doc = new BasicDBObject("_id", userId).append("follow", 0);
                        collFollower.save(doc);
                    }
                    System.out.println("Like");
                }

                if (doComment) {
                    if(!doComment(postId, getScreenName(userId),getRandomComment())) {
                        DBObject doc = new BasicDBObject("_id", userId).append("follow", 0);
                        collFollower.save(doc);
                    }
                    System.out.println("Comment");
                }
                System.out.println("postId:" + postId);
            }
            //Wait
            waitSomeTime(120);
        }
    }
    //Checks for every user in the given collection if he is a follower
    public void showNewFollower(String collectionName) {
        List<String> followerIDs = getFollowerIds();
        List<String> newFollower = new ArrayList<String>();

        DBCollection newFollowing = db.getCollection(collectionName);
        DBCursor curs = newFollowing.find(new BasicDBObject("follow", 1));
        System.out.println(">Check " + newFollowing.find(new BasicDBObject("follow", 1)).count() + " user<");
        while (curs.hasNext()) {
            if(followerIDs.contains(curs.next().get("_id"))) {
                System.out.println(getScreenName(curs.next().get("_id").toString()));
                newFollower.add(curs.next().get("_id").toString());
            }
        }
        System.out.println("Follower " + newFollower.size() + "\n");
    }
    //Extract the followers of the given user and save them in the DB.
    private void extractFollower(String userId, int number, String collectionName) {
        DBCollection collFollower = db.getCollection(collectionName);
        for (String id : getFollowerIds(userId)) {
            if(number > 0 && !isMyFriend(id) && getPostNumber(id) > 0) {
                System.out.println(number);
                DBObject doc = new BasicDBObject("_id", id).append("follow", 0);
                collFollower.save(doc);
                --number;
            }
            if(number == 0) {
                return;
            }
            waitSomeTime(10);
        }
    }
    private void createCommentTemplates() {
        DBCollection commentTemplates = db.getCollection("CommentTemplates");
        String[] comments = {"Nice!", "I really like it!!!", "Wow!", "Awesome", "Really nice!", "Whaaat?",
                "That ca not be true :P", "Really?", "Absolutely amazing", "Well done!"};
        for (String comment : comments) {
            DBObject doc = new BasicDBObject("_id", comment);
            commentTemplates.save(doc);
        }
    }
    private String getRandomComment() {
        DBCollection commentTemplates = db.getCollection("CommentTemplates");
        long numComments = commentTemplates.count();
        Random random = new Random();
        DBCursor randomElement = commentTemplates.find().limit(1).skip(random.nextInt((int) numComments));
        String comment = randomElement.next().get("_id").toString();
        System.out.println("Random comment:" + comment);
        return comment;
    }
    private void waitSomeTime(int maxTimeToWait){
        try {
            TimeUnit.SECONDS.sleep((long) randomize.nextInt(maxTimeToWait));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract int getFollowerCount();
    public abstract int getFollowingCount();
    public abstract int getCommentCount();
    public abstract int getLikesCount();
    public abstract int  getPostCount();
    public abstract int getPostNumber(String userId);
    public abstract String getScreenName(String userId);
    public abstract String getRandomPostId(String userId);
    public abstract String getUserId(String screenName);
    public abstract List<String> getFollowerIds();
    public abstract List<String> getFollowerIds(String userId);
    public abstract List<String> getNotFollowerIds();
    public abstract boolean isMyFriend(String userId);
    public abstract boolean followUser(String userId);
    public abstract boolean likePost(String postId);
    public abstract boolean doComment(String postId, String userName, String comment);
}
