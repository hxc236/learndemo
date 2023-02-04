export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        //canvas 坐标系的转换
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}