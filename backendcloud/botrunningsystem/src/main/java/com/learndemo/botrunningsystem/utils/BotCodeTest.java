package com.learndemo.botrunningsystem.utils;

import java.util.LinkedList;
import java.util.List;

public class BotCodeTest implements com.learndemo.botrunningsystem.utils.BotInterface {
    static class Cell {
        public int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_tail_increasing(int step) {
        if(step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {
        List<Cell> res = new LinkedList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        for(int i = 0; i < steps.length(); i ++ ) {
            x += dx[steps.charAt(i) - '0'];
            y += dy[steps.charAt(i) - '0'];
            res.add(new Cell(x, y));
            if(!check_tail_increasing(i)) {
                res.remove(0);
            }
        }
        return res;
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
        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3].substring(1, strs[3].length() - 1));
        List<Cell> bCells = getCells(bSx, bSy, strs[6].substring(1, strs[6].length() - 1));

        for(Cell cell: aCells) g[cell.x][cell.y] = 1;
        for(Cell cell: bCells) g[cell.x][cell.y] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for(int i = 0; i < 4; i ++ ) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];

            if(x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }
        return 0;
    }
}
