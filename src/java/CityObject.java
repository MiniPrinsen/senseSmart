
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import sun.reflect.annotation.TypeAnnotation.LocationInfo.Location;

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
    private ArrayList<BufferedImage> images;
    private long longitude;
    private long latitude;
    
    public int getLength(){
        return images.size();
    }
    
    public CityObject(){
        this.images = new ArrayList<>();
    }
    
    public void addImage(BufferedImage image){
        images.add(image);
    }
    
    public ArrayList<BufferedImage> getImages(){
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
    
    public void setLongitute(long longitude){
        this.longitude = longitude;
    }
    
    public long getLongitude(){
        return longitude;
    }
    
    public void setLatitude(long latitude){
        this.latitude = latitude;
    }
    
    public long getlatitude(){
        return latitude;
    }
    
    @Override
    public String toString(){
        String info = "Name: "+this.name+"\nDescription: "+this.description+
                "\nLongitude: "+this.longitude+"\nLatitude: "+this.latitude;
        return info;
    }
}
