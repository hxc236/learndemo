import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";


export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {  //ctx: 画布;  parent: 父元素 
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;         //地图格子中，一个格子是一个单位乘一个单位，L是一个单位的长度

        this.rows = store.state.pk.gamemap.length;
        this.cols = store.state.pk.gamemap[0].length;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876eb", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#f94847", r: 1, c: this.cols - 2}, this),
        ];
    }

    create_walls() {
        const g = this.store.state.pk.gamemap;

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
