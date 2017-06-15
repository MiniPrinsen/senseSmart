/*TJENA?????*/
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;

import org.bson.Document;

@MultipartConfig
public class MessageServlet extends HttpServlet {
    private PrintWriter out;

        
    /**
     * 
     * Läser in statsobjekt från formulär 
     * 
     * @param request Input from user
     * @param response Output from server back to user
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        out = response.getWriter(); 
        Collection<Part> parts = request.getParts();
        CityObject cityobject = new CityObject();
        
        for (Part p : parts){
            String type = p.getName();
            
            switch (type){
                case "name":
                    cityobject.setName(getStringFromInputStream(
                    p.getInputStream()));
                    break;
                case "description":
                    cityobject.setDescription(
                            getStringFromInputStream(p.getInputStream()));
                    break;
                case "longitude":
                    cityobject.setLongitute(Long.parseLong(getStringFromInputStream(
                            p.getInputStream())));
                    break;
                case "latitude":
                    cityobject.setLatitude(Long.parseLong(getStringFromInputStream(
                            p.getInputStream())));
                    break;
                case "image":
                    BufferedImage bufferedImage = ImageIO.read(p.getInputStream());
                    cityobject.addImage(bufferedImage);
                    break;  

            }
            
        }        
        out.print(cityobject.toString());
        
        /* Sparar bilder till filsystemet */
        for (int i = 0; i<cityobject.getImages().size(); i++){
            ImageIO.write(cityobject.getImages().get(i), "png", new File(
                    "/Users/gustafwennerstrom/Documents/Jobb/SenseSmart/Databas"
                            + "/images/"+cityobject.getName()+"-"+i+".png"));
        }
    }
        
    /**
     * 
     * Den här anropas när du går via webben till /Sensesmart/hey utan
     * att använda post
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
    throws ServletException, IOException {
        
        out = response.getWriter();
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");

        if (longitude != null){
            out.println("Longitude "+longitude+"?");
        }
        if (latitude != null){
            out.println("Latitude "+latitude+"?");
        }

    }
    
        /**
         *  Checks if a location is in a specific radius
         */
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
       
       
       public void doStuff(){
        
           try{   	
         // To connect to mongodb server
         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
         // Now connect to your databases
        
         out.println("Connect to database successfully");
         
         MongoDatabase database = mongoClient.getDatabase("test");
         
            MongoCollection<Document> collection = database.getCollection("cityobjects");
            out.println(collection.find().toString());
          
      }
        catch(Exception e){
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
        
   }
       
       public void testGetDataFromDatabase(){
        String storedText = "";
        
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

        MongoDatabase database = mongoClient.getDatabase("test");

        MongoCollection collec1 = database.getCollection("cityobjects"); 
        
        
        FindIterable<Document> iter = collec1.find();
        
        MongoCursor it;
        
        it = iter.iterator();
        
        Document as;
        
        
        while (it.hasNext()){
            
            as = (Document)it.next();
            storedText = as.toString();
            as.remove("_id");
            out.println(as.toJson()+"HEJ");
        }
        
       }
       
       
       /**
        * 
        * Creates a string from inputstream
        * 
        * @param inputstream 
        * @return 
        */
       private String getStringFromInputStream(InputStream inputstream) {
           
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(inputstream,
                        StandardCharsets.UTF_8));
                while ((line = br.readLine()) != null) {
                        sb.append(line);
                }

            } catch (IOException e) {
                out.println(e.getMessage());
            }
            
            return sb.toString();
       
       }



}
    