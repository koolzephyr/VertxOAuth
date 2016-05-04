import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by anons on 5/3/16.
 */
public class Authentication extends AbstractVerticle {
    private final String CLIENT_ID="209383746114188";
    private final String CLIENT_SECRET="5ed66b8fda3fd339c76555f457cec4e7";
    AccessToken token = null;
    //private final String CLIENT_ID="562022547316526";
    //private final String CLIENT_SECRET="f604698ee9a5079d05a947c6963b5a3b";
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Authentication());
        //f604698ee9a5079d05a947c6963b5a3b
        //562022547316526
    }
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);


        //router.route("/auth").handler(this::oAuth);
//        router.route("/result").handler(this::oResult);

        JsonObject credentials = new JsonObject()
                .put("clientID", CLIENT_ID)
                .put("clientSecret", CLIENT_SECRET)
                .put("site", "https://www.facebook.com")
                .put("authorizationPath", "/dialog/oauth")
                .put("tokenPath", "https://graph.facebook.com/oauth/access_token");

        OAuth2Auth oauth2 = OAuth2Auth.create(vertx, OAuth2FlowType.AUTH_CODE, credentials);


        router.route("/facebook").handler(rc->{
            System.out.println("someone is connecting with facebook");
            String authorization_uri = oauth2.authorizeURL(new JsonObject()
                    .put("redirect_uri", "http://localhost:8080/facebook/callback")
                    .put("scope", "email")
                    .put("state", "3(#0/!~"));

            rc.response().putHeader("Location",authorization_uri).setStatusCode(302).end();
        });

        router.route("/facebook/callback").handler(rc->{
            String query = rc.request().query();
            String[] strings=query.split("&");
            String code="",state="";

            for (String string : strings) {
                String[] pair = string.split("=");
                int i=0;
                try {
                    String left = new URI(pair[0]).getPath();
                    String right = new URI(pair[1]).getPath();

                    if (left.equals("code")) code = right;
                    if (left.equals("state")) state = right;

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            JsonObject tokenConfig = new JsonObject()
                    .put("code",code)
                    .put("redirect_uri","http://localhost:8080/facebook/protected");

            oauth2.getToken(tokenConfig,res->{
                if (res.failed()){
                    System.err.println("Access Token Error: " + res.cause().getMessage());
                }
                else {
                    token = res.result();
                    System.out.println("Token received");
                    System.out.println(token);
                }
            });

            rc.response().putHeader("Location","/facebook/protected").setStatusCode(302).end();
        });

        router.route("/facebook/protected").handler(rc->{
            FBGraph fbGraph = new FBGraph(token.toString());
            String graph = fbGraph.getFBGraph();
            Map<String, String> fbProfileData = fbGraph.getGraphData(graph);

            System.out.println("Username: "+fbProfileData.get("first_name"));
            rc.response().end("Welcome "+fbProfileData.get("first_name"));
        });

        router.get("/").handler(res-> {
            res.response().putHeader("content-type", "text/html").end("Hello<br><a href=\"/facebook\">log in with Facebook</a>");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);



    }

    /*public void oAuth(RoutingContext routingContext){
            JsonObject credentials = new JsonObject()
                    .put("clientID", CLIENT_ID)
                    .put("clientSecret", CLIENT_SECRET)
                    .put("site", "https://www.facebook.com")
                    .put("authorizationPath", "/dialog/oauth")
                    .put("tokenPath", "https://graph.facebook.com/oauth/access_token");

            OAuth2Auth oauth2 = OAuth2Auth.create(vertx, OAuth2FlowType.CLIENT, credentials);

            String authorization_uri = oauth2.authorizeURL(new JsonObject()
                    .put("redirect_uri", "http://localhost:8080/callback")
                    .put("scope", "notifications")
                    .put("state", "3(#0/!~"));

            routingContext.response().putHeader("Location", authorization_uri)
                    .setStatusCode(302)
                    .end();


        String code="xxxxxxxxxxxxxxxx";

        oauth2.getToken(new JsonObject().put("code",code).put("redirec_uri","http://localhost:8080/callback"),res ->{
            System.out.println(code);
            if (res.failed()){
                System.err.println("Access Token Error: " + res.cause().getMessage());
            }
            else {
                AccessToken token = res.result();
                System.out.println(token);
            }
        });*/

//            JsonObject tokenConfig = new JsonObject()
//                    .put("code", "<code>")
//                    .put("redirect_uri", "http://localhost:3000/callback");
//
//            oauth2.getToken(tokenConfig, res -> {
//                if (res.failed()) {
//                    System.err.println("Access Token Error: " + res.cause().getMessage());
//                } else {
//                    // Get the access token object (the authorization code is given from the previous step).
//                    AccessToken token = res.result();
//
//                }
//            });

//    }


}



