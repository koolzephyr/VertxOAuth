import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.awt.*;
import java.net.URI;
import java.util.Map;

/**
 * Created by anons on 5/3/16.
 */
public class OAuth extends AbstractVerticle{
    private static final long serialVersionUID = 1L;
    private String code="";
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new OAuth());
    }
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        router.route("/auth").handler(this::auth);
        router.route("/fbhome").handler(this::oResult);
    }
//    public void auth(RoutingContext routingContext){
//
//            FBConnection fbConnection = new FBConnection();
//        fbConnection.getFBAuthUrl();
//        code=routingContext.request().getParam("code");
//        if (code == null || code.equals("")) {
//            throw new RuntimeException(
//                    "ERROR: Didn't get code parameter in callback.");
//        }
//        System.out.println(code);
//            String accessToken =fbConnection.getAccessToken(code);
//            FBGraph fbGraph = new FBGraph(accessToken);
//            String graph = fbGraph.getFBGraph();
//            Map<String, String> fbProfileData = fbGraph.getGraphData(graph);
//
//        System.out.println("Username: "+fbProfileData.get("first_name"));
//
//        routingContext.response().setChunked(true);
//        routingContext.response().sendFile("webroot/oauth.html");
//
//    }
//}
    public void auth(RoutingContext routingContext){

        FBConnection fbConnection = new FBConnection();
        String url=fbConnection.getFBAuthUrl();
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void oResult(RoutingContext context){
        code=context.request().getParam("code");
        if (code == null || code.equals("")) {
            throw new RuntimeException(
                    "ERROR: Didn't get code parameter in callback.");
        }
        System.out.println(code);
        FBConnection fbConnection = new FBConnection();
        String accessToken =fbConnection.getAccessToken(code);
        FBGraph fbGraph = new FBGraph(accessToken);
        String graph = fbGraph.getFBGraph();
        Map<String, String> fbProfileData = fbGraph.getGraphData(graph);

        System.out.println("Username: "+fbProfileData.get("first_name"));

        context.response().putHeader("content-type","text/html;charset=UTF-8").end("Welcome "+fbProfileData.get("first_name")+"("+fbProfileData.get("email")+")");
    }

}
