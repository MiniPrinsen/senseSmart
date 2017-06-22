/*TJENA?????*/
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;*/

import java.io.*;
import javax.servlet.*;
import java.util.UUID;
import javax.servlet.http.*;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;

import org.bson.Document;
import org.bson.types.ObjectId;

@MultipartConfig
public class MessageServlet extends HttpServlet {
    private PrintWriter out;
    /*
    * visibleRadius: Cityobjects will be visible within this radius (meter).  
    */
    private final int visibleRadius = 5000;

        
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
        
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        
        if(longitude != null && latitude != null){
            double lng = Double.parseDouble(longitude);
            double lat = Double.parseDouble(latitude);
            
            /* Response as JSON. */
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.write(getCityObjectsJson(lat, lng));
        
        }
        
        else{
            
            Collection<Part> parts = request.getParts();
            CityObject cityobject = new CityObject();

            String imageURL = "../../Databas/images/";
            out.println(parts.size());
            if ( parts.size() >= 5){
                for (Part p : parts){
                    String type = p.getName();
                    switch (type){
                        case "name":
                            cityobject.setName(getStringFromInputStream(
                            p.getInputStream()));
                            out.println("NAME is: "+cityobject.getName());
                            break;
                        case "description":
                            cityobject.setDescription(
                                    getStringFromInputStream(p.getInputStream()));
                            out.println("DESC IS: "+cityobject.getDescription());
                            break;
                        case "obj_longitude":
                            out.println("IN LNG");
                            double lng = Double.parseDouble(getStringFromInputStream(
                            p.getInputStream()).replace(",", "."));
                            out.println(lng);
                            cityobject.setLongitude(lng);
                            out.println("AFTR LNG");

                            break;
                        case "obj_latitude":
                            out.println("IN LAT");
                            double lat = Double.parseDouble(getStringFromInputStream
                                (p.getInputStream()).replace(",", "."));
                            cityobject.setLatitude(lat);
                            out.println("LAT IS: "+cityobject.getCoordinates().getLatitude());

                            break;
                        case "image":
                            String imgName = UUID.randomUUID().toString();
                            BufferedImage bufferedImage = ImageIO.read(p.getInputStream());
                            String filePath = imageURL + imgName+".png";
                            
                            /* Create a unique filename (not human readable) */
                            Path image = Paths.get(filePath);
                            while(Files.exists(image)){
                                imgName = UUID.randomUUID().toString();
                                filePath = imageURL + imgName + ".png";
                                image = Paths.get(filePath);
                            }
                            
                            /* Http-URL for image (mapped in context.xml) */
                            URI imgURL = URI.create(request.getRequestURL().
                                    toString()).resolve(request.getContextPath()
                                            +"/images/"+imgName+".png");
                                   
                            
                            
                            /* Save image to object and file-system. */
                            cityobject.addImagePath(imgURL.toString());
                            ImageIO.write(bufferedImage, "png", new File(filePath));
                            out.print("IMAGE SAVED");
                            break;  

                    }

                }
            }

            else{
                out.println("Du måste fylla i alla fält för att ladda in"
                        + "ett nytt objekt.");
            }
            out.print(cityobject.toString());
            addCityObjectToDatabase(cityobject);
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
        response.setContentType("text/html");

        String uri = request.getRequestURI();
        if (uri.equals("/senseSmart/cityobjects")){
            getCityObjects();
        }
    }
  
    
    private void addCityObjectToDatabase(CityObject cityObject){
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

    //    Gson gson = new Gson();
//String json = gson.toJson(Employee);    
        MongoDatabase database = mongoClient.getDatabase("test");

        MongoCollection cityobjectsCol = database.getCollection("cityobjects");
        
        cityobjectsCol.insertOne(new Document()
               .append("name", cityObject.getName()).append(
               "description", cityObject.getDescription()
               ).append(
               "coordinates", new Document().append(
                       "longitude", cityObject.getCoordinates().getLongitude()).append(
                       "latitude", cityObject.getCoordinates().getLatitude())
                ).append("persons_voted", 0).append("rating", 0).append("images", cityObject.getImages()));
             

    }
    
        /**
         *  Checks if a location is in a specific radius
         */
       public boolean inRadius(double lat1, double lng1, double lat2, double lng2, int radius) {
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
       
       /**
        * Get city objects from the database formatted as a HTML-page. 
        */
       private void getCityObjects(){
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("cityobjects");
   
        FindIterable<Document> iter = collection.find(eq("coordinates", eq("$exists",true)));
            
        
        MongoCursor<Document> cursor = iter.iterator();
        
        ArrayList<CityObject> cityObjects = new ArrayList<>();
        Gson gson = new Gson();
        
        while (cursor.hasNext()) {
            Document temp = cursor.next();
            CityObject testUser = gson.fromJson(temp.toJson(), CityObject.class);
            cityObjects.add(testUser);
        }
        
        cursor.close();
        mongoClient.close();

        /* Prints as HTML */
         out.println("<!DOCTYPE html>");  // HTML 5
         out.println("<html><head>");
         out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n" +
"    <link href=\"https://fonts.googleapis.com/css?family=Open+Sans\" rel=\"stylesheet\"/>");
         out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head>");
         for (CityObject obj : cityObjects){
            out.print(obj.toHTML());
        }
        out.print("</body></html>");
        
        
       }
       
       /**
        * 
        * Gets cityobjects stored in a database (mongodb) based on
        * a latitude and longitude.
        * 
        * @param latitude
        * @param longitude
        * @return JSON-formatted CityObject(s) close to the lat and lng.
        */
        private String getCityObjectsJson(double latitude, double longitude){
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

             // Now connect to your databases

            MongoDatabase database = mongoClient.getDatabase("test");

            MongoCollection<Document> collection = database.getCollection("cityobjects");

            FindIterable<Document> iter = collection.find(eq("coordinates", eq("$exists",true)));


            MongoCursor<Document> cursor = iter.iterator();

            ArrayList<CityObject> cityObjects = new ArrayList<>();
            ArrayList<Document> tmpCities = new ArrayList<>();
            while (cursor.hasNext()) {
                Document temp = cursor.next();
                Document coordinates = (Document)temp.get("coordinates");
                if (coordinates != null){
                    double cityObjectlatitude = (double)coordinates.get("latitude");
                    double cityObjectlongitude = (double)coordinates.get("longitude");
                    boolean inRadius = inRadius(cityObjectlatitude, 
                            cityObjectlongitude, latitude, longitude, visibleRadius);
                  //  if(inRadius){
                    //    return temp.toJson();//out.write(temp.toJson());
                    tmpCities.add(temp);
                   // }

                }
            }
            
            cursor.close();
            mongoClient.close();


            StringBuilder jsonData = new StringBuilder();

            if (!tmpCities.isEmpty()){
                jsonData.append("[");
                for (int i = 0; i<tmpCities.size(); i++){
                    if (i != (tmpCities.size()-1)){
                        jsonData.append(tmpCities.get(i).toJson()+",");
                    }
                    else{
                        jsonData.append(tmpCities.get(i).toJson());
                    }
                }
                jsonData.append("]");
            }

            return jsonData.toString();
     }        
            
}




    