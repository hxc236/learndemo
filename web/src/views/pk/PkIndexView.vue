<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'"/>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import { onMounted, onUnmounted } from 'vue';       //onMounted当组件加载后，onUnmounted当组件卸载后
import { useStore } from 'vuex';
import MatchGround from '@/components/MatchGround.vue'

export default {
    components: {
        PlayGround,
        MatchGround
    },
    setup() {
        localStorage.setItem("current_page", "pk_index");
    
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:1644/websocket/${store.state.user.token}/`;
        
        let socket = null;
        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })

            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("connected!");
                store.commit("updateSocket", socket);
            };

            socket.onmessage = (msg) => {   // 从后端接受信息
                const data = JSON.parse(msg.data);
                if(data.event === "start_matching") {   //匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    store.commit("updateGame", data.game);
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 200);
                    
                } else if(data.event === "move") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if(data.event === "result") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_eye_direction(data.a_direction);
                    snake1.set_eye_direction(data.b_direction);
                    if(data.loser === "all" || data.loser === "A") {
                        snake0.status = "dead";
                    }
                    if(data.loser === "all" || data.loser === "B") {
                        snake1.status = "dead"; 
                    }
                }
            }
            
            socket.onclose = () => {
                console.log("disconnected!");
                store.commit("updateStatus", "matching");
            }
        });

        onUnmounted(() => {
            socket.close();
        });
    }
}
</script>

<style scoped>
</style>