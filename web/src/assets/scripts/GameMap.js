import { AcGameObject } from "./AcGameObject";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) {  //ctx: 画布;  parent: 父元素 
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;         //地图格子中，一个格子是一个单位乘一个单位，L是一个单位的长度

        this.rows = 13;
        this.cols = 13;
    }

    start() {

    }

    update_size() {
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows)
        // console.log("test start");
        // console.log(this.parent.clientWidth);
        // console.log(this.parent.clientHeight);
        // console.log("test end");
        // this.ctx.canvas.width = this.parent.clientWidth;
        // this.ctx.canvas.height = this.parent.clientHeight;
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
        
    }

    update() {
        this.update_size();
        this.render();
    }

    render() {
        this.ctx.fillStyle = 'green';
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
    }
} 
