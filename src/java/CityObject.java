
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gustafwennerstrom
 */
 
public class CityObject {
	String name;
        private String description;
	private Oid _id;
        private Coordinates coordinates;
        private double rating;
        private int persons_voted;
        private ArrayList<String> images;
        
        public CityObject(){
            images = new ArrayList<>();
            coordinates = new Coordinates();
        }
        
        public void addImagePath(String imgPath){
            images.add(imgPath);
        }
        
        public void setDescription(String desc){
            description = desc;
        }
        
        public ArrayList<String> getImages(){
            return images;
        }
        
        public String getDescription(){
            return description;
        }
        
        public double getRating(){
            return rating;
        }
        
        public int getPersons_voted(){
            return persons_voted;
        }
        
        
        public Oid getOid(){
            return _id;
        }
        
        public Coordinates getCoordinates(){
            return coordinates;
        }
        
        public void setLongitude(double lng){
            coordinates.setLongitude(lng);
        }
        
         public void setLatitude(double lat){
            coordinates.setLatitude(lat);
        }
        
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Mongodb will automatically generate ObjectId
	 * @author fhp
	 *
	 */
	public class Oid{
		String $oid;
		public String get$oid() {
			return $oid;
		}
 
		public void set$oid(String $oid) {
			this.$oid = $oid;
		}
 
	}
        
        public class Coordinates{
            double longitude;
            double latitude;
            
            public double getLongitude(){
                return this.longitude;
            }
            
            public void setLongitude(double longitude){
                this.longitude = longitude;
            }
            
            public double getLatitude(){
                return this.latitude;
            }
            
            public void setLatitude(double latitude){
                this.latitude = latitude;
            }
        }
        
        public String toHTML(){
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div id ='cityObject'>");
            htmlContent.append("<h1>"+name+"</h1>");
            for (String imgUrl : images){
                htmlContent.append("<img src='"+imgUrl+"'</img>");
            }
            htmlContent.append("<p>"+description+"</p>");
            htmlContent.append("<p><b>Longitude: </b>"+coordinates.getLongitude()+"</p>");
            htmlContent.append("<p><b>Latitude: </b>"+coordinates.getLatitude()+"</p>");
            htmlContent.append("<p><b>Antal röster: </b>"+persons_voted+"</p>");
            htmlContent.append("<p><b>Rating: </b>"+rating+"</p>");
            htmlContent.append("</div>");
            return htmlContent.toString();

        }
}