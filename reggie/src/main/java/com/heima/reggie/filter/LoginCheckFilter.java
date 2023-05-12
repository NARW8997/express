package com.heima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.heima.reggie.common.BaseContext;
import com.heima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // create a path matcher
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("intercept the request:" + request.getRequestURI());

        // get request uri
        String requestUri = request.getRequestURI();

        // define ignored paths
        String[] ignoredPaths = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        boolean isMatch = checkPathsMatching(ignoredPaths, requestUri);

        // if match any of ignored path, release
        if (isMatch) {
            log.info("request path ignored");
            filterChain.doFilter(request, response);
            return;
        }

        Long employeeId = (Long) request.getSession().getAttribute("employee");
        // set employee id into current thread
        BaseContext.setCurrentId(employeeId);

        // check login status
        if (employeeId != null) {
            log.info("already login");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("not login");
        // if not login, response
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean checkPathsMatching(String[] paths, String uri) {
        for (String path : paths) {
            if (PATH_MATCHER.match(path, uri)) {
                return true;
            }
        }
        return false;
    }
}
