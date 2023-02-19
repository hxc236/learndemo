<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>A玩家</th>
                    <th>B玩家</th>
                    <th>对战结果</th>
                    <th>对战时间</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="rec in records" :key="rec.record.id">
                    <td>
                        <img :src="rec.a_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ rec.a_username }}</span>
                    </td>
                    <td>
                        <img :src="rec.b_photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ rec.b_username }}</span>
                    </td>
                    <td>
                        {{ rec.result }}
                    </td>
                    <td>
                        {{ rec.record.createTime }}
                    </td>
                    <td>
                        <button type="button" class="btn btn-primary">查看录像</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';

export default {
    components: {
        ContentField
    },
    setup() {
        localStorage.setItem("current_page", "record_index");

        const store = useStore();
        let records = ref([]);
        let current_page = 1;
        let total_record = 0;

        const pull_page = page => {

            $.ajax({
                url: "http://127.0.0.1:1644/record/getlist/",
                data: {
                    page,
                },
                tpye: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    records.value = resp.records;
                    total_record = resp.records_count;
                    console.log(resp);
                },
                error(resp) {
                    console.log(resp);
                }
            });
        }

        pull_page(current_page);

        return {
            records,
            total_record,

        }
    }
}
</script>

<style scoped>
img.record-user-photo {
    width: 4vh;
    border-radius: 50%;
}
</style>
