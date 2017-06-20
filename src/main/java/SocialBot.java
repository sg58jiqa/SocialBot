import com.mongodb.*;
import org.jinstagram.exceptions.InstagramException;
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
    private DBCollection coll;
    private Random randomize;

    public SocialBot(String network) {

        try{
            // Connect to mongodb server
            mongoClient = new MongoClient("localhost", 27017);

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");
            coll = db.getCollection(network + "Follower");
            //System.out.println("Load table " + network + "Follower successfully\n");

            randomize = new Random();

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void checkFollower() throws InstagramException {

        //Set all followMe values to 0.
        for(DBObject doc : coll.find()) {
            doc.put("followMe", 0);
            coll.save(doc);
        }

        //Insert the follower of the given user.
        for (String userId : getFollowerIds()) {
            BasicDBObject doc = new BasicDBObject("_id", userId).append("followMe", 1);
            coll.save(doc);
        }
    }
    //Prints all users not following you back anymore.
    public void showNotFollowingAnymore() throws InstagramException {
        System.out.println("\nDo not follow you anymore:");
        int numNotFollowAnymore = 0;
        for(DBObject doc : coll.find(new BasicDBObject("followMe", 0))) {
            System.out.println("Username:" + getScreenName(doc.get("_id").toString()));
            ++numNotFollowAnymore;
        }
        System.out.println("Total Number:" + numNotFollowAnymore);
    }
    //Extract the followers of the given user and save them in the DB.
    private void extractFollower(String userId, int number, String collectionName) {
        DBCollection collFollower = db.getCollection(collectionName);
        for (String id : getFollowerIds(userId)) {
            if(notFollowing(id) && number > 0) {
                DBObject doc = new BasicDBObject("_id", id).append("follow", 0);
                collFollower.save(doc);
                --number;
            }
            if(number == 0) {
                return;
            }
            //waitSomeTime(20);
        }
    }
    //Load number of followers of the given user and like or comment one post of each user.
    public void FollowLikeComment(String screenName, int number, boolean doLike, boolean doComment) {

        String rootUserId = getUserId(screenName);
        String collectionName = rootUserId + "_Follower" + "_follow" + "_" + doLike + "_" + doComment;

        //DBCollection Follower = db.getCollection(collectionName);
        //Follower.drop();

        extractFollower(rootUserId, number, collectionName );

        DBCollection collFollower = db.getCollection(collectionName);
        DBCursor curs = collFollower.find();

        System.out.println(collFollower.count()
                + " follower from " + getScreenName(rootUserId) + " userId:" + rootUserId + "\n");

        while (curs.hasNext()){
            //Show how many users left
            System.out.println(collFollower.find(new BasicDBObject("follow", 0)).size() + " user left!");

            //Follow
            String userId = (String) curs.next().get("_id");
            if(notFollowing(userId)) {
                followUser(userId);
                DBObject doc = new BasicDBObject("_id", userId).append("follow", 1);
                collFollower.save(doc);
                System.out.println(("Follow user:" + getScreenName(userId) + " userId:" + userId));
            } else {
                DBObject doc = new BasicDBObject("_id", userId).append("follow", 0);
                collFollower.save(doc);
            }
            //Like or comment
            if (doLike || doComment) {
                String postId = getRandomPostId(userId);

                if (doLike) {
                    likePost(postId);
                    System.out.println("Like post:" + postId);
                }

                if (doComment) {
                    doComment(postId, getScreenName(userId),getRandomComment());
                    System.out.println("Comment post:" + postId);
                }
            }
            //Wait
            waitSomeTime(60);
        }
    }
    public abstract int getFollowerCount();
    public abstract int getFollowingCount();
    public abstract int getCommentCount();
    public abstract int getLikesCount();
    public abstract int  getPostCount();
    public abstract String getScreenName(String userId);
    public abstract String getRandomPostId(String userId);
    public abstract List<String> getFollowerIds();
    public abstract List<String> getFollowerIds(String userId);
    public abstract void followUser(String userId);
    public abstract void likePost(String postId);
    public abstract void doComment(String postId, String userName, String comment);
    public abstract String getUserId(String screenName);
    public abstract boolean notFollowing(String userId);

    private String getRandomFollowerId() {
        List<String> userIds = getFollowerIds();
        Random randomize = new Random();
        return userIds.get(randomize.nextInt(userIds.size()));
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
}
