package com.docsy.repository;

import com.docsy.model.entity.App;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepository extends CrudRepository<App, Long> {
    Optional<App> findByAppId(String appId);
}
