import com.mongodb.*;
import org.jinstagram.exceptions.InstagramException;

import java.util.List;

/**
 * Created by bar on 07.06.17.
 */
public abstract class SocialBot {

    private MongoClient mongoClient;
    private DB db;
    private DBCollection coll;

    public SocialBot(String network) {

        try{
            // Connect to mongodb server
            mongoClient = new MongoClient();

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");
            coll = db.getCollection(network + "Follower");

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
        for(DBObject doc : coll.find(new BasicDBObject("following", 0))) {
            System.out.println(doc.toString());
            System.out.println("Username:" + getUsername(doc.get("_id").toString()));
            ++numNotFollowAnymore;
        }
        System.out.println("Total Number:" + numNotFollowAnymore);
    }

    public abstract int getFollowerCount();
    public abstract int getFollowingCount();
    public abstract int getCommentCount();
    public abstract int getLikesCount();
    public abstract int  getPostCount();
    public abstract String getUsername(String user_id);
    public abstract List<String> getFollowerIds();
}
