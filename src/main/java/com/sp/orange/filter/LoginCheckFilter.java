package com.sp.orange.filter;


import com.alibaba.fastjson.JSON;
import com.sp.orange.common.BaseContext;
import com.sp.orange.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter" , urlPatterns = "/*")
public class LoginCheckFilter implements Filter {


    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


//        过滤器处理逻辑如下：
//        1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        String[] urls=new String[] {//  /backend/index.html无法通过/**匹配匹配到,使用AntPathMatcher
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"

        };//这里放的是不需要处理的请求


//        2.判断本次请求是否需要处理
        boolean check = check(urls,requestURI);

//        3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

//        4-1.判断登录状态，如果已登录。则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("员工已登录，员工id为：{}",request.getSession().getAttribute("employee"));
           Long empId =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //        4-2.判断登录状态，如果已登录。则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId =(Long) request.getSession().getAttribute("user");
            //员工、用户同时登录会出现bug
            BaseContext.setCurrentUserId(userId);
            filterChain.doFilter(request,response);
            return;
        }

//        5.如果未登录则返回未登录的结果
        //这里根据js文件，返回相应的结果
        //通过输出流，向客户端相应数据
        log.info("未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url , requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
