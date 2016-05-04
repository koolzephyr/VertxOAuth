import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anons on 5/3/16.
 */
public class FBGraph {
    private String accessToken;

    public FBGraph(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getFBGraph() {
        String graph = "";
        try {

            String g = "https://graph.facebook.com/me?fields=id,name,email&" + accessToken;
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            graph = b.toString();
            System.out.println(graph);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in getting FB graph data. " + e);
        }
        return graph;
    }
    public Map<String, String> getGraphData(String fbGraph) {
        Map<String, String> fbProfile = new HashMap<String, String>();
        try {
            JsonObject json = new JsonObject(fbGraph);
            fbProfile.put("id", json.getString("id"));
            fbProfile.put("first_name", json.getString("name"));
            if (json.containsKey("email"))
                fbProfile.put("email", json.getString("email"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in parsing FB graph data. " + e);
        }
        return fbProfile;
    }
}
