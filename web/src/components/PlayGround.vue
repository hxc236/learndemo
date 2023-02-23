<template>
    <div class="playground">
        <GameMap />
        <div class="col-3">
            <ContentField>
                我方蛇颜色：{{ my_color }}
            </ContentField>
        </div>
    </div>
</template>

<script>
import GameMap from './GameMap.vue';
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';

export default {
    components: {
        GameMap,
        ContentField,
    },
    setup() {
        let my_color = ref("");
        const store = useStore();

        if (parseInt(store.state.user.id) === store.state.pk.a_id) {
            my_color.value = "蓝色";
        } else {
            my_color.value = "红色";
        }

        return {
            my_color,
        }
    },


}
</script>

<style scoped>
div.playground {
    width: 60vw;
    /* 60%的浏览器宽度 */
    height: 70vh;
    /* 70%的浏览器高度 */
    margin: 40px auto;
    /* 居中,上边距;  距离上下左右四个方向的距离 */
    /* background: lightblue; */
}
</style>

