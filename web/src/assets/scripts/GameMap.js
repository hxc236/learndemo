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
            new Snake({ id: 0, color: "#4876eb", r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: "#f94847", r: 1, c: this.cols - 2 }, this),
        ];
    }

    create_walls() {
        const g = this.store.state.pk.gamemap;

        // 绘制墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;
    }

    add_listening_events() {        //检测按键等

        if (this.store.state.record.is_record) {
            let k = 0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if(k >= a_steps.length - 1) {  // 每300ms判断一下蛇有没有走完
                    if(loser === "all" || loser === "A") {
                        snake0.status = "dead";
                    }
                    if(loser === "all" || loser === "B") {
                        snake1.status = "dead"; 
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));

                }  
                k ++ ;
            }, 300);       // 每 多长时间 执行一次
           
        } else {
            this.ctx.canvas.focus();   //聚焦canvas

            this.ctx.canvas.addEventListener("keydown", e => {  //绑定一个keydown事件
                let d = -1;     // 前端发送移动信息
                if (e.key === 'w') d = 0;
                else if (e.key === 'd') d = 1;
                else if (e.key === 's') d = 2;
                else if (e.key === 'a') d = 3;

                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }));
                }
            });
        }

    }

    start() {
        for (let i = 0; i < 1000; i++)
            if (this.create_walls()) break;

        this.add_listening_events();
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))  //转为整型，使得方块之间无缝连接
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;

    }

    check_ready() {
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    next_step() {   //让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }


    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        // console.log(this.ctx);
        const color_even = "#aad751";
        const color_odd = "#a2d048";
        for (let r = 0; r < this.rows; r++)
            for (let c = 0; c < this.cols; c++) {
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
