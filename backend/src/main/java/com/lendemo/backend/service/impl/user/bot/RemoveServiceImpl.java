package com.lendemo.backend.service.impl.user.bot;

import com.lendemo.backend.mapper.BotMapper;
import com.lendemo.backend.pojo.Bot;
import com.lendemo.backend.pojo.User;
import com.lendemo.backend.service.impl.utils.UserDetailsImpl;
import com.lendemo.backend.service.user.bot.RemoveService;
import com.lendemo.backend.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));

        Map<String, String> map = new HashMap<>();

        Bot bot = botMapper.selectById(bot_id);

        if(bot == null) {
            map.put("error_message", "bot不存在或已被删除");
            return map;
        }

        if(!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "没有权限删除该Bot");
            return map;
        }

        botMapper.deleteById(bot_id);

        map.put("error_message", "success");
        return map;
    }
}
