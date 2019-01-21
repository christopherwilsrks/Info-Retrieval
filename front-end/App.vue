<template>
  <div id="app" class="app">

    <div :class="{activated: isshow, inactivated: !isshow}">
      <div class="container">

        <div class="navbar">
          <a class="linktop menumargin gmailbilleder" href="http://mail.nankai.edu.cn/">NKU mail</a>
          <a class="linktop menumargin gmailbilleder" href="https://www.google.com/imghp?hl=zh-CN&tab=wi">图片</a>
          <!--<img class="google-apps menumargin" title="Google apps" src="./assets/google-apps.ico">-->
          <button type="button" class="menumargin log-ind">Log in</button>


        </div>

        <div class="content">
          <img class="google-logo" src="./assets/googlelogo.png" alt="Microphone">
          <div class="search-bar-container">
            <input id="mikrofon" class="search-bar" alt="Search" title="Search" name="search" autofocus=""
                   v-model.trim="inputvalue"  @keyup="get($event)" placeholder="请输入内容" type="text" @keyup.enter="search"
                   @keydown.down="selectDown()" @keydown.up.prevent="selectUp()" @blur="show = false">
            <span class="search-reset" @click="clearInput()">&times;</span>
            <!--<img class="mikrofon" src="./assets/Google_mic.svg.png" title="Search by voice">-->
            <div class="search-select">
              <!-- transition-group也是vue2.0中的新特性,tag='ul'表示用ul包裹v-for出来的li -->
              <transition-group name="itemfade" tag="ul" mode="out-in" v-cloak style="padding-left: 0px; list-style-type: none;">
                <li v-if="show" v-for="(value,index) in myData" :class="{selectback:index==now}" class="search-select-option search-select-list" @mouseover="selectHover(index)" @click="selectClick(index)" :key="value">
                  {{value}}
                </li>
              </transition-group>
            </div>
          </div>
          <div class="buttoncontainer">
            <button class="googleknapper" @click="search()">
              Nookle 搜索
            </button>
            <button class="googleknapper" @click="download()" type="submit">
              手气不错
            </button>
          </div>
        </div>

        <footer class="footerny">
          <p style="text-align: center; color: white">Special offer for NanKai</p>
        </footer>

        <footer class="footer">
          <div class="footer-left">
            <a style="color: white;" class="link"
               href="https://www.google.com/intl/zh-CN_us/ads/?subid=ww-ww-et-g-awa-a-g_hpafoot1_1!o2&utm_source=google.com&utm_medium=referral&utm_campaign=google_hpafooter&fg=1">广告</a>
            <a style="color: white;" class="link"
               href="https://www.google.com/services/?subid=ww-ww-et-g-awa-a-g_hpbfoot1_1!o2&utm_source=google.com&utm_medium=referral&utm_campaign=google_hpbfooter&fg=1">商务</a>
            <!--<a class="link"-->
               <!--href="https://www.google.com/intl/da_dk/about/?utm_source=google.com&utm_medium=referral&utm_campaign=hp-footer&fg=1"></a>-->
          </div>
          <div class="footer-right">
            <a style="color: white;" class="link" href="https://www.google.com/intl/zh-CN_us/policies/privacy/?fg=1">隐私权</a>
            <a style="color: white;" class="link" href="https://www.google.com/intl/zh-CN_us/policies/terms/?fg=1">条款</a>
            <a style="color: white;" class="link" href="https://www.google.com/preferences?hl=zh-CN">设置</a>
          </div>
        </footer>


      </div>
    </div>
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive">
        <!-- 这里是会被缓存的视图组件，比如 page1,page2 -->
      </router-view>
    </keep-alive>
  </div>
</template>

<script>
  import SearchResult from "./components/SearchResult";

  const delay = (function() {
    let timer = 0;
    return function(callback, ms) {
      clearTimeout(timer);
      timer = setTimeout(callback, ms);
    };
  })();
  export default {
    name: "App",
    components: {
      SearchResult: SearchResult
    },
    data() {
      return {
        isshow: true,
        inputvalue: "",
        myData: [],
        now: -1,
        show: false,
      };
    },
    // watch: {
    //   //watch title change
    //   inputvalue() {
    //     if (this.inputvalue == "") {
    //       this.myData = [];
    //     }
    //     delay(() => {
    //       this.fetchData();
    //     }, 300);
    //   },
    // },
    methods: {
      download() {
        window.open("/api/3/3/download");
      },
      get: function(ev) {
        // 如果按得键是上或者下，就不进行ajax
        if (ev.keyCode == 38 || ev.keyCode == 40) {
          return;
        }
        this.fetchData();
        this.show = true;
      },
      async fetchData() {
        this.$axios({
          method: "get",
          url: "/api/3/3/fetch",
          params: {
            query: this.inputvalue
          },
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          }
        }).then(response => {
          console.log(response.data);
          this.myData = response.data;

        }).catch(error => {
          console.log(error);
        });
      },
      search() {
        let postData = this.$qs.stringify({
          query: this.inputvalue,
          field: "",
          pageNow: 1,
        });
        this.$axios({
          method: "post",
          url: "/api/3/3/search",
          data: postData,
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          }
        })
          .then(response => {
            console.log(response.data);
            if (response.data == "") {
              return;
            }
            this.isshow = !this.isshow;
            document.getElementById("app").className = "search_app";
            this.$router.push({name: 'SearchResult', params:{totalResult: response.data, inputvalue: this.inputvalue}});
          })
          .catch(error => {
            console.log(error);
          });
      },
      selectDown: function() {
        this.now++;
        //到达最后一个时，再按下就回到第一个
        if (this.now == this.myData.length) {
          this.now = 0;
        }
        this.inputvalue = this.myData[this.now];
      },
      selectUp: function() {
        this.now--;
        //同上
        if (this.now == -1) {
          this.now = this.myData.length - 1;
        }
        this.inputvalue = this.myData[this.now];
      },
      selectHover: function(index) {
        this.now = index
      },
      selectClick: function(index) {
        this.inputvalue = this.myData[index];
        this.search();
      },
      clearInput: function() {
        this.myData = [];
        this.inputvalue = "";
      },
    }
  };
</script>

<style scoped>
  @import './assets/css/google.css';

  .app {
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.5) 43px, rgba(255, 255, 255, 0.4) 92px, rgba(255, 255, 255, 0.3) 146px, rgba(255, 255, 255, 0.2) 210px, rgba(255, 255, 255, 0.1) 295px, rgba(255, 255, 255, 0) 500px), url(https://source.unsplash.com/featured/1920x1080/?+)!important;
    background-position: center!important;
    /*background-size: cover;*/
    background-attachment: fixed;
    background-repeat: no-repeat;
    padding:0px;
    margin:0px;
    position:absolute;
    top:0px;
    left:0px;
    width:100%;
    height:100%;
    border:hidden;
  }

  .search_app {
    padding:0px;
    margin:0px;
    position:absolute;
    top:0px;
    left:0px;
    width:100%;
    height:100%;
    border:hidden;
  }

  .inactivated {
    display: none;
  }

  .search-select {
    width: 600px;
    box-sizing: border-box;
    z-index: 999;
  }
  .search-select li {
    border: 1px solid #d4d4d4;
  ;
    border-top: none;
    border-bottom: none;
    background-color: #fff;
    width: 100%;
    height: 30px;
  }
  .search-select-option {
    box-sizing: border-box;
    padding: 7px 10px;
  }

  .selectback {
    background-color: #eee !important;
    cursor: pointer
  }
  input::-ms-clear {
    display: none
  }
  .search-reset {
    width: 21px;
    height: 21px;
    position: absolute;
    display: block;
    line-height: 21px;
    text-align: center;
    cursor: pointer;
    font-size: 20px;
    right: 40px;
    top: 12px
  }
  .search-select-list {
    transition: all 0.5s
  }
  .itemfade-enter,
  .itemfade-leave-active {
    opacity: 0;
  }
  .itemfade-leave-active {
    position: absolute;
  }
  .selectback {
    background-color: #eee !important;
    cursor: pointer
  }
  .search-select ul{margin:0;text-align: left; }

</style>
