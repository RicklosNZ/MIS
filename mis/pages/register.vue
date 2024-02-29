<template>
  <title>注册 | 南开大学校友信息系统</title>
  <v-parallax :src="loginBg" class="fill-height">
    <v-container class="fill-height d-flex justify-center">
      <v-card class="mx-auto" min-width="500">
        <v-container>
          <h2 class="text--primary mb-6">注册</h2>
          <v-form fast-fail @submit.prevent>
            <v-text-field
              color="primary"
              clearable
              variant="underlined"
              v-for="i in formBody"
              :key="i.text"
              :label="i.text"
              v-model="regForm[i.model]"
              :rules="i.validate"
              :type="i.isPassword ? (pwdShow[i.isPassword] ? 'text' : 'password') : 'text'"
              :append-icon="i.isPassword ? (pwdShow[i.isPassword] ? 'mdi-eye' : 'mdi-eye-off') : ''"
              @click:append="i.isPassword ? (pwdShow[i.isPassword] = !pwdShow[i.isPassword]) : ''"
              :counter="i.counter || false"
              class="px-1"
            />
            <v-select
              label="政治面貌"
              :items="[ '共产党员', '预备党员', '共青团员', '民主党派人士', '无党派人士','群众', '其他']"
              class="mt-4"
              color="primary"
              v-model="regForm.politicsStatus"
            ></v-select>
            <v-select label="性别" :items="['男', '女']" color="primary" v-model="regForm.sex"></v-select>

            <div class="mt-6 d-flex justify-space-between">
              <v-btn type="submit" color="primary" @click="register()">注册</v-btn>
              <v-btn color="primary" variant="text" @click="useRouter().push('/')">已有账号，返回登录</v-btn>
            </div>
          </v-form>
        </v-container>
      </v-card>
    </v-container>
  </v-parallax>
</template>
<script setup>
import loginBg from '~/assets/img/login-bg.jpg';
const regForm = reactive({
  id: 0,
  name: '',
  password: '',
  graduatedDepartment: '',
  nativePlace: null,
  enrollmentYear: null,
  graduatedYear: null,
  sex: null,
  politicsStatus: null,
  mobile: null,
  position: null,
  _againPwd: '',
});
const pwdShow = reactive([, false, false]);
const rules = {
  required: value => !!value || '此项必填',
  password: v => /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$/.test(v) || '密码必须为数字加字母，且不少于8位',
  passwordMatch: v => v === regForm.password || '两次输入的密码不一致',
  phone: v => /^1(3|4|5|6|7|8|9)\d{9}$/.test(v) || '请输入11位手机号',
  year: v => /^(19|20)\d{2}$/.test(v) || '请输入四位有效年份',
  yearBig: v => v > regForm.enrollmentYear || '毕业年份需大于入学年份',
};
const formBody = [
  { text: '用户名', model: 'name', validate: [rules.required] },
  { text: '密码', model: 'password', validate: [rules.required, rules.password], isPassword: 1 },
  { text: '确认密码', model: '_againPwd', validate: [rules.required, rules.passwordMatch], isPassword: 2 },
  { text: '专业班级', model: 'graduatedDepartment', validate: [rules.required] },
  { text: '籍贯', model: 'nativePlace', validate: [rules.required] },
  { text: '入学年份', model: 'enrollmentYear', validate: [rules.required, rules.year], counter: 4 },
  { text: '毕业年份', model: 'graduatedYear', validate: [rules.required, rules.year, rules.yearBig], counter: 4 },
  { text: '手机号', model: 'mobile', validate: [rules.required, rules.phone], counter: 11 },
];
const inputError = ref('');

const $ = inject('axios');
const snackbar = inject('snackbar');
function register() {
  const { _againPwd, ...data } = regForm;
  $.post('/Alumni/updateAlumni', JSON.stringify(data))
    .then(response => {
      // 根据后端返回的数据判断登录状态
      if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
        // 登录成功，处理后续逻辑，比如路由跳转等
        useRouter().push('/');
        snackbar.text = '注册成功，请您登录';
        snackbar.color = 'success';
        snackbar.show = true;
      } else {
        snackbar.text = '注册失败';
        snackbar.color = 'error';
        snackbar.show = true;
      }
    })
    .catch(err => {
      snackbar.text = '网络错误，请稍后重试';
      snackbar.color = 'error';
      snackbar.show = true;
    });
}
</script>
