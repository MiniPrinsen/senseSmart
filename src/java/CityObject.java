
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
    private String name;
    private String description;
    private ArrayList<String> images;
    private String longitude;
    private String latitude;
    
    public int getLength(){
        return images.size();
    }
    
    public CityObject(){
        this.images = new ArrayList<>();
    }
    
    public void addImagePath(String image){
        images.add(image);
    }
    
    public ArrayList<String> getImagePaths(){
        return images;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public void setLongitute(String longitude){
        this.longitude = longitude;
    }
    
    public String getLongitude(){
        return longitude;
    }
    
    public void setLatitude(String latitude){
        this.latitude = latitude;
    }
    
    public String getlatitude(){
        return latitude;
    }
    
    @Override
    public String toString(){
        String info = "Name: "+this.name+"\nDescription: "+this.description+
                "\nLongitude: "+this.longitude+"\nLatitude: "+this.latitude;
        for (String path : this.images){
            info = info + "\n"+path;
        }
        
        return info;
    }
}
