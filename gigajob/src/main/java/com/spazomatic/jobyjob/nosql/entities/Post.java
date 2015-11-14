package com.spazomatic.jobyjob.nosql.entities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document
//@Document(indexName = "jobyjob", type = "geo-class-point-type", shards = 1, replicas = 0)
public class Post {
    @Id
    private String id;
    private String title;
    private String description;
    
    private String userId;
    private Boolean active;
    private String clientName;
    
    
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

	private List<String> imageURLs;
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
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	
    public List<String> getImageURLs() {
		return imageURLs;
	}

	public void setImageURLs(List<String> imageURLs) {
		this.imageURLs = imageURLs;
	}

	public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
