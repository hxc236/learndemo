package com.lendemo.backend.service.impl.pk;

import com.lendemo.backend.consumer.WebSocketServer;
import com.lendemo.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("a_id: " + aId + ", b_id" + bId);
        WebSocketServer.startGame(aId, bId);
        return "start game success";
    }
}
