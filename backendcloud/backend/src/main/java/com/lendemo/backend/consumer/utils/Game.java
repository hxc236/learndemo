package com.lendemo.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.lendemo.backend.consumer.WebSocketServer;
import com.lendemo.backend.pojo.Bot;
import com.lendemo.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {  // 有多个Client端时会有多局游戏，因此Game需要多线程操作，要继承自Thread
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] gameMap;

    private static final int[] dx = {1, 0, -1, 0};
    private static final int[] dy = {0, 1, 0, -1};

    private final Player playerA;
    private final Player playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private final Integer drawTime = 200;   // 前端绘制一格需要多少毫秒
    private final ReentrantLock lock = new ReentrantLock();       // 引入用来控制互斥操作的“锁”

    private final static String addBotUrl = "http://127.0.0.1:1646/bot/add/";

    private String status = "playing";      // playing -> finished

    private String loser = "";    // all -> 平局, A: A输, B: B输


    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.gameMap = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if(botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public int[][] getGameMap() {
        return gameMap;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {  // 会在WebSocketServer中更改nextStep的值，因此会有两个线程同时操作一个变量，会有读写冲突
        lock.lock();
        try {       // 这样写，在报异常的时候也会解锁，不会产生死锁
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }

    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if(sx == tx && sy == ty) return true;
        gameMap[sx][sy] = 1;
        for(int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i];
            int y = sy + dy[i];
            if(gameMap[x][y] == 0 && x > 0 && x < this.rows - 1 && y > 0 && y < this.cols - 1)
                if(check_connectivity(x, y, tx, ty)) {
                    gameMap[sx][sy] = 0;
                    return true;
                }
        }
        gameMap[sx][sy] = 0;
        return false;
    }

    private boolean drawMap() {
        for(int i = 0; i < this.rows; i ++ )
            for(int j = 0; j < this.cols; j ++ )
                gameMap[i][j] = 0;

        // 给地图加一圈围墙
        for(int r = 0; r < this.rows; r ++ )
            gameMap[r][0] = gameMap[r][this.cols - 1] = 1;

        for(int c = 0; c < this.cols; c ++ )
            gameMap[0][c] = gameMap[this.rows - 1][c] = 1;

        // 按照中心对称生成障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i ++ ) {
            for(int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(gameMap[r][c] == 1 || gameMap[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                gameMap[r][c] = gameMap[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        // 判断连通性
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for(int i = 0; i < 1000; i ++ ) {
            if(drawMap())
                break;
        }
    }

    private String getBotCodeInput(Player player) { // 将当前的局部信息编码成字符串
        // map#my_sx#my_sy#(my_steps)#your_sx#your_sy#(your_steps)
        StringBuilder input = new StringBuilder();
        Player opponentPlayer;
        if(player.getId().equals(playerA.getId()))
            opponentPlayer = playerB;
        else
            opponentPlayer = playerA;
        input.append(getGamemapString())
                .append("#")
                .append(player.getSx())
                .append("#")
                .append(player.getSy())
                .append("#(")
                .append(player.getSteps())
                .append(")#")
                .append(opponentPlayer.getSx())
                .append("#")
                .append(opponentPlayer.getSy())
                .append("#(")
                .append(opponentPlayer.getSteps())
                .append(")");
        return input.toString();
    }

    private void sendBotCode(Player player) {
        if(player.getBotId().equals(-1)) return;
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getBotCodeInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep() {    // 等待两名玩家的下一步操作
        try {
            Thread.sleep(200);     // 在前端绘制一格的200ms内，防止接受过多的输入
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 分别判断两名玩家是AI还是人
        sendBotCode(playerA);
        sendBotCode(playerB);



        for(int i = 0; i < 50; i ++ )
        {
            try {
                Thread.sleep(200);
                lock.lock();
                try {
                    if(nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendAllMessage(String message) {
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private String getGamemapString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i ++ )
            for(int j = 0; j < cols; j ++ )
                res.append(gameMap[i][j]);
        return res.toString();
    }

    private void saveToDatabase() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getGamemapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() { // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {     // 前者玩家（A）最后一步是否非法
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);      // 最后一步走到的格子
        if(gameMap[cell.getX()][cell.getY()] == 1) {
            return false;
        }
        for(int i = 0; i < n - 1; i ++ ) {
            if(cellsA.get(i).getX() == cell.getX() && cellsA.get(i).getY() == cell.getY())
                return false;
            if(cellsB.get(i).getX() == cell.getX() && cellsB.get(i).getY() == cell.getY())
                return false;
        }
        return true;
    }

    private void judge() {  // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA, validB;
        validA = check_valid(cellsA, cellsB);
        validB = check_valid(cellsB, cellsA);

        if(!validA || !validB) {
            this.status = "finished";

            if(!validA && !validB) {
                this.loser = "all";
            } else if(!validA) {
                this.loser = "A";
            } else {
                this.loser = "B";
            }
        }
    }

    private void sendMove() {   // 向两个Client传递移动信息
        JSONObject resp = new JSONObject();
        resp.put("event", "move");
        lock.lock();
        try {
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for(int i = 0; i < 1000; i ++ ) {
            if(nextStep()) {    // 是否获取两条蛇的下一步操作
                judge();
                if(status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if(nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                // 发送游戏结果
                sendResult();
                break;
            }

        }
    }
}
