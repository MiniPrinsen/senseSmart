/*TJENA?????*/
import java.io.*;
import javax.servlet.*;
import java.net.URLEncoder;
import javax.servlet.http.*;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MessageServlet extends HttpServlet {
	private int hej = 5;
    
    private PrintWriter out;
    String storedText = "";
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

        MongoDatabase database = mongoClient.getDatabase("touchpoints");

        MongoCollection collec1 = database.getCollection("touchpoints");
       

        FindIterable<Document> iter = collec1.find();
        MongoCursor it;
        
        Document as;
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
    throws ServletException, IOException {
        
        out = response.getWriter();

        String message = request.getParameter("message");
        Bot bot = new Bot();
        
        if (message != null){
            try {
                
              message = URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            out.println(bot.getReply(message));
        }
        else{
            
                getValue();
        }
    }
    public String getValue() throws IOException {
        it = iter.iterator();
        
        while (it.hasNext()){
        as = (Document)it.next();
        storedText = as.toString();
        out.println("NAME: "+as.getString("name"));
        out.println("COORDINATES: "+as.get("coordinates"));
        out.println("PICTURE: "+as.get("picture")+"\n");
      
  //then
        	
        if (as.get("toucnpoints") != null){
        }
//out.println(storedText);
        }
    return "";
    }
    
       public static boolean inRadius(double lat1, double lng1, double lat2, double lng2, int radius) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        if (dist < radius)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
    