package com.spazomatic.jobyjob.nosql.entities;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

public class Provider {
    @Id
    private String id;
    
    
    private ObjectId user_id;
    private String title;
    private String description;
    private Boolean active;
    private String providerName;
    
    
	public Provider() {
	}

	public Provider(String id, ObjectId user_id, String title, String description, Boolean active, double[] location,
			List<Tag> tags) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.description = description;
		this.active = active;
		this.location = location;
		this.tags = tags;
	}

	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;
	
    private List<Tag> tags;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ObjectId getUser_id() {
		return user_id;
	}

	public void setUser_id(ObjectId user_id) {
		this.user_id = user_id;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
