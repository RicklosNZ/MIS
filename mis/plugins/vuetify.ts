import '@mdi/font/css/materialdesignicons.css';
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import { VDataTable } from 'vuetify/labs/VDataTable';

export default defineNuxtPlugin(app => {
  const vuetify = createVuetify({
    components: {
      VDataTable,
    },
  });
  app.vueApp.use(vuetify);
});
