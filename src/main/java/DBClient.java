import com.mongodb.*;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 * Created by bar on 07.06.17.
 */
public class DBClient {

    private DB db;

    public DBClient() {

        try{
            // Connect to mongodb server
            MongoClient mongoClient = new MongoClient();

            // Connect to database
            db = mongoClient.getDB( "FollowerDB" );
            System.out.println("Connect to database " + db.getName() + " successfully");

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public void checkInstagramFollower(InstaBot instaBot) throws InstagramException {

        DBCollection instaColl = db.getCollection("InstagramFollower");

        //Set all followMe values to 0.
        for(DBObject doc : instaColl.find()) {
            doc.put("followMe", 0);
            instaColl.save(doc);
        }

        //Insert the follower of the given user.
        for (UserFeedData user : instaBot.getFollower()) {
            BasicDBObject doc = new BasicDBObject("_id", user.getId()).append("followMe", 1);
            instaColl.save(doc);
        }

        //Print all db entries
        System.out.println("\nDatabase_Content:");
        for(DBObject doc : instaColl.find()) {
            System.out.println(doc.toString());
        }
    }

}
