package com.lyl.study.cloud.security.api.facade;

import com.lyl.study.cloud.security.api.dto.response.RoleDTO;
import com.lyl.study.cloud.security.api.dto.response.UserDTO;
import com.lyl.study.cloud.security.api.dto.response.UserDetailDTO;

import java.rmi.NoSuchObjectException;
import java.util.List;

public interface UserFacade {
    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 返回对应ID的用户信息；找不到用户时，返回null
     */
    UserDetailDTO getById(long id);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回对应用户名的用户信息；找不到用户时，返回null
     */
    UserDetailDTO getByUsername(String username);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 返回对应ID用户的角色列表
     * @throws NoSuchObjectException 找不到用户时抛出次异常
     */
    List<RoleDTO> getRolesByUserId(long userId) throws NoSuchObjectException;

    /**
     * 查询用户列表
     *
     * @param pageIndex 页码
     * @param pageSize  页面大小
     * @return 用户列表
     */
    List<UserDTO> list(int pageIndex, int pageSize);

    /**
     * 新增用户信息
     *
     * @return 新用户ID
     */
    long save();

    /**
     * 修改用户信息
     */
    void update();

    /**
     * 根据ID删除用户信息
     *
     * @param id 用户ID
     */
    void deleteById(long id);
}