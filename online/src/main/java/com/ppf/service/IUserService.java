package com.ppf.service;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.User;

public interface IUserService {
    public ServerResponse register(User user);
    public ServerResponse login(User user,int type);

    public ServerResponse forget_question(String username);
    public ServerResponse forget_check_answer(String username,String question,String answer);
    public ServerResponse forget_reset_password(String username,String newpassword,String forgettoken);
    public ServerResponse modifyPassword(String passwordOld,User user,String password);
    public ServerResponse getInfo(User user);
    public ServerResponse updateInfo(User user,int id);
}
