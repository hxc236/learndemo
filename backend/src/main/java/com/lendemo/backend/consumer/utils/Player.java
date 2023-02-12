package com.lendemo.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private ArrayList<Integer> steps;       // 储存Player每一步走的方向

    private boolean check_tail_increasing(int step) {
        if(step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new LinkedList<>();

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        int x = this.sx, y = this.sy;
        res.add(new Cell(x, y));
        int step = 0;
        for (Integer d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if(!check_tail_increasing( ++ step)) {
                res.remove(0);
            }
        }
        return res;
    }
}
