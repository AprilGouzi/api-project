package com.api.aop;

import com.api.annotation.AuthCheck;
import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.api.model.entity.User;
import com.api.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 囍崽
 * version 1.0
 * 权限校验 AOP
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<String> anyRole = Arrays.stream(authCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        //当前登录用户
        User user = userService.getLoginUser(request);
        String userRole = user.getUserRole();
        //拥有任意权限即通过
        if (CollectionUtils.isNotEmpty(anyRole)) {
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        //必须有的权限才能通过
        if (StringUtils.isNotBlank(mustRole)) {
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        //通过权限校验，放行
        return joinPoint.proceed();
    }
}
