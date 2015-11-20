package com.spazomatic.jobyjob.nosql.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.spazomatic.jobyjob.nosql.entities.Post;

public interface PostRepository extends MongoRepository<Post, String>{

    Page<Post> findByTagsName(String name, Pageable pageable);
    Page<Post> findByLocation(Point location, Pageable pageable);
    Page<Post> findByLocationNear(Point location, Distance distance, Pageable pageable);
    List<Post> findByLocationNear(Point location, Distance distance);
    Page<Post> findByUserId(String userId, Pageable pageable);
    Page<Post> findByClientName(String clientName, Pageable pageable);
    Page<Post> findByTitle(String title, Pageable pageable);
    Page<Post> findByTitleLike(String title, Pageable pageable);
}
