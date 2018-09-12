package com.lyl.study.cloud.gateway.web.controller;

import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.gateway.api.ErrorCode;
import com.lyl.study.cloud.gateway.api.dto.response.UserDetailDTO;
import com.lyl.study.cloud.gateway.security.CurrentSessionHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    @GetMapping("/currentUser")
    public Result<UserDetailDTO> getCurrentSession() {
        return new Result<>(ErrorCode.OK, "查询成功", CurrentSessionHolder.getCurrentUser());
    }
}
