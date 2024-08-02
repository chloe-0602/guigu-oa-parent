package com.chloe.process.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chloe.auth.service.SysUserService;
import com.chloe.common.Result;
import com.chloe.common.jwt.JwtHelper;
import com.chloe.model.system.SysUser;
import com.chloe.vo.wechat.BindPhoneVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@Slf4j
@RequestMapping(value = "/admin/wechat")
@CrossOrigin
public class WechatController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private WxMpService wxMpService;
    @Value("wechat.userInfoUrl")
    private String userInfoUrl;
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) throws UnsupportedEncodingException {
        //由于授权回调成功后，要返回原地址路径，原地址路径带“#”号，当前returnUrl获取带“#”的url获取不全，因此前端把“#”号替换为“guiguoa”了，这里要还原一下
        String redirectURL = wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(userInfoUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl.replace("guiguoa", "#"), "UTF-8"));

        log.info("【微信网页授权】获取code,redirectURL={}", redirectURL);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code, // 微信公众号提供的
                           @RequestParam("state") String returnUrl) throws WxErrorException {

        log.info("【微信网页授权】code={}", code);
        log.info("【微信网页授权】state={}", returnUrl);

        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service()
                .getAccessToken(code);

        String openId = accessToken.getOpenId();
        log.info("【微信网页授权】openId={}", openId);

        WxOAuth2UserInfo wxOAuth2UserInfo = wxMpService.getOAuth2Service()
                .getUserInfo(accessToken, null);
        log.info("【微信网页授权】wxMpUser={}", JSON.toJSONString(wxOAuth2UserInfo));

        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));

        String token = "";
        if(null != sysUser){
            token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        }

        if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        if(null != sysUser) {
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);

            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号码不存在，绑定失败");
        }
    }
}
