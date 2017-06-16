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

    public SocialBot(String network) {

        try{
            // Connect to mongodb server
            mongoClient = new MongoClient("localhost", 27017);

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");
            coll = db.getCollection(network + "Follower");
            System.out.println("Load table " + network + "Follower successfully\n");

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
            System.out.println("Username:" + getUsername(doc.get("_id").toString()));
            ++numNotFollowAnymore;
        }
        System.out.println("Total Number:" + numNotFollowAnymore);
    }
    //Extract the followers of the given user and save them in the DB.
    public void extractFollower(String userId) {
        DBCollection collFollower = db.getCollection(userId + "_Follower");
        for (String id : getFollowerIds(userId)) {
            DBObject doc = new BasicDBObject("_id", id);
            collFollower.save(doc);
        }
    }
    //Choose follower randomly, extract 100 follower and follow n user.
    //Like and comment one post of each user.
    public void FollowLikeComment(int number) {

        // createCommentTemplates();

        //Extract a user which have at least number follower.
        String randomUserId = getRandomFollowerId();
        while (getFollowerIds(randomUserId).size() < number) {
            System.out.println("Search new user ...");
            randomUserId = getRandomFollowerId();
        }
        System.out.println("Extract " + getFollowerIds(randomUserId).size() + " follower");
        extractFollower(randomUserId);

        DBCollection collFollower = db.getCollection(randomUserId + "_Follower");
        DBCursor curs = collFollower.find();

        Random random = new Random();

        while (curs.hasNext()){
            //Follow
            String userId = (String) curs.next().get("_id");
            followUser(userId);
            System.out.println("Follow user:" + userId);

            //Like and comment post
            String postId = getPostId(userId);
            likePost(postId);
            if(postId.equals(null)) {
                System.out.println("Post no found!");
            } else {
                System.out.println("Like post:" + postId);
                doComment(postId, getRandomComment());
                System.out.println("Comment post:" + postId);
            }
            //Wait a random time.
            try {
                TimeUnit.SECONDS.sleep((long) random.nextInt(100));
                System.out.println("Wait ...\n" );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public abstract int getFollowerCount();
    public abstract int getFollowingCount();
    public abstract int getCommentCount();
    public abstract int getLikesCount();
    public abstract int  getPostCount();
    public abstract String getUsername(String userId);
    public abstract String getPostId(String userId);
    public abstract List<String> getFollowerIds();
    public abstract List<String> getFollowerIds(String userId);
    public abstract void followUser(String userId);
    public abstract void likePost(String postId);
    public abstract void doComment(String postId, String comment);

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
}
