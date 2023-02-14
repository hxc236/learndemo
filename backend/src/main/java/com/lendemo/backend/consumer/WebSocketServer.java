package com.lendemo.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.lendemo.backend.consumer.utils.Game;
import com.lendemo.backend.consumer.utils.JwtAuthentication;
import com.lendemo.backend.mapper.RecordMapper;
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
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();   //线程安全的map

    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();     //线程安全的Set

    private User user;  //记录当前链接对应的是谁
    private Session session = null;

    // 由于WebSocketServer不是SpringBoot中的单例模式，因此不能直接Autowired注入
    private static UserMapper userMapper;

    public static RecordMapper recordMapper;

    private Game game = null;


     // @Autowired写在set()方法上，在spring会根据方法的参数类型从ioc容器中找到该类型的Bean对象注入到方法的行参中，
     // 并且自动反射调用该方法(被@Autowired修饰的方法一定会执行)，所以一般使用在set方法中、普通方法不用。
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
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

    private void startMatching() {      // 开始匹配
        System.out.println("startMatching!");
        matchPool.add(this.user);

        // 开始匹配
        while(matchPool.size() >= 2) {
            Iterator<User> iterator = matchPool.iterator();
            User playerA = iterator.next(), playerB =  iterator.next();
            matchPool.remove(playerA);
            matchPool.remove(playerB);

            Game game = new Game(13, 14, 20, playerA.getId(), playerB.getId());
            game.createMap();

            users.get(playerA.getId()).game = game;
            users.get(playerB.getId()).game = game;
            game.start();       // 另起一个线程执行game，start函数封装在线程的基类Thread中

            // 将地图信息打包成JSON传到Client端
            JSONObject respGame = new JSONObject();
            respGame.put("a_id", game.getPlayerA().getId());
            respGame.put("a_sx", game.getPlayerA().getSx());
            respGame.put("a_sy", game.getPlayerA().getSy());
            respGame.put("b_id", game.getPlayerB().getId());
            respGame.put("b_sx", game.getPlayerB().getSx());
            respGame.put("b_sy", game.getPlayerB().getSy());
            respGame.put("map", game.getGameMap());

            JSONObject resp1 = new JSONObject();
            resp1.put("event", "start_matching");
            resp1.put("opponent_username", playerB.getUsername());
            resp1.put("opponent_photo", playerB.getPhoto());
            resp1.put("color", "蓝色");
            resp1.put("game", respGame);
            users.get(playerA.getId()).sendMessage(resp1.toJSONString());

            JSONObject resp2 = new JSONObject();
            resp2.put("event", "start_matching");
            resp2.put("opponent_username", playerA.getUsername());
            resp2.put("opponent_photo", playerA.getPhoto());
            resp2.put("color", "红色");
            resp2.put("game", respGame);
            users.get(playerB.getId()).sendMessage(resp2.toJSONString());


        }
    }

    private void stopMatching() {
        System.out.println("stopMatching!");
        matchPool.remove(this.user);
    }

    private void move(int direction) {
        if(game.getPlayerA().getId().equals(user.getId())) {   // 判断是哪名玩家
            game.setNextStepA(direction);
        } else if(game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {    // 当做路由
        // 从Client接受信息
        System.out.println("receive message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start_matching".equals(event)) {
            startMatching();
        } else if("stop_matching".equals(event)) {
            stopMatching();
        } else if("move".equals(event)) {
            move(data.getInteger("direction"));
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
