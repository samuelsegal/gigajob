package com.spazomatic.jobyjob.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.nosql.repo.PostRepository;
import com.spazomatic.jobyjob.service.PostService;

@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;
	
    @Autowired
	private MongoTemplate mongoTemplate;
	
    @Override
    public Post save(Post post) {
        postRepository.save(post);
        return post;
    }

    @Override
    public Post findOne(String id) {
        return postRepository.findOne(id);
    }

    @Override
    public Iterable<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Page<Post> findByTagsName(String tagName, PageRequest pageRequest) {
        return postRepository.findByTagsName(tagName, pageRequest);
    }
    
    
	@Override
	public Page<Post> findBySpatialDistance( String distance, Point geoPoint, PageRequest pageRequest) {

    	List<Post> postList = 
    			mongoTemplate.find(new Query(Criteria.where("location").nearSphere(geoPoint).maxDistance(30.0d)), Post.class);
    	Page<Post> pagedResult = new PageImpl<>(postList,pageRequest,postList.size());
    	return pagedResult;
	}
	
	@Override
	public Page<Post> findByLocationNear( Point location, String distance, PageRequest pageRequest) {

		Page<Post> postPage = postRepository.findByLocationNear(
				location, new Distance(Double.valueOf(distance), Metrics.MILES), 
				pageRequest);
		return postPage;
	}

	@Override
	public Page<Post> findByUserId(String userId, Pageable pageable) {
		Page<Post> postPage = postRepository.findByUserId(userId, pageable);
		return postPage;		
	}

	@Override
	public Page<Post> findByClientName(String clientName, Pageable pageable) {
		Page<Post> postPage = postRepository.findByClientName(clientName, pageable);
		return postPage;	
	}

	@Override
	public Page<Post> findByTitle(String title, Pageable pageable) {
		Page<Post> postPage = postRepository.findByTitle(title, pageable);
		return postPage;	
	}
}
