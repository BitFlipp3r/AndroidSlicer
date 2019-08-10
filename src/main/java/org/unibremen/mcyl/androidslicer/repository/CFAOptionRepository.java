package org.unibremen.mcyl.androidslicer.repository;

import org.unibremen.mcyl.androidslicer.domain.CFAOption;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the CFAOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CFAOptionRepository extends MongoRepository<CFAOption, String> {

}
