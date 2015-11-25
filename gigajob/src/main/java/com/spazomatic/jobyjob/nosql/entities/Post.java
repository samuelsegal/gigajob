package com.spazomatic.jobyjob.nosql.entities;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.api.client.util.DateTime;
@Document
//@Document(indexName = "jobyjob", type = "geo-class-point-type", shards = 1, replicas = 0)
public class Post {
	
    @Id private String id;
    
    private String title;
    private String description;
    
    private String userId;
    private Boolean active;
    private String clientName;
       
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;
	private String formattedAddress;
	
	private List<String> imageURLs;
    private List<Tag> tags;
    
    @Transient Map<String, byte[]> imgFiles;   
    @CreatedDate private DateTime createdDate;
    
    private DateTime expirationTime;
    
    private EstimatedTime estimatedTime;
    private BasePay basePay;
    
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
	
    public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
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

	public Map<String, byte[]> getImgFiles() {
		return imgFiles;
	}

	public void setImgFiles(Map<String, byte[]> imgFiles) {
		this.imgFiles = imgFiles;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public DateTime getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(DateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

	public EstimatedTime getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(EstimatedTime estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public BasePay getBasePay() {
		return basePay;
	}

	public void setBasePay(BasePay basePay) {
		this.basePay = basePay;
	}    
    
}
