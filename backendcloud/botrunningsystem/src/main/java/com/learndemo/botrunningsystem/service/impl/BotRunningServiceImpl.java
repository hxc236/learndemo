package com.learndemo.botrunningsystem.service.impl;

import com.learndemo.botrunningsystem.service.BotRunningService;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("addBot: " + userId + ", " + botCode + ", " + input);
        return "add bot success";
    }
}
