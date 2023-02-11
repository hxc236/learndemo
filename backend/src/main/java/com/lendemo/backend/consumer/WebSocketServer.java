package com.lendemo.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.lendemo.backend.consumer.utils.Game;
import com.lendemo.backend.consumer.utils.JwtAuthentication;
import com.lendemo.backend.mapper.UserMapper;
import com.lendemo.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

    //要根据user_id来找到对应的WebSocket链接，从而向客户端发请求 对所有WebSocket实例都可见，同时只有一份
    private static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();   //线程安全的map

    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();

    private User user;  //记录当前链接对应的是谁
    private Session session = null;

    /**
     * 由于WebSocketServer不是SpringBoot中的单例模式，因此不能直接Autowired注入
     */
    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        //建立链接
        this.session = session;
        System.out.println("connected!");

        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);


        if(this.user != null) {
            users.put(userId, this);    //将user存起来
        } else {
            this.session.close();
        }

    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null) {
            users.remove(this.user.getId());
            matchPool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("startMatching!");
        matchPool.add(this.user);

        // 开始匹配
        while(matchPool.size() >= 2) {
            Iterator<User> iterator = matchPool.iterator();
            User player1 = iterator.next(), player2 =  iterator.next();
            matchPool.remove(player1);
            matchPool.remove(player2);

            Game game = new Game(13, 14, 20);
            game.createMap();

            JSONObject resp1 = new JSONObject();
            resp1.put("event", "start_matching");
            resp1.put("opponent_username", player2.getUsername());
            resp1.put("opponent_photo", player2.getPhoto());
            resp1.put("gamemap", game.getGameMap());
            users.get(player1.getId()).sendMessage(resp1.toJSONString());

            JSONObject resp2 = new JSONObject();
            resp2.put("event", "start_matching");
            resp2.put("opponent_username", player1.getUsername());
            resp2.put("opponent_photo", player1.getPhoto());
            resp2.put("gamemap", game.getGameMap());
            users.get(player2.getId()).sendMessage(resp2.toJSONString());


        }
    }

    private void stopMatching() {
        System.out.println("stopMatching!");
        matchPool.remove(this.user);
    }

    @OnMessage
    public void onMessage(String message, Session session) {    // 当做路由
        // 从Client接受信息
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start_matching".equals(event)) {
            startMatching();
        } else {
            stopMatching();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {       //后端向当前链接发送信息
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
