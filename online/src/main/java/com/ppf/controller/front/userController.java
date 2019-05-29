package com.ppf.controller.front;

import com.ppf.common.ResponseCode;
import com.ppf.common.ServerResponse;
import com.ppf.pojo.User;
import com.ppf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")
public class userController {
    @Autowired
    HttpSession session;
    /**
     * 注册
     * */
    @Autowired
    IUserService userService;
@RequestMapping("register.do")
    public ServerResponse register(User user){
    return  userService.register(user);
}
/**
 * 登陆
 * */
@RequestMapping("login.do")
public ServerResponse login(User user){
    ServerResponse serverResponse= userService.login(user,1);

    if(serverResponse.isSucess()){
    session.setAttribute("user",serverResponse.getData());}
    System.out.println(session.getAttribute("user"));
    return serverResponse;
}
/**
 * 忘记密码
 * */
@RequestMapping("forget_question")
public ServerResponse forget_question(String username){
    return userService.forget_question(username);
}
@RequestMapping("forget_check_answer")
public ServerResponse forget_check_answer(String username,String question,String answer){

    return userService.forget_check_answer(username,question,answer);
}
@RequestMapping("forget_reset_password")
public ServerResponse forget_reset_password(String username,String newpassword,String forgettoken){

    return userService.forget_reset_password(username,newpassword,forgettoken);
}


/**
 * 登陆后修改密码
 * */
@RequestMapping("modify")
    public ServerResponse modifyPassword(String passwordOld,String password){
        User user=(User)session.getAttribute("user");
        return userService.modifyPassword(passwordOld,user,password);
    }

    /**
     * 登陆状态查看信息
     * */
    @RequestMapping("get_inforamtion.do")
    public ServerResponse getInfo(){
        User user=(User)session.getAttribute("user");
        if(user==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.NOT_LOGIN,"未登录");
        }
        return userService.getInfo(user);
    }
    /**
     * 修改信息
     * */
    @RequestMapping("update_information.do")
    public ServerResponse updateInfo(User user){
        User user1=(User)session.getAttribute("user");
        if(user1==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.NOT_LOGIN,"未登录");
        }
        return userService.updateInfo(user,user1.getId());
    }
    /**
     * 退出登陆
     * */
@RequestMapping("logout.do")
    public ServerResponse logout(){
    session.removeAttribute("user");
    return ServerResponse.createServerResponseBySucess();
}
}
