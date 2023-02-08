<template>
    <div class="card">
        <div class="card-header">
            <select class="form-select float-end" style="width: 15%;">
                <option selected>C++</option>
                <option value="1">C</option>
                <option value="2">Java</option>
                <option value="3">Python</option>
                <option value="4">JavaScript</option>
            </select>
        </div>
        <div class="card-body">
            <VAceEditor @init="editorInit" :lang=settings.lang :theme=settings.theme style="height: 300px;" :options="{
                enableBasicAutocompletion: true, //启用基本自动完成
                enableSnippets: true, // 启用代码段
                enableLiveAutocompletion: false, // 启用实时自动完成
                fontSize: settings.fontSize, //设置字号
                tabSize: settings.tabSize, // 缩进大小
                showPrintMargin: false, //去除编辑器里的竖线
                highlightActiveLine: true,
            }">
                <slot></slot>
            </VAceEditor>
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
            fontSize: 14,
            tabSize: 4,
        });

        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/");

        console.log(settings);
        if (settings.language === "C++" || settings.language === "C") {
            settings.lang = "c_cpp";
            console.log(settings.lang);
        } else if (settings.language === "Java") {
            settings.lang = "java";
        } else if (settings.language === "JavaScript") {
            settings.lang = "javascript";
        } else if (settings.language === "Python") {
            settings.lang = "python";
        }

        return {
            settings
        }
    }
}
</script>

<style scoped>
/* VAceEditor.default {

} */
</style>