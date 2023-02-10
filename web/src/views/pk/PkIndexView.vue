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
            };

            socket.onmessage = (msg) => {
                const data = JSON.parse(msg.data);
                console.log(data);
            }
            
            socket.onclose = () => {
                console.log("disconnected!");
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