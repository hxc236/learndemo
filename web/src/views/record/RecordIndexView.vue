<template>
    <ContentField>
        <table class="table table-striped table-hover" style="text-align: center;">
            <thead>
                <tr>
                    <th>A玩家</th>
                    <th>B玩家</th>
                    <th>对战结果</th>
                    <th>对战时间</th>
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
                        <button @click="open_record_content(rec.record.id)" type="button"
                            class="btn btn-primary">查看录像</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="..." >
            <ul class="pagination" style="float: right;">
                <li class="page-item" @click="click_page(-2)">
                    <a class="page-link" href="#">前一页</a>
                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#">后一页</a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';
import router from '@/router/index';

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
        let pages = ref([]);

        const click_page = page => {
            if(page === -2) page = current_page - 1;
            else if(page === -1) page = current_page + 1;
            
            let max_pages = parseInt(Math.ceil(total_record / 10));
            if(page >= 1 && page <= max_pages) {
                pull_page(page);
            }

        }

        const update_pages = () => {
            let max_pages = parseInt(Math.ceil(total_record / 10));
            let new_pages = [];
            for(let i = current_page - 2; i <= current_page + 2; i ++ ) {
                if(i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === current_page ? "active" : "",
                    });
                    
                }
            }
            pages.value = new_pages;
        }

        const pull_page = page => { // 加载一个新的分页
            current_page = page;
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
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        }

        pull_page(current_page);

        // 将map中的01字符串转化成二维数组
        const stringTo2D = map => {
            let g = [];
            for (let i = 0, k = 0; i < 13; i++) {
                let line = [];
                for (let j = 0; j < 14; j++, k++) {
                    if (map[k] == '0') line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const open_record_content = recordId => {
            for (const record of records.value) {
                if (record.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    console.log(record);
                    store.commit("updateGame", {
                        a_id: record.aid,
                        a_sx: record.asx,
                        a_sy: record.asy,
                        b_id: record.bid,
                        b_sx: record.bsx,
                        b_sy: record.bsy,
                        map: stringTo2D(record.record.map),
                    });
                    store.commit("updateStep", {
                        a_steps: record.record.asteps,
                        b_steps: record.record.bsteps,
                    });
                    store.commit("updateRecordLoser", record.record.loser);
                    router.push({
                        name: "record_content",
                        params: {
                            recordId: recordId,         //recordId:recordId相同可以简写为recordId
                        }
                    });

                    break;
                }
            }
        }

        return {
            records,
            open_record_content,
            pages,
            click_page,
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
