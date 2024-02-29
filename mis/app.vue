<template>
  <NuxtLayout>
    <v-app>
      <v-app-bar color="teal-darken-4">
        <template v-slot:image>
          <v-img gradient="to top right, rgba(75, 7, 120,.8), rgba(146, 106, 212,.8)"></v-img>
        </template>

        <template v-slot:prepend>
          <v-app-bar-nav-icon></v-app-bar-nav-icon>
        </template>

        <v-app-bar-title @click="useRouter().push('/show')">南开大学校友信息系统</v-app-bar-title>

        <v-spacer></v-spacer>

        <v-tabs v-model="tab">
          <v-tab value="home" to="/">登录</v-tab>
          <v-tab value="show" to="/show">校园风光</v-tab>
          <v-tab value="info" to="/info">校友信息查询</v-tab>
          <v-tab value="activity" to="/activity">校友活动查询</v-tab>
        </v-tabs>
      </v-app-bar>

      <v-main>
        <NuxtPage />
      </v-main>
      <v-snackbar v-model="snackbar.show" :color="snackbar.color" :close-delay="snackbar.closeDelay">
        {{ snackbar.text }}
        <template v-slot:actions>
          <v-btn variant="text" @click="snackbar.show = false"> 关闭 </v-btn>
        </template>
      </v-snackbar>
    </v-app>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { Axios } from 'axios';
const snackbar = reactive({
  text: '',
  show: false,
  color: 'info',
  closeDelay: 2000,
});
provide('snackbar', snackbar);
const $ = new Axios({
  baseURL: 'http://192.168.3.108:8088',
  // withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
  validateStatus(status) {
    return status < 400;
  },
});
provide('axios', $);

const tab = ref('one');
const router = useRouter();
watch(() => router.currentRoute.value.path, changeTab);
onMounted(changeTab);
function changeTab() {
  let path = router.currentRoute.value.path;
  if (path === '/') {
    tab.value = 'home';
  } else if (path === '/activity') {
    tab.value = 'activity';
  } else if (path === '/show'){
    tab.value = 'show';
  }else if (path === '/info') {
    tab.value = 'info';
  } else {
    tab.value = '';
  }
}
</script>
