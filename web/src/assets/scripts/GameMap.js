import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";


export class GameMap extends AcGameObject {
    constructor(ctx, parent) {  //ctx: 画布;  parent: 父元素 
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;         //地图格子中，一个格子是一个单位乘一个单位，L是一个单位的长度

        this.rows = 13;
        this.cols = 13;

        this.inner_walls_count = 20;
        this.walls = [];
    }

    // flood_fill
    check_connectivity(g, sx, sy, tx, ty) {
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [1, 0, -1, 0], dy = [0, 1, 0, -1];
        for(let i = 0; i < 4; i ++ )
        {
            let x = sx + dx[i], y = sy + dy[i];
            if(!g[x][y])
                return this.check_connectivity(g, x, y, tx, ty); 
        }
        return false;
    }

    create_walls() {
        const g = [];
        // 初始化，g[r][c] = true表示(r, c)有障碍物
        for(let r = 0; r < this.rows; r ++ ) {
            g[r] = [];
            for(let c = 0; c < this.cols; c ++ ) {
                g[r][c] = false;
            }
        }

        // 给四周加上障碍物
        for(let r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.rows - 1] = true;
        }

        for(let c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.cols - 1][c] = true;
        }

        // 创建随机障碍物
        for(let i = 0; i < this.inner_walls_count; i ++ ) {
            for(let j = 0; j < 1000; j ++ ) {
                let r = parseInt(Math.random() * this.rows);     //生成[0, this.rows - 1]中的一个随机整数
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c] || g[c][r]) continue;
                //保证左下角和右上角不会被填充
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                
                g[r][c] = g[c][r] = true;
                break;
            } 

        }

        // 判断连通性
        
        const copy_g = JSON.parse(JSON.stringify(g))    //复制g[]数组，先转成JSON再解析出来
        if(!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        // 绘制墙
        for(let r = 0; r < this.rows; r ++ ) {
            for(let c = 0; c < this.cols; c ++ ) {
                if(g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;
    }

    start() {
        for(let i = 0; i < 1000; i ++ )
            if(this.create_walls()) break;
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))  //转为整型，使得方块之间无缝连接
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
        const color_even = "#aad751";
        const color_odd = "#a2d048";
        for (let r = 0; r < this.rows; r ++ )
            for (let c = 0; c < this.cols; c ++ ) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                //canvas的坐标系中，横坐标为x向右，纵坐标为y向下，因此(r,c)对应到canvas中为(cL,rL)
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);  
            }
                
    }
} 
