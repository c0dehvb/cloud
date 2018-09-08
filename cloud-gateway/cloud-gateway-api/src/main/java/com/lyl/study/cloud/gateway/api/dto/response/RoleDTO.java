package com.lyl.study.cloud.gateway.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class RoleDTO implements Serializable {
    private Long id;
    private String name;
    private String sign;
    private Long departmentId;
    private String departmentName;
    private Boolean enable;
    private List<PermissionItem> permissions = new ArrayList<>();
    private Long creatorId;
    private Long ownRoleId;
    private Long ownerId;
    private Date createTime;
    private Date updateTime;
}