<template>
  <title>校友活动查询 | 南开大学校友信息系统</title>
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
        <v-toolbar-title>校友活动查询</v-toolbar-title>
        <div class="d-flex justify-center align-center" style="min-width: 400px; width: 50vw">
          <v-text-field
            hide-details
            prepend-icon="mdi-list-status"
            variant="underlined"
            single-line
            label="活动标题"
            density="compact"
            color="primary"
            v-model="search.acTitle"
          ></v-text-field>
          <v-text-field
            class="ml-2"
            hide-details
            prepend-icon="mdi-account"
            variant="underlined"
            single-line
            label="发起人（选填）"
            density="compact"
            color="primary"
            v-model="search.username"
          ></v-text-field>
          <v-btn class="mx-4" color="primary" variant="flat" @click="searchUser">查询</v-btn>
          <v-dialog v-model="dialog" max-width="500px">
            <template v-slot:activator="{ props }">
              <v-btn color="primary" dark class="mr-4" variant="outlined" v-bind="props">添加活动</v-btn>
            </template>
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
                    label="审核状态"
                    :items="['未审核', '通过', ' 未通过']"
                    class="mt-4"
                    color="primary"
                    v-model="editedItem.acStatus"
                  ></v-select>
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
            <v-card-title class="text-h5">确定要删除该活动吗？</v-card-title>
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
const blank = ref('请先执行查询操作');
const $ = inject('axios');
const snackbar = inject('snackbar');
const dialog = ref(false);
const dialogDelete = ref(false);
const search = ref({
  acTitle: '',
  username: '',
});
const rules = {
  required: value => !!value || '此项必填',
  date: v => /^((19|20)\d{2})-(\d{1,2})-(\d{1,2})$/.test(v) || '请输入如2023-11-6类似的有效日期',
};
const formBody = [
  { text: '活动标题', model: 'acTitle', validate: [rules.required] },
  { text: '活动描述', model: 'acDesc', validate: [rules.required] },
  { text: '活动发起人', model: 'aluId', validate: [rules.required] },
  { text: '创建时间', model: 'acCreateTime', validate: [rules.required, rules.date] },
  { text: '开始时间', model: 'acStartTime', validate: [rules.required, rules.date] },
  { text: '结束时间', model: 'acEndTime', validate: [rules.required, rules.date] },
  { text: '需要人数', model: 'acCount', validate: [rules.required] },
  { text: '审核意见', model: 'acOpinion', validate: [rules.required] },
];
const headers = ref([
  { title: '活动标题', align: 'start', key: 'acTitle' },
  { title: '发起人Id', key: 'aluId' },
  { title: '创建时间', key: 'acCreateTime' },
  { title: '开始时间', key: 'acStartTime' },
  { title: '结束时间', key: 'acEndTime' },
  { title: '需要人数', key: 'acCount' },
  { title: '审核状态', key: 'acStatus' },
  { title: '审核意见', key: 'acOpinion' },
  { title: '操作', key: 'actions' },
]);
const desserts = ref([]);
const editedIndex = ref(-1);
const editedItem = ref({
  acId: 0,
  acTitle: '',
  acDesc: '',
  aluId: '',
  acCreateTime: '',
  acStartTime: '',
  acEndTime: '',
  acCount: '',
  acStatus: '',
  acOpinion: '',
});
const defaultItem = ref({
  acId: 0,
  acTitle: '',
  acDesc: '',
  aluId: '',
  acCreateTime: '',
  acStartTime: '',
  acEndTime: '',
  acCount: '',
  acStatus: '',
  acOpinion: '',
});
const formTitle = computed(() => {
  return editedIndex.value === -1 ? '添加活动' : '编辑活动信息';
});
watch(dialog, val => {
  val || close();
});
watch(dialogDelete, val => {
  val || closeDelete();
});

const status = ['未审核', '通过', '未通过'];

function preProcess(data) {
  data.acCreateTime = new Date(data.acCreateTime).toLocaleDateString();
  data.acStartTime = new Date(data.acStartTime).toLocaleDateString();
  data.acEndTime = new Date(data.acEndTime).toLocaleDateString();
  data.acStatus = status[data.acStatus];
  return data;
}

function postProcess(data) {
  data.acCreateTime = new Date(data.acCreateTime);
  data.acStartTime = new Date(data.acStartTime);
  data.acEndTime = new Date(data.acEndTime);
  data.acStatus = status.indexOf(data.acStatus);
  return data;
}

function initialize() {
  desserts.value = [
    {
      acId: 1,
      acTitle: '活动 1',
      acDesc: '活动 1111',
      aluId: '1',
      acCreateTime: '2021-06-01',
      acStartTime: '2021-06-01',
      acEndTime: '2021-06-01',
      acCount: '10',
      acStatus: '0-审核中',
      acOpinion: '审核中',
    },
    {
      acId: 2,
      acTitle: '老李家母猪产后护理实践',
      acDesc: '为村东头老李家的母猪进行产后护理实践，劳动时长+1h。',
      aluId: '2',
      acCreateTime: '2021-06-01',
      acStartTime: '2021-06-01',
      acEndTime: '2021-06-01',
      acCount: '10',
      acStatus: '1-通过',
      acOpinion: '批准',
    },
    {
      acId: 3,
      acTitle: '谁问你了',
      acDesc: '活动 3333',
      aluId: '3',
      acCreateTime: '2021-06-01',
      acStartTime: '2021-06-01',
      acEndTime: '2021-06-01',
      acCount: '10',
      acStatus: '2-未通过',
      acOpinion: '我没意见',
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
  $.post('/Activity/deleteActivity', JSON.stringify({ id: editedItem.value.acId })).then(response => {
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
    $.post('/Activity/updateActivity', JSON.stringify(postProcess(editedItem.value)))
      .then(response => {
        // 根据后端返回的数据判断登录状态
        if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
          // 登录成功，处理后续逻辑，比如路由跳转等
          close();
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
    const { acId, ...data } = editedItem.value;
    $.post('/Activity/updateActivity', JSON.stringify(postProcess(data)))
      .then(response => {
        // 根据后端返回的数据判断登录状态
        if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
          // 登录成功，处理后续逻辑，比如路由跳转等
          close();
          desserts.value.push(editedItem.value);
          snackbar.text = '添加成功';
          snackbar.color = 'success';
          snackbar.show = true;
        } else {
          snackbar.text = '添加失败';
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
}
function searchUser() {
  $.post('/Activity/searchByTitleAndUsername', JSON.stringify(search.value))
    .then(response => {
      // 根据后端返回的数据判断登录状态
      if (response.data /* 此处根据后端返回的数据结构进行修改 */) {
        // 登录成功，处理后续逻辑，比如路由跳转等
        desserts.value = preProcess(JSON.parse(response.data));
        if (JSON.parse(response.data).length === 0) {
          blank.value = '没有查询到相关活动';
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
  // initialize();
  sessionStorage.getItem('user') ? '' : useRouter().push('/');
});
</script>
