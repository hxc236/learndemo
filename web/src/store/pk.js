// import $ from 'jquery'

export default {
    state: {
        status: "matching",     // matching表示匹配界面，playing表示对战界面
        socket: null,
        opponent_username: "",
        opponent_photo: "",

    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
    },
    actions: { 
        
    },
    modules: {
    }
}