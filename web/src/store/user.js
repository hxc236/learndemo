import $ from 'jquery'

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,     //是否正在拉取信息
    },
    getters: {
    },
    mutations: {    //一般用来修改数据 同步操作可以放在mutations里
        updateUser(state, user) {
            state.id = user.id; 
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        },
    },
    actions: {  // 异步操作只能放在actions里，修改state的函数一般写在actions里
        login(context, data) {      // data为dispatch时传的JSON
            $.ajax({
                url: "http://127.0.0.1:1644/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);        //在actions里调用mutations里的函数需要用commit加字符串
                        data.success(resp);         //回调函数调用dispatch里的success函数?
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        getInfo(context, data) {        //context为上下文, 类似python的self; data为
            $.ajax({
                url: "http://127.0.0.1:1644/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp,            //解构出resp,即把那些id,username,photo那些键值对放在当前的对象里
                            is_login: true,
                        });
                        data.success(resp);     //回调函数
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    console.log(resp);
                }
            });
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            localStorage.removeItem("current_page");
            context.commit("logout");
            location.reload();
        }
    },
    modules: {
    }
}