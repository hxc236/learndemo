<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-6">
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


export default {
    components: {
    },
    setup() {
        const store = useStore();

        let match_btn_info = ref("开始匹配");

        const click_match_btn = () => {
            if(match_btn_info.value === "开始匹配") {
                match_btn_info.value = "匹配中...";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start_matching",
                }));
            } else {
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop_matching",
                }));
            }
        }

        return {
            match_btn_info,
            click_match_btn
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
</style>

