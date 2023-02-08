package com.lendemo.backend.service.impl.user.account;

import com.lendemo.backend.pojo.User;
import com.lendemo.backend.service.impl.utils.UserDetailsImpl;
import com.lendemo.backend.service.user.account.InfoService;
import com.lendemo.backend.utils.UserUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getInfo() {

        User user = UserUtil.getUser();

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
