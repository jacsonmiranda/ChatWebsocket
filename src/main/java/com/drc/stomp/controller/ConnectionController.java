package com.drc.stomp.controller;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drc.stomp.service.ActiveUserManager;

@RestController
public class ConnectionController {
    
    @Autowired
    private ActiveUserManager activeSessionManager;
    
    @PostMapping("/connect")
    public String connect(HttpServletRequest request,
            @ModelAttribute("username") String userName) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("Remote_Addr");
            if (StringUtils.isEmpty(remoteAddr)) {
                remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                }
            }
        }
        
        activeSessionManager.add(userName, remoteAddr);
        return remoteAddr;
    }
    
    @PostMapping("/disconnect")
    public String disconnect(@ModelAttribute("username") String userName) {
        activeSessionManager.remove(userName);
        return "disconnected";
    }
    
    @GetMapping("/active-users-except/{userName}")
    public Set<String> getActiveUsersExceptCurrentUser(@PathVariable String userName) {
        return activeSessionManager.getActiveUsersExceptCurrentUser(userName);
    }
}
