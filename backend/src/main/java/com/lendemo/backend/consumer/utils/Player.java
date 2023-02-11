package com.lendemo.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private ArrayList<Integer> steps;       // 储存Player每一步走的方向
}
