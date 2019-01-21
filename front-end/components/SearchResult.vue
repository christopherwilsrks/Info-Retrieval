<template>
  <div>
    <div id="wrap">
      <div id="header-top">
        <div class="header-left">
          <a id="logo" href=""><img src="../assets/images/logo-small.png"></a>
          <div id="search-form">
            <input type="text" name="text" input v-model="inputvalue" @keyup="get($event)" @keyup.enter="search(0)"
                   @keydown.down="selectDown()" @keydown.up.prevent="selectUp()" @blur="show = false">
            <div class="search-select">
              <!-- transition-group也是vue2.0中的新特性,tag='ul'表示用ul包裹v-for出来的li -->
              <transition-group name="itemfade" tag="ul" mode="out-in" v-cloak
                                style="padding-left: 0px; list-style-type: none;">
                <li v-if="show" v-for="(value,index) in myData" :class="{selectback:index==now}"
                    class="search-select-option search-select-list" @mouseover="selectHover(index)"
                    @click="selectClick(index)" :key="value">
                  {{value}}
                </li>
              </transition-group>
            </div>
            <div id="voice">
              <a href="#"><img src="../assets/images/voice.png"></a>
            </div>
            <div id="search-button">
              <a href="#" @click="search(0)"><img src="../assets/images/searchb-logo.png"></a>
            </div>
          </div>
        </div>
        <div class="header-right">
          <a class="linktop menumargin gmailbilleder" href="http://mail.nankai.edu.cn/">NKU mail</a>
          <a class="linktop menumargin gmailbilleder" href="https://www.google.com/imghp?hl=zh-CN&tab=wi">图片</a>
          <!--<img class="google-apps menumargin" title="Google apps" src="./assets/google-apps.ico">-->
          <button type="button" class="menumargin log-ind">Log in</button>
          <br style="clear: both"/> <!-- clear floats -->
        </div>
      </div>

      <div id="header-bottom">
        <div class="header2-left">
          <a href="#">All</a>
          <a href="#">Videos</a>
          <a href="#">Maps</a>
          <a href="#">Shopping</a>
          <a href="#">News</a>
          <a href="#">More</a>
          <a href="#">Search tools</a>
        </div>
        <div class="header2-right">
          <a href="#"><img src="../assets/images/priv.png" alt="Currently showing private results"></a>
          <a href="#"><img src="../assets/images/glob.png" alt="Hide private results"></a>
          <a href="#"><img src="../assets/images/settings.png" alt="Options"></a>
        </div>
      </div>


      <div id="container">
        <p>About {{totalResult.total}} results (0.51 seconds)</p>
        <div v-for="result in totalResult.results" class="result">
          <a class="result_title" target="_blank" :href="result.url"><span v-html="result.title"></span><br/>
            <div><cite>{{result.url}}</cite></div></a>

          <p><span style="color: #666">{{result.time.substring(0, 10)}} - </span><span v-html="result.content"></span>
          </p>
          <p>来自：<a :href="result.url.split('/2')[0]" target="_blank" style="color: #1A0DAB; font-size: 13px;">{{result.website}}</a>
          </p>
        </div>
        <!--<div id="related">-->
        <!--<p>Searches related to build this webpage</p>-->
        <!--<table>-->
        <!--<tr>-->
        <!--<td><a href="#"><strong>how to</strong> build <strong>a website</strong></a></td>-->
        <!--<td><a href="#"><strong>how to create a</strong> webpage</a></td>-->
        <!--</tr>-->
        <!--<tr>-->
        <!--<td><a href="#"><strong>how to</strong> build <strong>a</strong> webpage <strong>for free</strong></a>-->
        <!--</td>-->
        <!--<td><a href="#">build <strong>your own</strong> webpage</a></td>-->
        <!--</tr>-->
        <!--<tr>-->
        <!--<td><a href="#">build <strong>a</strong> webpage <strong>google</strong></a></td>-->
        <!--<td><a href="#"><strong>how to</strong> build <strong>a</strong> webpage <strong>from scratch</strong></a>-->
        <!--</td>-->
        <!--</tr>-->
        <!--<tr>-->
        <!--<td><a href="#"><strong>how to</strong> build <strong>a web page for free</strong></a></td>-->
        <!--<td><a href="#"><strong>how to</strong> build <strong>a</strong> webpage <strong>using html</strong></a>-->
        <!--</td>-->
        <!--</tr>-->
        <!--</table>-->
        <!--</div>-->
        <div id="pages">
          <img src="../assets/images/pages.png">
          <div id="numbers">
            <a v-for="index in totalPage" v-if="totalPage <= 10" href="#" :class="{isNowPage: index === pageNow}"
               @click="search(index)" :key="index">{{index}}</a>
            <a v-for="index in 10" v-if="totalPage > 10" :class="{isNowPage: index === pageNow}" href="#"
               @click="search(index)">{{index}}</a>
            <a href="#" @click="search(pageNow + 1)" v-if="totalPage > 1 && pageNow < 10">Next</a>
          </div>
        </div>
      </div>
    </div>


    <div id="footer">
      <div id="footer-content">
        <p>California - From your Internet address - </p>
        <a href="#">Use precise location - </a>
        <a href="#">Learn more</a><br>
        <a href="#">Help</a>
        <a href="#">Send feedback</a>
        <a href="#">Privacy</a>
        <a href="#">Terms</a>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "SearchResult",
    data() {
      return {
        totalResult: this.$route.params.totalResult,
        inputvalue: this.$route.params.inputvalue,
        pageNow: 1,
        totalPage: Math.ceil(this.$route.params.totalResult.total / 10),
        myData: [],
        now: -1,
        show: true,
      }
    },
    beforeRouteLeave(to, from, next) {
      next(false);
    },
    methods: {
      search(index) {
        if (index == 0) {
          index = 1;
        }
        let postData = this.$qs.stringify({
          query: this.inputvalue,
          pageNow: index,
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
            this.totalResult = response.data;
            this.totalPage = Math.ceil(this.totalResult.total / 10);
            this.pageNow = index;
            console.log("pageNow: " + this.pageNow);
          })
          .catch(error => {
            console.log(error);
          });
      },
      get: function (ev) {
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
          this.myData = response.data;
        }).catch(error => {
          console.log(error);
        });
      },
      selectDown: function () {
        this.now++;
        //到达最后一个时，再按下就回到第一个
        if (this.now == this.myData.length) {
          this.now = 0;
        }
        this.inputvalue = this.myData[this.now];
      },
      selectUp: function () {
        this.now--;
        //同上
        if (this.now == -1) {
          this.now = this.myData.length - 1;
        }
        this.inputvalue = this.myData[this.now];
      },
      selectHover: function (index) {
        this.now = index
      },
      selectClick: function (index) {
        this.inputvalue = this.myData[index];
        this.search(0);
      },
      clearInput: function () {
        this.myData = [];
        this.inputvalue = "";
      },
    },
  }
</script>

<style scoped>

  @import "../assets/css/reset2.css";
  @import "../assets/css/results.css";

  .search-select {
    position: absolute;
    width: 600px;
    box-sizing: border-box;
    z-index: 999;
  }

  .search-select li {
    border: 1px solid #d4d4d4;;
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

  .search-select ul {
    margin: 0;
    text-align: left;
  }

  .isNowPage {
    color: black !important;
  }
</style>
