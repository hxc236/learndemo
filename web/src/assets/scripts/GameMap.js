import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";


export class GameMap extends AcGameObject {
    constructor(ctx, parent) {  //ctx: 画布;  parent: 父元素 
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;         //地图格子中，一个格子是一个单位乘一个单位，L是一个单位的长度

        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876eb", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#f94847", r: 1, c: this.cols - 2}, this),
        ];
    }

    // flood_fill
    check_connectivity(g, sx, sy, tx, ty) {
        if(sx === tx && sy === ty) return true;
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
            g[r][0] = g[r][this.cols - 1] = true;
        }

        for(let c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        // 创建随机障碍物
        for(let i = 0; i < this.inner_walls_count / 2; i ++ ) {
            for(let j = 0; j < 1000; j ++ ) {
                let r = parseInt(Math.random() * this.rows);     //生成[0, this.rows - 1]中的一个随机整数
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                //保证左下角和右上角不会被填充
                if(r === this.rows - 2 && c === 1 || r === 1 && c === this.cols - 2) continue;
                
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
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

    add_listening_events() {        //检测按键等
        this.ctx.canvas.focus();   //聚焦canvas

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {  //绑定一个keydown事件
            if(e.key === 'w') snake0.set_direction(0);
            else if(e.key === 'd') snake0.set_direction(1);
            else if(e.key === 's') snake0.set_direction(2);
            else if(e.key === 'a') snake0.set_direction(3);
            else if(e.key === 'ArrowUp') snake1.set_direction(0);
            else if(e.key === 'ArrowRight') snake1.set_direction(1);
            else if(e.key === 'ArrowDown') snake1.set_direction(2);
            else if(e.key === 'ArrowLeft') snake1.set_direction(3);
        });
    }

    start() {
        for(let i = 0; i < 1000; i ++ )
            if(this.create_walls()) break;

        this.add_listening_events();
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))  //转为整型，使得方块之间无缝连接
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
        
    }

    check_ready() {
        for(const snake of this.snakes) {
            if(snake.status !== "idle") return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    next_step() {   //让两条蛇进入下一回合
        for(const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) { // 检测目标位置是否合法: 没有撞到两条蛇的身体以及障碍物
        for(const wall of this.walls) {
            if(wall.r === cell.r && wall.c === cell.c) 
                return false;
        }
        
        for(const snake of this.snakes) {
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()) {    //蛇尾会前进，不需要判断
                k -- ;
            }
            for(let i = 0; i < k; i ++ ) {
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    update() {
        this.update_size();
        if(this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        // console.log(this.ctx);
        const color_even = "#aad751";
        const color_odd = "#a2d048";
        for (let r = 0; r < this.rows; r ++ )
            for (let c = 0; c < this.cols; c ++ ) {
                if ((r + c) % 2 === 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                //canvas的坐标系中，横坐标为x向右，纵坐标为y向下，因此(r,c)对应到canvas中为(cL,rL)
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);  
            }
                
    }
} 
