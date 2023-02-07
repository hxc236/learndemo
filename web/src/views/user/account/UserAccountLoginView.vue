<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login"> <!--@submit.prevent="login"把提交和login绑定起来，并阻止掉默认行为-->
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                        <!--v-model把username变量和输入内容绑定-->
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password"
                            placeholder="请输入密码">
                    </div>
                    <div class="error-message"> {{ error_message }} </div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>

    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import router from '@/router/index';

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref("");
        let password = ref("");
        let error_message = ref("");

        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token) {
            store.commit("updateToken", jwt_token);
            store.dispatch("getInfo", {
                success() {
                    router.push({name:"home"});
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        } else {
            store.commit("updatePullingInfo", false);
        }

        const login = () => {
            error_message.value = "";
            store.dispatch("login", { //调用user.js中的actions里的函数需要用dispatch
                username: username.value,
                password: password.value,
                success() {           //data.success(resp)时调到的函数
                    store.dispatch("getInfo", {
                        success() {
                            router.push({ name: "home" });
                            console.log(store.state.user);

                        },
                    })
                },
                error() {
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}

div.error-message {
    color: red;
}
</style>