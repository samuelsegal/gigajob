package com.spazomatic.jobyjob.entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

//@Document(indexName = "jobyjob", type = "geo-class-point-type", shards = 1, replicas = 0)
public class Post {
    @Id
    private String id;
    private String title;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

	// @Field(type= FieldType.Nested)
    private List<Tag> tags;
    
    public Post(){
    }

    public Post(String title, double latitude, double longitude){
        this.title = title;
        this.location = new double[2];
        this.location[0] = latitude;
        this.location[1] = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}
	
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
