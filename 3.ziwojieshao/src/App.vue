<script setup>
import canvas1 from '@/components/canvas1.vue'
import canvas2 from '@/components/canvas2.vue'
import PersonalHome from '@/components/PersonalHome.vue'
</script>

<template>
  <div class="page">
    <canvas1 :backgroundColorw="false" class="background" />
    <canvas2 :backgroundColorw="false" />
    <PersonalHome />
  </div>
</template>

<style scoped>
.page {
  width: 100%;
  height: 100%;
  /* background-image: url('@/assets/1.jpg'); */
  background: linear-gradient(135deg, #484848 0%, #8c8c8c 100%);
}

.background {
  opacity: 0.4;
}
</style>
