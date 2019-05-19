package org.unibremen.mcyl.androidslicer.repository;

import org.unibremen.mcyl.androidslicer.domain.Slice;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Slice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SliceRepository extends MongoRepository<Slice, String> {

    @Query("{ 'running': ?0 }")
    public List<Slice> findByRunning(Boolean running);
    
}
