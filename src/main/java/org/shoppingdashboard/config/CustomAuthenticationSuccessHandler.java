package org.shoppingdashboard.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
@Log4j2
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Retrieve the username from the Authentication object
        String username = authentication.getName();
       log.info("User {} has successfully logged in", username);
        // Store the username in the session
        HttpSession session = request.getSession();
        session.setAttribute("username", username);

        // Redirect to the default success URL
        response.sendRedirect("/customer/product");
    }
}