package com.lendemo.backend.service.impl.pk;

import com.lendemo.backend.consumer.WebSocketServer;
import com.lendemo.backend.consumer.utils.Game;
import com.lendemo.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive bot move: " + userId + " " + direction);
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;
            if(game != null) {
                if(game.getPlayerA().getId().equals(userId)) {   // 判断是哪名玩家
                    game.setNextStepA(direction);
                } else if(game.getPlayerB().getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            }
        }
        return "receive bot move success";
    }
}
