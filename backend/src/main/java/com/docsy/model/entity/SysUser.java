package com.docsy.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 系统用户实体
 */
@Data
@Table("sys_user")
public class SysUser {
    @Id
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Integer isActive;
    private String createdAt;
    private String updatedAt;
}
