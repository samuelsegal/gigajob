package com.spazomatic.jobyjob.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.repo.GigaProviderRepository;
import com.spazomatic.jobyjob.service.GigaProviderService;
import com.spazomatic.jobyjob.util.Util;

@Service
public class GigaProviderServiceImpl implements GigaProviderService{

	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s :: %s", Util.LOG_TAG, GigaProviderServiceImpl.class));
    @Autowired private GigaProviderRepository gigaProviderRepository;	
    @Autowired private MongoTemplate mongoTemplate;	
    @Autowired private GridFsTemplate gridfsTemplate;
    
    @Override
    public GigaProvider save(GigaProvider gigaProvider) {
        gigaProviderRepository.save(gigaProvider);
        saveImages(gigaProvider);
        
        return gigaProvider;
    }

	@Override
    public GigaProvider findOne(String id) {
        GigaProvider gp = gigaProviderRepository.findOne(id);
        syncProviderImages(gp);
        return gp;
    }

    @Override
    public Iterable<GigaProvider> findAll() {
        return gigaProviderRepository.findAll();
    }

    @Override
    public Page<GigaProvider> findByTagsName(String tagName, PageRequest pageRequest) {
        return gigaProviderRepository.findByTagsName(tagName, pageRequest);
    }
    
    
	@Override
	public Page<GigaProvider> findBySpatialDistance( String distance, Point geoPoint, PageRequest pageRequest) {

    	List<GigaProvider> providerList = 
    			mongoTemplate.find(new Query(Criteria.where("location")
    					.nearSphere(geoPoint).maxDistance(30.0d)), 
    					GigaProvider.class);
    	Page<GigaProvider> pagedResult = new PageImpl<>(providerList,pageRequest,providerList.size());
    	return pagedResult;
	}
	
	@Override
	public Page<GigaProvider> findByLocationNear( Point location, String distance, PageRequest pageRequest) {

		Page<GigaProvider> providerPage = gigaProviderRepository.findByLocationNear(
				location, new Distance(Double.valueOf(distance), Metrics.MILES), 
				pageRequest);
		return providerPage;
	}

	@Override
	public Page<GigaProvider> findByActiveIsTrue(PageRequest pageRequest) {
		
		Page<GigaProvider> providerPage = gigaProviderRepository.findByActiveIsTrue(pageRequest);
		return providerPage;
	}

	@Override
	public Page<GigaProvider> findByActiveIsFalse(PageRequest pageRequest) {
		
		Page<GigaProvider> providerPage = gigaProviderRepository.findByActiveIsTrue(pageRequest);
		return providerPage;		
	}

	@Override
	public GigaProvider findByUserId(String userId) {
		
		GigaProvider gigaProvider = gigaProviderRepository.findByUserId(userId);
		syncProviderImages(gigaProvider);
		return gigaProvider;
	}

	@Override
	public GigaProvider update(GigaProvider gigaProvider) {
		Query providerToUpdate = new Query(
			Criteria
				.where("userId")
				.is(gigaProvider.getUserId()));
		
		Update update = new Update()
				.set("providerName", gigaProvider.getProviderName())
				.set("title",        gigaProvider.getTitle())
				.set("description",  gigaProvider.getDescription())
				.set("location",     gigaProvider.getLocation());
		
		//TODO:only save images if images are new
		Map<String,byte[]> imgFiles = gigaProvider.getImgFiles();
		gigaProvider = mongoTemplate.findAndModify(providerToUpdate, update, 
				new FindAndModifyOptions().returnNew(true).upsert(true), 
				GigaProvider.class);
		gigaProvider.setImgFiles(imgFiles);
		saveImages(gigaProvider);
		return gigaProvider;
	}
	
    private void syncProviderImages(GigaProvider gigaProvider) {
        List<GridFSDBFile> files = gridfsTemplate.find(
        		new Query(Criteria.where("metadata.providerid").is(gigaProvider.getId())));
        if(files != null){
        	LOG.debug(String.format("%d images found for post %s with id %s",
        			files.size(), gigaProvider.getTitle(), gigaProvider.getId()));
        	Map<String, byte[]> imgFiles = new HashMap<>();
        	for(GridFSDBFile gridFSDBFile : files){
        		try {
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		gridFSDBFile.writeTo(baos);
            		byte[] byteArr = baos.toByteArray();
					LOG.debug(String.format("Read %d bytes from imgFile %s ", 
							gridFSDBFile.getLength(), 
							gridFSDBFile.getFilename()));
					imgFiles.put(gridFSDBFile.getFilename(),  byteArr);
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}        		        		
        	}
        	gigaProvider.setImgFiles(imgFiles);
        }		
	}	
    private void saveImages(GigaProvider gigaProvider) {
        //Save Images
        DBObject metaData = new BasicDBObject();
        metaData.put("providerid", gigaProvider.getId()) ;
        
		Map<String, byte[]> imgFiles = gigaProvider.getImgFiles();
		imgFiles.forEach((k,v) ->{
			InputStream inputStream = new ByteArrayInputStream(v);
			String id = gridfsTemplate.store(inputStream, k, 
					"image/png", metaData).getId().toString();	
			LOG.debug(String.format(
					"Stored image file to grifs with id %s", id)); 	
		});        
		
	}	
}
