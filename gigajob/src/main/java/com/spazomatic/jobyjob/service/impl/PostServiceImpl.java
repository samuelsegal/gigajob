package com.spazomatic.jobyjob.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.nosql.repo.PostRepository;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.Util;

@Service
@Transactional
public class PostServiceImpl implements PostService{
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s :: %s", Util.LOG_TAG, PostServiceImpl.class));
    @Autowired private PostRepository postRepository;
    @Autowired private MongoTemplate mongoTemplate;
	@Autowired private GridFsTemplate gridfsTemplate;
	
    @Override
    public Post save(Post post) {
        postRepository.save(post);
        DBObject metaData = new BasicDBObject();
        metaData.put("postid", post.getId()) ;
        InputStream inputStream;
		Map<String, byte[]> imgFiles = post.getImgFiles();
		
		inputStream =new ByteArrayInputStream(post.getImgFiles().get("fileInput1"));
		String id = gridfsTemplate.store(inputStream, "fileinput1", 
						"image/png", metaData).getId().toString();
		LOG.debug(String.format("Created file with id %s", id)); 		
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
        	Map<String, byte[]> imgFiles = new HashMap<>();
        	for(GridFSDBFile gridFSDBFile : files){

        		try {
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		gridFSDBFile.writeTo(baos);
            		byte[] byteArr = baos.toByteArray();
					LOG.debug(String.format("Read %d bytes from imgFile %s ", 
							gridFSDBFile.getLength(), 
							gridFSDBFile.getFilename()));
					imgFiles.put("fileInput1",  byteArr);
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
        		
        		
        	}
        	post.setImgFiles(imgFiles);
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
	@Override
	public Page<Post> findByTitleLike(String title, Pageable pageable) {
		Page<Post> postPage = postRepository.findByTitleLike(title, pageable);
		return postPage;	
	}
}
