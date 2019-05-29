package com.ppf.service.impl;

import com.ppf.common.ResponseCode;
import com.ppf.common.RoleEnum;
import com.ppf.common.ServerResponse;
import com.ppf.dao.UserMapper;
import com.ppf.pojo.User;
import com.ppf.service.IUserService;
import com.ppf.utils.MD5Utils;
import com.ppf.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public ServerResponse register(User user) {
        if(user==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }
        int result=userMapper.isexistsusername(user.getUsername());
        if(result>0){//用户名已存在
            return ServerResponse.createServerResponseByFail(ResponseCode.USERNAME_EXISTS,"用户名已存在");
        }
        int resultemail=userMapper.isexistsemail(user.getEmail());
        if(resultemail>0){//邮箱已存在
            return ServerResponse.createServerResponseByFail(ResponseCode.EMAIL_EXISTS,"邮箱已存在");
        }
        //step4: MD5密码加密，设置用户角色 ADMIN ---XXXXXXXXXXXXX
        user.setPassword(MD5Utils.getMD5Code(user.getPassword()));
        //设置角色为普通用户
        user.setRole(RoleEnum.ROLE_USER.getRole());
        //step5: 注册
        int insertResult= userMapper.insert(user);
        if(insertResult<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"注册失败");
        }
        return ServerResponse.createServerResponseBySucess();
    }

    @Override
    public ServerResponse login(User user,int type) {
        System.out.println(MD5Utils.getMD5Code(user.getPassword()));
        if(user.getUsername()==null||user.getUsername().equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"用户名不能为空");
        }
        if(user.getPassword()==null||user.getPassword().equals("")) {
            return ServerResponse.createServerResponseByFail(ResponseCode.PASSWORD_NOT_NULL, "密码不能为空");
        }
        if(userMapper.isexistsusername(user.getUsername())>0){

            User user1=userMapper.login(user.getUsername(),MD5Utils.getMD5Code(user.getPassword()));

            if(user1==null){
                return ServerResponse.createServerResponseByFail(ResponseCode.PASSWORD_NOT_EXIST, "密码错误");
            }else {
                if(type==0){
                    if(user1.getRole()==RoleEnum.ROLE_USER.getRole()){
                            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"没有登陆权限");
                    }
                }
                return ServerResponse.createServerResponseBySucess("success",user1);
            }
        }

        return  ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_EXIST,"用户名不存在");
    }

/**
 * 忘记密码的操作
 * */
    @Override
    public ServerResponse forget_question(String username) {
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"用户名不能为空");
        }
        String question=userMapper.forget_question(username);
        if(question==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"未查询到密保问题");
        }
        return ServerResponse.createServerResponseBySucess("密保问题",question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"用户名不能为空");
        }
        if(question==null||question.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"密保问题不能为空");
        }
        if(answer==null||answer.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"答案不能为空");
        }
        int result=userMapper.forget_check_answer(username,question,answer);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"密保答案错误");
        }
        //生成唯一标示token
        String toekn= UUID.randomUUID().toString();
        TokenCache.set("username:"+username,toekn);
        return ServerResponse.createServerResponseBySucess(null,toekn);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String newpassword, String forgettoken) {
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"用户名不能为空");
        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"密码不能为空");
        }
        if(forgettoken==null||forgettoken.equals("")){
            return ServerResponse.createServerResponseByFail(ResponseCode.UERNAME_NOT_NULL,"token不能为空");
        }
       String token=TokenCache.get("username:"+username);
        if(token==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"无权修改别人密码或token已过期");
        }
        if(!token.equals(forgettoken)){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"无效的token");
        }
        int result=userMapper.forget_reset_password(username,MD5Utils.getMD5Code(newpassword));
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"修改失败");
        }
        return ServerResponse.createServerResponseBySucess();
    }

    @Override
    public ServerResponse modifyPassword(String passwordOld,User user,String password){
        int num=userMapper.isexistpassword(MD5Utils.getMD5Code(passwordOld));
        if(num>0){
        int ressult=userMapper.modifyPassword(MD5Utils.getMD5Code(password),user.getUsername());
        if(ressult>0){
            return ServerResponse.createServerResponseBySucess("修改成功");
        }}
        return ServerResponse.createServerResponseByFail(ResponseCode.PASSWORD_NOT_ERROR,"初始密码错误");
    }

    @Override
    public ServerResponse getInfo(User user) {
        User user1=userMapper.getInfo(user.getUsername());
        if(user1!=null){
            return ServerResponse.createServerResponseBySucess("查看成功",user1);
        }
        return null;
    }

    @Override
    public ServerResponse updateInfo(User user,int id) {
        if(user==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数不能为空");
        }
        int result=userMapper.updaInfo(user,id);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"修改失败");
        }
        return ServerResponse.createServerResponseBySucess();
    }
}
