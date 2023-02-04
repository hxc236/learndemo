import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];    //存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null;  //下一步的目标格子

        this.speed = 5; //每秒移动多少个格子
        this.direction = -1;    //下一步的指令，-1表示没有指令，0,1,2,3表示上右下左
        this.status = "idle";   //idle表示静止, move表示正在移动，dead表示死亡
    
        this.dr = [-1, 0, 1, 0];    // 4个行偏移量
        this.dc = [0, 1, 0, -1];    // 4个列偏移量

        this.step = 0;  //表示当前的回合数，用于判断蛇是否变长
        this.eps = 1e-2;    //允许的误差

        this.eye_direction = 0; //眼睛方向
        if(this.id === 1) this.eye_direction = 2;

        this.eye_dx = [ //蛇眼睛不同方向的x偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [ //蛇眼睛不同方向的y偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
    }

    start() {

    }

    check_tail_increasing() {   // 检测当前回合蛇的长度是否增加
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;
        return false;
    }

    set_direction(d) {
        this.direction = d;
    }

    next_step() {   //将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;    //清空操作
        this.status = "move";
        this.step ++ ;

        const k = this.cells.length;
        for(let i = k; i > 0; i -- ) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));  //这里要深复制，因为直接赋值传的都是引用，容易造成混乱
        }

        if(!this.gamemap.check_valid(this.next_cell)) {     //撞上，蛇去世了
            this.status = "dead";

        }
    }


    /**移动逻辑：
     * 
     * 当蛇向下一个方向移动一步时，中间的格子不变，只移动头和尾;
     * 可以在头部生成一个新的Cell，将其移动到下一个格子;
     * 如果当前回合蛇的长度不变长，则蛇尾需要走向下一个目的地;
     * ps. 每一帧移动的距离 = this.speed * this.timedelta
     */
    update_move() {
        
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps) {   // 走到目标点，认为两个点已经重合了
            this.cells[0] = this.next_cell; //将目标点作为新的头部
            this.next_cell = null;  //清空目标点
            this.status = "idle"; // 走完了，停下来

            if(!this.check_tail_increasing()) {
                this.cells.pop(); 
            }
            
        } else {    //不重合
            const move_distance = this.speed * this.timedelta / 1000;   // (单位是ms,转化为s) 每两帧之间走过的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if(!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;

            }
        }
    }

    update() {  //每一帧执行一次
        if(this.status === 'move') {
            this.update_move();
        }
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if(this.status === "dead") {
            ctx.fillStyle = "#757575";
        }

        for(const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length; i ++ ) {
            const a = this.cells[i - 1], b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if(Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.5 + 0.1) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else if(Math.abs(a.y - b.y) < this.eps) {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.5 + 0.1) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            } 
        }

        ctx.fillStyle = "black";
        if(this.status === "dead") ctx.fillStyle = "white";
        for(let i = 0; i < 2; i ++ ) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i]* 0.15) * L ;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i]* 0.15) * L ;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, 2 * Math.PI);
            ctx.fill();
        }
    }

}