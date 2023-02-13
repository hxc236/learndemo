<template>
    <div class="card">
        <div class="card-header">
            <select class="form-select float-end" style="width: 15%;" @change="update_lang($event.target.value)" >
                <option value="1" selected>C++</option>
                <option value="2">Java</option>
                <option value="3">Python</option>
                <option value="4">JavaScript</option>
            </select>
        </div>
        <div class="card-body">
            <VAceEditor @init="myAceEditorInit" :lang=settings.lang :theme=settings.theme style="height: 300px;" :options="{
                enableBasicAutocompletion: true, //启用基本自动完成
                enableSnippets: true, // 启用代码段
                enableLiveAutocompletion: true, // 启用实时自动完成
                fontSize: settings.fontSize, //设置字号
                tabSize: settings.tabSize, // 缩进大小
                showPrintMargin: false, //去除编辑器里的竖线
                highlightActiveLine: true,
            }" :key="my_editor"/>
        </div>
    </div>


</template>

<script>
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import { reactive } from 'vue';

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components: {
        VAceEditor,
    },
    setup() {

        const settings = reactive({
            lang: "c_cpp",
            theme: "textmate",
            fontSize: 18,
            tabSize: 4,
        });

        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/");

        
        const myAceEditorInit = () => {
            settings.lang = "c_cpp";
            settings.theme = "textmate";
            settings.fontSize = 18;
            settings.tabSize = 4;
            // require("brace/mode/c_cpp");
            // require("brace/theme/chrome");
            // require("brace/ext/emmet");
            // require("brace/ext/language_tools");
        }

        const update_lang = (value) => {
            console.log(value);
            if(value === 1) {
                settings.lang = "c_cpp";
            } else if(value === 2) {
                settings.lang = "java";
            } else if(value === 3) {
                settings.lang = "python";
            } else if(value === 4) {
                settings.lang = "javascript";
            }
        }

        return {
            settings,
            myAceEditorInit,
            update_lang,
        }
    }
}
</script>

<style scoped>
/* VAceEditor.default {

} */
</style>