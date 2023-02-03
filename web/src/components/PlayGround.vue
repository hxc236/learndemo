<template>
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
import GameMap from './GameMap.vue';

export default {
    components: {
        GameMap,
    }
}
</script>

<style scoped>
div.playground {
    width: 60vw;    /* 60%的浏览器宽度 */
    height: 70vh;   /* 70%的浏览器高度 */
    margin: 40px auto;    /* 居中,上边距;  距离上下左右四个方向的距离 */
    /* background: lightblue; */
}
</style>

