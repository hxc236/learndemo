<template>
    <ContentField>我的bot</ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'

export default {
    components: {
        ContentField
    },
    setup() {
        localStorage.setItem("current_page", "user_bot_index");
    }
}
</script>

<style scoped>
</style>