package com.docsy.repository;

import com.docsy.model.entity.SysUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends CrudRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);
}
