package com.docsy.repository;

import com.docsy.model.entity.Font;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FontRepository extends CrudRepository<Font, Long> {
    Optional<Font> findByFontName(String fontName);
}
