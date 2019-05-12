package org.unibremen.mcyl.androidslicer.repository;

import org.unibremen.mcyl.androidslicer.domain.SlicerSetting;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the SlicerSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SlicerSettingRepository extends MongoRepository<SlicerSetting, String> {

}
