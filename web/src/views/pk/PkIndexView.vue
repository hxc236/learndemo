<template>
    <PlayGround />
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'

export default {
    components: {
        PlayGround
    },
    setup() {
        localStorage.setItem("current_page", "pk_index");
    }
}
</script>

<style scoped>
</style>