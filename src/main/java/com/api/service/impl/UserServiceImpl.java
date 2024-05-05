package com.api.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.model.entity.User;
import com.api.service.UserService;
import com.api.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.api.constant.UserConstant.ADMIN_ROLE;
import static com.api.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @author 20466
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-04-30 15:47:21
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "api";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //校验两次密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
        }

        //intern()判断常量池是否已经存在
        synchronized (userAccount.intern()) {
            //账户不重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            //2.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            //3.分配 accessKey,secretKey
            String accessKey = "cli_" + DigestUtil.md5Hex(SALT + userPassword + RandomUtil.randomNumbers(4));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            //4.插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest request) {
        //1.检验用户参数是否存在
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount).eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //4.用户不存在，抛出异常
        if (user == null) {
            log.info("用户不存在或密码输入错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //5.记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        //移除登录
        log.info("用户退出登录...");
        session.removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //1、先判断用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(USER_LOGIN_STATE);
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //2、从数据库中查询，可以使用缓存，提高性能
        long userId = user.getId();
        user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(USER_LOGIN_STATE);
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }
}




