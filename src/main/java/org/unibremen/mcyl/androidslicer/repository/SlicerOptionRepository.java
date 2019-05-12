package org.unibremen.mcyl.androidslicer.repository;

import org.unibremen.mcyl.androidslicer.domain.SlicerOption;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the SlicerOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SlicerOptionRepository extends MongoRepository<SlicerOption, String> {

}
