const AC_GAME_OBJECTS = [];

export class AcGameObject {
    constructor() {
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;             //这一帧执行的时刻距离上一帧执行的时刻距离的时间间隔
        this.has_called_start = false;  //记录start有没有执行过
    }

    start() {   //只执行一次

    }

    update() {  //每一帧执行一次，除了第一帧之外

    }

    on_destroy() {  //删除之前执行

    }

    destroy() {
        this.on_destroy();

        for (let i in AC_GAME_OBJECTS) {        //js 中，遍历用in遍历的是下标，用of遍历的是值
            const obj = AC_GAME_OBJECTS[i];
            if(obj === this) {
                AC_GAME_OBJECTS.splice(i);  //删除第i个元素
                break;
            }
        }
    }
}

let last_timestamp;     //上一次执行的时刻

//定义step函数
const step = timestamp => {     //timestamp 表示当前函数执行的时刻
    for(let obj of AC_GAME_OBJECTS) {
        if(!obj.has_called_start) {     //没有执行过start 即执行start
            obj.has_called_start = true;
            obj.start();
        } else {        //更新timedelta并执行update
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step)     //在下一帧渲染前执行step函数
}

requestAnimationFrame(step)