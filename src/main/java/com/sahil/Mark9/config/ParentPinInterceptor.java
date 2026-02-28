package com.sahil.Mark9.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ParentPinInterceptor implements HandlerInterceptor {

    
    
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        boolean verified = session != null &&
                Boolean.TRUE.equals(session.getAttribute("PARENT_PIN_VERIFIED"));
        System.out.println("PIN session value = " + (session == null ? null : session.getAttribute("PARENT_PIN_VERIFIED")));
        System.out.println("Request URI = " + request.getRequestURI());
        System.out.println("JSESSIONID=" + request.getRequestedSessionId());
        System.out.println("Cookie header=" + request.getHeader("Cookie"));
        if (!verified) {
            response.sendRedirect("/parent/pin");
            return false;
        }

        return true;
    }
}
