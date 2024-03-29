package com.sp.reggit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sp.reggit.common.R;
import com.sp.reggit.entity.User;
import com.sp.reggit.service.UserService;
import com.sp.reggit.utils.SMSUtils;
import com.sp.reggit.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            log.info(code);
            //需要将生成的验证码保存到Session中
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
       log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码比对
        if(codeInSession !=null &&codeInSession.equals(codeInSession)){
            //比对成功，则登录成功
            LambdaQueryWrapper<User> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }


        return R.error("登录失败");

    }



}


















