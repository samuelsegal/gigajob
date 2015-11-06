package com.spazomatic.jobyjob.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.spazomatic.jobyjob.entities.Post;

public interface PostRepository extends MongoRepository<Post, String>{

    Page<Post> findByTagsName(String name, Pageable pageable);
    Page<Post> findByLocation(Point location, Pageable pageable);
    Page<Post> findByLocationNear(Point location, Distance distance, Pageable pageable);
    List<Post> findByLocationNear(Point location, Distance distance);
}
