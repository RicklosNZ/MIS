<template>
    <title>校园风光 | 南开大学校友信息系统</title>
    <v-parallax :src="loginBg" class="fill-height">
      <v-container class="fill-height d-flex justify-center">
        <v-card 
            class="mx-auto d-flex justify-center" 
            min-width="1000"
            color="#ffffff7f"
             >
            <v-container>
                <div style="display:flex;justify-content:left;"> 
                    <h1>校园风光一览</h1>
                </div>
                <br>

        <v-carousel
            cycle
            hide-delimiter-background
            show-arrows="hover"
        >
            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/tZl6HyxU1AY5ui3.webp"
                cover
            ></v-carousel-item>

            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/nbh1moTI7kWX8ZV.webp"
                cover
            ></v-carousel-item>

            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/84i3ozCQulps7yx.webp"
                cover
            ></v-carousel-item>

            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/oWKCR7pqjzryUY3.webp"
                cover
            ></v-carousel-item>

            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/FiqmQeYD1XpOdyW.webp"
                cover
            ></v-carousel-item>

            <v-carousel-item
                src="https://s2.loli.net/2023/11/13/oWKCR7pqjzryUY3.webp"
                cover
            ></v-carousel-item>
        </v-carousel>
    </v-container>
    </v-card>

      </v-container>
    </v-parallax>
  </template>
  <script setup>
  import loginBg from '~/assets/img/login-bg.jpg';
 
  onMounted(() => {
  sessionStorage.getItem('user') ? '' : useRouter().push('/');
});
  </script>
  