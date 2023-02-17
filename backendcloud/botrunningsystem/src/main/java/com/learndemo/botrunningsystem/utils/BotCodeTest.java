package com.learndemo.botrunningsystem.utils;

public class BotCodeTest implements com.learndemo.botrunningsystem.utils.BotInterface {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for(int i = 0, k = 0; i < 13; i ++ ) {
            for(int j = 0; j < 14; j ++ , k ++ ) {
                if(strs[0].charAt(k) == '1')
                    g[i][j] = 1;
            }
        }
        return 0;
    }
}
