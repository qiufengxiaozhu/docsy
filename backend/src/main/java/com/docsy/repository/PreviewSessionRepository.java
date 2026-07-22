package com.docsy.repository;

import com.docsy.model.entity.PreviewSession;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreviewSessionRepository extends CrudRepository<PreviewSession, Long> {

    Optional<PreviewSession> findBySessionId(String sessionId);

    List<PreviewSession> findByStatus(String status);

    List<PreviewSession> findByAppId(String appId);

    @Modifying
    @Query("UPDATE preview_session SET status = 'expired' WHERE status = 'active' AND expires_at < datetime('now', 'localtime')")
    void expireOldSessions();

    @Query("SELECT COUNT(*) FROM preview_session WHERE status = 'active'")
    long countActiveSessions();
}
