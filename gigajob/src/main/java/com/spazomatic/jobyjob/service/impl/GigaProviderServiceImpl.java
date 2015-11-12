package com.spazomatic.jobyjob.service.impl;

import java.util.List;

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
import org.springframework.stereotype.Service;

import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.repo.GigaProviderRepository;
import com.spazomatic.jobyjob.service.GigaProviderService;

@Service
public class GigaProviderServiceImpl implements GigaProviderService{
    @Autowired
    private GigaProviderRepository gigaProviderRepository;
	
    @Autowired
	private MongoTemplate mongoTemplate;
	
    @Override
    public GigaProvider save(GigaProvider gigaProvider) {
        gigaProviderRepository.save(gigaProvider);
        return gigaProvider;
    }

    @Override
    public GigaProvider findOne(String id) {
        return gigaProviderRepository.findOne(id);
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
    			mongoTemplate.find(new Query(Criteria.where("location").nearSphere(geoPoint).maxDistance(30.0d)), GigaProvider.class);
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
		return gigaProvider;
	}

	@Override
	public GigaProvider update(GigaProvider gigaProvider) {
		Query providerToUpdate = new Query(
			Criteria
				.where("userId")
				.is(gigaProvider.getUserId()));
		
		Update update = new Update()
				.set("name",gigaProvider.getProviderName())
				.set("title", gigaProvider.getTitle())
				.set("description", gigaProvider.getDescription());
			
		return mongoTemplate.findAndModify(providerToUpdate, update, 
				new FindAndModifyOptions().returnNew(true).upsert(true), 
				GigaProvider.class);		
		//mongoTemplate.upsert(query, update, entityClass)
	}
	
	
}
