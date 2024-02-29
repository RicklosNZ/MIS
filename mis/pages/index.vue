<template>
  <title>登录 | 南开大学校友信息系统</title>
  <v-parallax :src="loginBg" class="fill-height">
    <v-container class="fill-height d-flex justify-center">
      <v-card class="mx-auto" min-width="500">
        <v-container>
          <h2 class="text--primary mb-6">请您先登录</h2>
          <v-form fast-fail @submit.prevent>
            <v-text-field
              color="primary"
              clearable
              :rules="[rules.required]"
              label="用户名"
              prepend-icon="mdi-account"
              variant="underlined"
              :error-messages="inputError"
              v-model="username"
            ></v-text-field>
            <v-text-field
              color="primary"
              clearable
              :rules="[rules.required, rules.min]"
              label="密码"
              prepend-icon="mdi-key"
              variant="underlined"
              :error-messages="inputError"
              :type="passwordShow ? 'text' : 'password'"
              v-model="password"
              :append-icon="passwordShow ? 'mdi-eye' : 'mdi-eye-off'"
              @click:append="passwordShow = !passwordShow"
            ></v-text-field>
            <div class="mt-6 d-flex justify-space-between">
              <v-btn type="submit" color="primary" @click="login()">登录</v-btn>
              <v-btn color="primary" variant="text" @click="useRouter().push('/register')">没有账号？注册一个</v-btn>
            </div>
          </v-form>
        </v-container>
      </v-card>
    </v-container>
  </v-parallax>
</template>
<script setup>
import loginBg from '~/assets/img/login-bg.jpg';
const username = ref('');
const password = ref('');
const userId = ref(0);
const passwordShow = ref(false);
const inputError = ref('');
const rules = {
  required: value => !!value || '此项必填',
  min: v => v.length >= 8 || '最少 8 位',
};
const $ = inject('axios');
const snackbar = inject('snackbar');
function login() {
  $.post('/Alumni/Login', JSON.stringify({ username: username.value, password: password.value }))
    .then(response => {
      // 根据后端返回的数据判断登录状态
      if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
        // 登录成功，处理后续逻辑，比如路由跳转等
        useRouter().push('/show');
        userId.value = response.data.id;
        console.log(userId);
        if (process.client) {
          sessionStorage.setItem('user', response.data);
        }
      } else {
        inputError.value = '用户名或密码错误';
        // alert('用户名或密码错误，请重新试试哟！');
        // 登录失败的处理
        // 可能需要提醒用户用户名或密码错误
      }
    })
    .catch(err => {
      snackbar.text = '网络错误，请稍后重试';
      snackbar.color = 'error';
      snackbar.show = true;
    });
}
</script>
