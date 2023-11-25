package com.stone.process.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.stone.auth.service.SysUserService;
import com.stone.common.config.exception.StoneException;
import com.stone.common.result.Result;
import com.stone.model.system.SysUser;
import com.stone.utils.JwtHelper;
import com.stone.vo.system.AssginMenuVo;
import com.stone.vo.wechat.BindPhoneVo;
import io.swagger.annotations.Api;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.catalina.mbeans.UserMBean;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("admin/wechat")
@Api("微信公众号")
@CrossOrigin
public class WechatController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private WxMpService wxMpService;
    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
        String redirectUrl = null;
        try {
            redirectUrl = wxMpService.getOAuth2Service()
                    .buildAuthorizationUrl(userInfoUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                            URLEncoder.encode(returnUrl.replace("guiguoa", "#"),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:"+redirectUrl;
    }
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code, @RequestParam("state") String returnUrl) throws Exception{
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        System.out.println("openId: "+openId);
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        System.out.println("微信用户信息"+ JSON.toJSONString(userInfo));
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOpenId,openId);
        SysUser user = userService.getOne(wrapper);
        String token = "";
        if(user!=null){
            token = JwtHelper.createToken(user.getId(), user.getUsername());
        }
        if(returnUrl.indexOf("?")==-1){
            return "redirect:"+returnUrl+"?token="+token+"&openId="+openId;
        }
        return "redirect:"+returnUrl+"&token="+token+"&openId="+openId;
    }
    @ResponseBody
    @PostMapping("bindPhone")
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo){
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone,bindPhoneVo.getPhone());
        SysUser user = userService.getOne(wrapper);
        if(user!=null){
            user.setOpenId(bindPhoneVo.getOpenId());
            userService.updateById(user);
            String token = JwtHelper.createToken(user.getId(), user.getUsername());
            return Result.ok(token);
        }
        return Result.fail().message("手机号不存在");
    }
}
