package com.spazomatic.jobyjob.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.nosql.repo.PostRepository;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.Util;

@Service
public class PostServiceImpl implements PostService{
	
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
    @Autowired private PostRepository postRepository;
    @Autowired private MongoTemplate mongoTemplate;
	@Autowired private GridFsTemplate gridfsTemplate;
	
    @Override
    public Post save(Post post) {
        postRepository.save(post);
       /* DBObject metaData = new BasicDBObject();
        metaData.put("postid", post.getId()) ;
        InputStream inputStream;
		try {
			MultipartFile img1 = post.getImgFiles().get(0);
			inputStream = img1.getInputStream();
	        String id = 
	        		gridfsTemplate.store(inputStream, img1.getName(), 
	        				"image/png", metaData).getId().toString();
	        LOG.debug(String.format("Created file with id %s", id));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} 
		*/
        return post;
    }

    @Override
    public Post findOne(String id) {
        Post post = postRepository.findOne(id);
        syncPostImages(post);

        return post;
    }

    private void syncPostImages(Post post) {
        List<GridFSDBFile> files = gridfsTemplate.find(
        		new Query(Criteria.where("metadata.postid").is(post.getId())));
        if(files != null){
        	LOG.debug(String.format("%d images found for post %s with id %s",
        			files.size(), post.getTitle(), post.getId()));
        	for(GridFSDBFile gridFSDBFile : files){
        		//File tmp = Files.createTempFile(dir, prefix, suffix, attrs)
        		//gridFSDBFile.writeTo(tmp);
        	}
        }
		
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
    			mongoTemplate.find(new Query(
    					Criteria.where("location").nearSphere(geoPoint).maxDistance(30.0d)), 
    					Post.class);
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
