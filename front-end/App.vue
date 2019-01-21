<template>
  <div id="app" :class="{activated: isshow, inactivated: !isshow}">
    <el-button size="small" type="info" plain @click="gotoBool" v-show="!isshow">前往搜索引擎</el-button>
    <el-row
      type="flex"
      class="row-bg"
      justify="center"
      style="margin-bottom:5px; padding-bottom:0px; margin-top:10px;"
    >
      <el-col :span="8">
        <el-form :inline="isshow" @submit.native.prevent>
          <el-form-item>
            <el-input v-model="inputvalue" placeholder="可先尝试搜索[french]，再根据文档内短语随意查询" @keyup.enter.native="search"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              icon="el-icon-search"
              type="primary"
              :style="{activated: isshow}"
              v-on:click="search"
            >搜索</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <hr v-show="isshow">
    <transition name="slide-fade">
      <file-list :documents="documents" :query="query" v-if="isshow"/>
    </transition>
    <div style="margin-top: 50px;" v-if="!isshow">
      <el-row type="flex" class="row-bg" justify="center">
        <el-col :md="1" :sm="2" :xs="3">
          <el-tooltip class="item" effect="dark" content="下载说明及报告" placement="bottom">
            <el-button size="mini" type="info" plain @click="download" icon="el-icon-download"></el-button>
          </el-tooltip>
        </el-col>
        <!--<el-col :md="3" :sm="5" :xs="7">-->
          <!--请至-->
          <!--<a target="_blank" href="https://github.com/christopherwilsrks/Info-Retrieval/tree/VSM-Retrieval">GitHub</a>查看源码-->
        <!--</el-col>-->
      </el-row>
    </div>
  </div>
</template>

<script>
  import FileList from "./components/FileList";

  export default {
    name: "App",
    components: {
      fileList: FileList
    },
    data() {
      return {
        isshow: false,
        inputvalue: "",
        documents: [],
        query: ""
      };
    },
    methods: {
      gotoBool() {
        window.location.href = "http://www.inforetrieval.xyz/nookle";
      },
      download() {
        window.open("/api/4/download");
      },
      search() {
        let postData = this.$qs.stringify({
          query: this.inputvalue
        });
        console.log(this.inputvalue);
        this.$axios({
          method: "post",
          url: "/api/4/search",
          data: postData,
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          }
        })
          .then(response => {
            console.log(response.data);
            this.documents = response.data;
            this.query = this.inputvalue;
            this.isshow = true;
          })
          .catch(error => {
            console.log(error);
          });
      }
    }
  };
</script>

<style>
  #app {
    font-family: "Avenir", Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }

  .activated {
    margin-top: 30px;
  }

  .inactivated {
    margin-top: 18em;
  }
  .slide-fade-enter-active {
    transition: all 1s ease;
  }
  .slide-fade-leave-active {
    transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
  }
  .slide-fade-enter, .slide-fade-leave-to
    /* .slide-fade-leave-active for below version 2.1.8 */ {
    transform: translateX(10px);
    opacity: 0;
  }
</style>
