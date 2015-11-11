package com.spazomatic.jobyjob.nosql.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.spazomatic.jobyjob.nosql.entities.GigaProvider;

public interface GigaProviderRepository extends MongoRepository<GigaProvider, String>{

    Page<GigaProvider> findByTagsName(String name, Pageable pageable);
    Page<GigaProvider> findByLocation(Point location, Pageable pageable);
    Page<GigaProvider> findByLocationNear(Point location, Distance distance, Pageable pageable);
    List<GigaProvider> findByLocationNear(Point location, Distance distance);
    Page<GigaProvider> findByActiveIsTrue(Pageable pageable);
    Page<GigaProvider> findByActiveIsFalse(Pageable pageable);
    GigaProvider findByUserId(String userId);
}
