package com.lendemo.backend.consumer.utils;

import java.util.Random;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] gameMap;

    private static final int[] dx = {1, 0, -1, 0};
    private static final int[] dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        gameMap = new int[rows][cols];
    }

    public int[][] getGameMap() {
        return gameMap;
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

}
