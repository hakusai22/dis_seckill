package com.seckill.dis.gateway.config.resolver;

import com.seckill.dis.common.api.cache.RedisServiceApi;
import com.seckill.dis.common.api.cache.vo.SkUserKeyPrefix;
import com.seckill.dis.common.api.user.UserServiceApi;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.gateway.config.access.UserContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解析请求，并将请求的参数设置到方法参数中
 * @author xizizzz
 */
@Service
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private static Logger logger = LoggerFactory.getLogger(UserArgumentResolver.class);

    /**
     * 当请求参数为 UserVo 时，使用这个解析器处理
     * 客户端的请求到达某个 Controller 的方法时，判断这个方法的参数是否为 UserVo，
     * 如果是，则这个 UserVo 参数对象通过下面的 resolveArgument() 方法获取，
     * 然后，该 Controller 方法继续往下执行时所看到的 UserVo 对象就是在这里的 resolveArgument() 方法处理过的对象
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
         logger.info("supportsParameter");
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == UserVo.class;
    }

    /**
     * 从分布式 session 中获取 UserVo 对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        log.info("【User用户信息获取】,message={}", "用户手机号: " + UserContext.getUser().getPhone());
        UserVo user = UserContext.getUser();
        UserContext.close();
        return user;
    }
}
