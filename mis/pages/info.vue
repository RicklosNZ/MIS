<template>
<title>校友信息查询 | 南开大学校友信息系统</title>
<v-parallax :src="loginBg" class="fill-height">
  <v-data-table
    :headers="headers"
    :items="desserts"
    :sort-by="[{ key: 'calories', order: 'asc' }]"
    class="elevation-1"
    color="primary"
  >
    <template v-slot:top>
      <v-toolbar flat floating>
        <v-toolbar-title>校友信息查询</v-toolbar-title>
        <div class="d-flex justify-center align-center" style="min-width: 400px; width: 50vw">
          <v-text-field
            hide-details
            prepend-icon="mdi-account"
            variant="underlined"
            single-line
            label="姓名"
            density="compact"
            color="primary"
            v-model="search.username"
          ></v-text-field>
          <v-text-field
            class="ml-2"
            hide-details
            prepend-icon="mdi-phone"
            variant="underlined"
            single-line
            label="手机号"
            density="compact"
            color="primary"
            v-model="search.phone"
          ></v-text-field>
          <v-btn class="mx-4" color="primary" variant="flat" @click="searchUser">查询</v-btn>
          <v-dialog v-model="dialog" max-width="500px">
            <v-card>
              <v-card-title>
                <span class="text-h5">{{ formTitle }}</span>
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-text-field
                    color="primary"
                    clearable
                    variant="underlined"
                    v-for="i in formBody"
                    :key="i.text"
                    :label="i.text"
                    v-model="editedItem[i.model]"
                    :rules="i.validate"
                    :counter="i.counter || false"
                    class="px-1"
                  />
                  <v-select
                    label="政治面貌"
                    :items="[ '共产党员','预备党员', '共青团员', '民主党派人士', '无党派人士', '群众', '其他']"
                    class="mt-4"
                    color="primary"
                    v-model="editedItem.politicsStatus"
                  ></v-select>
                  <v-select label="性别" :items="['男', '女']" color="primary" v-model="editedItem.sex"></v-select>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" variant="outlined" @click="close">取消</v-btn>
                <v-btn color="primary" variant="flat" @click="save">保存</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </div>
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="text-h5">确定要删除该校友吗？</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="primary" variant="outlined" @click="closeDelete">取消</v-btn>
              <v-btn color="red" variant="flat" @click="deleteItemConfirm">删除</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
    </template>
    <template v-slot:item.actions="{ item }">
      <v-icon size="small" class="me-2" @click="editItem(item)" color="primary"> mdi-pencil </v-icon>
      <v-icon size="small" @click="deleteItem(item)" color="primary"> mdi-delete </v-icon>
    </template>
    <template v-slot:no-data>
      <h4>{{ blank }}</h4>
    </template>
  </v-data-table>
</v-parallax>
</template>
<script setup>
import loginBg from '~/assets/img/login-bg.jpg';
// TODO 删除用户逻辑
const blank = ref('请先执行查询操作');
const $ = inject('axios');
const snackbar = inject('snackbar');
const dialog = ref(false);
const dialogDelete = ref(false);
const search = ref({
  username: '',
  phone: '',
});
const rules = {
  required: value => !!value || '此项必填',
  phone: v => /^1(3|4|5|6|7|8|9)\d{9}$/.test(v) || '请输入11位手机号',
  year: v => /^(19|20)\d{2}$/.test(v) || '请输入四位有效年份',
  yearBig: v => v > editedItem.enrollmentYear || '毕业年份需大于入学年份',
};
const formBody = [
  { text: '用户名', model: 'name', validate: [rules.required] },
  { text: '专业班级', model: 'graduatedDepartment', validate: [rules.required] },
  { text: '籍贯', model: 'nativePlace', validate: [rules.required] },
  { text: '入学年份', model: 'enrollmentYear', validate: [rules.required, rules.year], counter: 4 },
  { text: '毕业年份', model: 'graduatedYear', validate: [rules.required, rules.year, rules.yearBig], counter: 4 },
  { text: '手机号', model: 'mobile', validate: [rules.required, rules.phone], counter: 11 },
];
const headers = ref([
  { title: '用户名', align: 'start', key: 'name' },
  { title: '用户ID', align: 'start', key: 'id' },
  { title: '性别', key: 'sex' },
  { title: '籍贯', key: 'nativePlace' },
  { title: '入学年份', key: 'enrollmentYear' },
  { title: '毕业年份', key: 'graduatedYear' },
  { title: '专业班级', key: 'graduatedDepartment' },
  { title: '政治面貌', key: 'politicsStatus' },
  // { title: '地址', key: 'address' },
  { title: '手机号', key: 'mobile' },
  { title: '操作', key: 'actions', sortable: false },
]);
const desserts = ref([]);
const editedIndex = ref(-1);
const editedItem = ref({
  id: 0,
  name: '',
  password: '',
  graduatedDepartment: '',
  nativePlace: null,
  enrollmentYear: null,
  graduatedYear: null,
  sex: null,
  politicsStatus: null,
  smartphone: null,
  position: null,
  _againPwd: '',
});
const defaultItem = ref({
  id: 0,
  name: '',
  password: '',
  graduatedDepartment: '',
  nativePlace: null,
  enrollmentYear: null,
  graduatedYear: null,
  sex: null,
  politicsStatus: null,
  smartphone: null,
  position: null,
  _againPwd: '',
});
const formTitle = computed(() => {
  return editedIndex.value === -1 ? '添加校友' : '编辑校友信息';
});
watch(dialog, val => {
  val || close();
});
watch(dialogDelete, val => {
  val || closeDelete();
});

// onMounted(initialize);
function initialize() {
  desserts.value = [
    {
      name: '张三',
      sex: '男',
      nativePlace: '河北省',
      enrollmentYear: '2017',
      graduatedYear: '2021',
      graduatedDepartment: '计算机科学与技术',
      politicsStatus: '共产党员',
      address: '南开大学',
      mobile: '12345678901',
    },
    {
      name: '李四',
      sex: '男',
      nativePlace: '安徽省蚌埠市',
      enrollmentYear: '2022',
      graduatedYear: '2026',
      graduatedDepartment: '母猪的产后护理',
      politicsStatus: '群众',
      address: '南开大学',
      mobile: '13333333333',
    },
  ];
}

function editItem(item) {
  editedIndex.value = desserts.value.indexOf(item);
  editedItem.value = Object.assign({}, item);
  dialog.value = true;
}

function deleteItem(item) {
  editedIndex.value = desserts.value.indexOf(item);
  editedItem.value = Object.assign({}, item);
  dialogDelete.value = true;
}

function deleteItemConfirm() {
  $.post('/Alumni/deleteAlumni', JSON.stringify({ id: editedItem.value.id })).then(response => {
    // 根据后端返回的数据判断登录状态
    if (JSON.parse(response.data) /* 此处根据后端返回的数据结构进行修改 */) {
      //还要提供修改功能
      desserts.value.splice(editedIndex.value, 1);
      snackbar.text = '删除成功';
      snackbar.color = 'success';
      snackbar.show = true;
    } else {
      snackbar.text = '删除失败';
      snackbar.color = 'error';
      snackbar.show = true;
    }
  });
  closeDelete();
}

function close() {
  dialog.value = false;
  nextTick(() => {
    editedItem.value = Object.assign({}, defaultItem.value);
    editedIndex.value = -1;
  });
}

function closeDelete() {
  dialogDelete.value = false;
  nextTick(() => {
    editedItem.value = Object.assign({}, defaultItem.value);
    editedIndex.value = -1;
  });
}

function save() {
  if (editedIndex.value > -1) {
    Object.assign(desserts.value[editedIndex.value], editedItem.value);
    $.post('/Alumni/updateAlumni', JSON.stringify(editedItem.value))
      .then(response => {
        // 根据后端返回的数据判断登录状态
        if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
          // 登录成功，处理后续逻辑，比如路由跳转等
          snackbar.text = '修改成功';
          snackbar.color = 'success';
          snackbar.show = true;
        } else {
          snackbar.text = '修改失败';
          snackbar.color = 'error';
          snackbar.show = true;
        }
      })
      .catch(err => {
        snackbar.text = '网络错误，请稍后重试';
        snackbar.color = 'error';
        snackbar.show = true;
      });
  } else {
    desserts.push(editedItem.value);
  }
  close();
}
function searchUser() {
  $.post('/Alumni/searchByNameAndPhone', JSON.stringify(search.value))
    .then(response => {
      // 根据后端返回的数据判断登录状态
      if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
        // 登录成功，处理后续逻辑，比如路由跳转等
        desserts.value = JSON.parse(response.data);
        if (JSON.parse(response.data).length === 0) {
          blank.value = '没有查询到相关校友';
        }
      } else {
      }
    })
    .catch(err => {
      snackbar.text = '网络错误，请稍后重试';
      snackbar.color = 'error';
      snackbar.show = true;
    });
}

onMounted(() => {
  sessionStorage.getItem('user') ? '' : useRouter().push('/');
});
</script>
