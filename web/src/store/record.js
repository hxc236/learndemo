export default {
    state: {
        is_record: false,
        a_steps: "",
        b_steps: "",
        record_loser: "",
    },
    getters: {
    },
    mutations: {
        updateIsRecord(state, record) {
            state.is_record = record;
        },
        updateStep(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser(state, loser) {
            state.record_loser = loser;
        },
    },
    actions: { 
        
    },
    modules: {
    }
}