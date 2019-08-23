package org.unibremen.mcyl.androidslicer.web.rest;

import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories("org.unibremen.mcyl.androidslicer.repository")
@Import(value = EmbeddedMongoAutoConfiguration.class)
public class TestMongoConfig {

}