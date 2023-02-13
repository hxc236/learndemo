<template>
  <ContentField>
      <CodeEditor></CodeEditor>
  </ContentField>
  
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import CodeEditor from '@/components/CodeEditor.vue';


export default {
  components: {
  ContentField,
  CodeEditor,
},
  setup() {
      localStorage.setItem("current_page", "test");
  }
}
</script>

<style scoped>
</style>