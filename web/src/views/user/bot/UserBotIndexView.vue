<template>
    <ContentField>我的bot</ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
    components: {
        ContentField
    },
    setup() {
        localStorage.setItem("current_page", "user_bot_index");

        const store = useStore();



        // $.ajax({
        //     url: "http://127.0.0.1:1644/user/bot/add/",
        //     type: "post",

        //     data: {
        //         title: "hxc的Bot标题2",
        //         description: "hxc的Bot的描述2",
        //         content: "hxc的Bot的代码2",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp)
        //     },
        //     error(resp) {
        //         console.log(resp)
        //     }
        // })

        // $.ajax({
        //     url: "http://127.0.0.1:1644/user/bot/update/",
        //     type: "post",

        //     data: {
        //         bot_id: 3,
        //         title: "hxc的Bot标题10",
        //         description: "hxc的Bot的描述10",
        //         content: "#include <iostream>",
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp)
        //     },
        //     error(resp) {
        //         console.log(resp)
        //     }
        // })

        $.ajax({
            url: "http://127.0.0.1:1644/user/bot/getlist/",
            type: "get",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp)
            },
            error(resp) {
                console.log(resp)
            }
        })
    }
}
</script>

<style scoped>
</style>