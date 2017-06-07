import com.mongodb.*;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Created by bar on 07.06.17.
 */
public class DBClient {

    private DB db;
    private DBCollection coll;
    private InstaBot instaBot;

    public DBClient(InstaBot instaBot) {

        try{
            // Connect to mongodb server
            MongoClient mongoClient = new MongoClient();

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");
            coll = db.getCollection("InstagramFollower");
            this.instaBot = instaBot;

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
        for (UserFeedData user : instaBot.getFollower()) {
            BasicDBObject doc = new BasicDBObject("_id", user.getId()).append("followMe", 1);
            coll.save(doc);
        }
    }
    //Prints all users not following you back anymore.
    public void showNotFollowingAnymore() throws InstagramException {
        //Print all db entries
        System.out.println("\nDo not follow you anymore:");
        int numNotFollowAnymore = 0;
        for(DBObject doc : coll.find(new BasicDBObject("following", 0))) {
            System.out.println(doc.toString());
            System.out.println("Username:" + instaBot.getUsername(doc.get("_id").toString()));
            ++numNotFollowAnymore;
        }
        System.out.println("Total Number:" + numNotFollowAnymore);
    }

}
