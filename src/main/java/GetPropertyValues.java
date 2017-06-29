import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by sg58jiqa on 29.06.17.
 * sg58jiqa@studerserv.uni-leipzig.de
 */
class GetPropertyValues {
    private HashMap<String,String> result = new HashMap<String, String>();
    private InputStream inputStream;

    HashMap<String,String> getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String[] propNames = {"token","secret", "userId", "consumerKey", "consumerSecret", "accessToken",
                    "accessTokenSecret", "ffRationMax", "ffRationMin", "timeToUnfollow", "lastPost", "numPosts",
                    "numToFollow", "numToUnfollow", "rootUser"};
            for(int i = 0; i < propNames.length-1; i++) {
                result.put(propNames[i], prop.getProperty(propNames[i]));
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }
}
