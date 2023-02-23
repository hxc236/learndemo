<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>
                <div class="online_count">
                    当前在线人数: {{ online_player_count }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 15vh;">
                <button type="button" class="btn btn-warning btn-lg" @click="click_match_btn">{{ match_btn_info }}</button>
            </div>
        </div>
</div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
    components: {
    },
    setup() {
        const store = useStore();

        let match_btn_info = ref("开始匹配");
        let bots = ref([]);
        let select_bot = ref("-1");
        let online_player_count = ref("");

        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                match_btn_info.value = "匹配中...";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start_matching",
                    bot_id: select_bot.value,       //一定记得.value啊
                }));
            } else {
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop_matching",
                }));
            }
        }

        const update_online_count = () => {
            $.ajax({
                url: "http://127.0.0.1:1644/pk/getonlineplayernum/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    console.log(resp);
                    online_player_count.value = resp.online_count;
                }
            })
            // requestAnimationFrame(update_online_count);
        }

        update_online_count();

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:1644/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            });
        }

        refresh_bots();     // 从云端动态获取bots

        return {
            match_btn_info,
            click_match_btn,
            bots,           //一定记得返回
            select_bot,
            online_player_count,
        }
    }

}
</script>

<style scoped>
div.matchground {
    width: 60vw;
    /* 60%的浏览器宽度 */
    height: 70vh;
    /* 70%的浏览器高度 */
    margin: 40px auto;
    /* 居中,上边距;  距离上下左右四个方向的距离 */
    background: rgb(0, 0, 0, 0.5);
}

div.user-photo {
    text-align: center;
    padding-top: 10vh;
}

div.user-photo>img {
    border-radius: 50%;
    width: 20vh;

}

div.user-username {
    padding-top: 5%;
    text-align: center;
    font-size: xx-large;
    font: bolder;
    font-weight: 700;
    color: white;
}

div.user-select-bot {
    padding-top: 20vh;
}
div.user-select-bot > select {
    width: 60%;
    margin: 0 auto;
}

div.online_count {
    text-align: center;
    color: white;
    padding-top: 20%;
    font-size: large;
}
</style>

