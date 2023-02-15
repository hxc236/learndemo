package com.learndemo.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static RestTemplate restTemplate;
    private static List<Player> players = new LinkedList<>(); // 两个线程会访问players,匹配线程|传参的线程
    private final ReentrantLock lock = new ReentrantLock();
    private final static String startGameUrl = "http://127.0.0.1:1644/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();
        try {
            players.add(new Player(userId, rating, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new LinkedList<>();
            for(Player player : players) {
                if(!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() {    // 将所有当前玩家的等待时间+1
        for(Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b) {        // 判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        // 随时间推移，每秒两名玩家允许的分差+10;
        // a允许的分差为a的等待时间 * 10, b允许的分差为b的等待时间 * 10
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b) {   // 返回a和b的匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() {        // 尝试匹配所有玩家
        System.out.println("match players...: " + players.toString());
        boolean[] used = new boolean[players.size()];
        // 匹配策略：等待时间越长的玩家匹配的优先度越高
        for(int i = 0; i < players.size(); i ++ ) {
            if(used[i]) continue;
            for(int j = i + 1; j < players.size(); j ++ ) {
                if(used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if(checkMatched(a, b)) {
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new LinkedList<>();
        for(int i = 0; i < players.size(); i ++ ) {
            if(!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
