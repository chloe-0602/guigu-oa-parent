package com.chloe.auth.controller;

import com.chloe.auth.service.SysUserService;
import com.chloe.common.Result;
import com.chloe.common.exception.GuiguException;
import com.chloe.common.jwt.JwtHelper;
import com.chloe.common.utils.MD5;
import com.chloe.model.system.SysUser;
import com.chloe.vo.system.LoginVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;
    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {

        SysUser sysUser = sysUserService.getByUserName(loginVo.getUsername());
        if(null == sysUser){
            throw new GuiguException(201, "用户不存在");
        }
        if(!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())){
            throw new GuiguException(201, "密码错误");
        }
        if(sysUser.getStatus().intValue() == 0){
            throw new GuiguException(201, "用户被禁用");
        }

        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        String username = JwtHelper.getUsername(request.getHeader("token"));
        Map<String, Object> map = sysUserService.getUserInfo(username);
//
//        map.put("roles","[admin]");
//        map.put("name","admin");
//        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
