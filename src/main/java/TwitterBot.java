import java.util.List;

/**
 * Created by sg58jiqa on 06.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
public class TwitterBot extends SocialBot {

    public TwitterBot() {
        super("TWitter");
    }

    @Override
    public String getUsername(String user_id) {
        return null;
    }

    @Override
    public List<String> getFollowerIds() {
        return null;
    }

    @Override
    public int getFollowerCount() {
        return 0;
    }

    @Override
    public int getFollowingCount() {
        return 0;
    }

    @Override
    public int getCommentCount() {
        return 0;
    }

    @Override
    public int getPostCount() {
        return 0;
    }

    @Override
    public int getLikesCount() {
        return 0;
    }
}
