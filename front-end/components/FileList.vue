<template>
  <div id="FileList">
    <el-row type="flex" class="row-bg" justify="space-around">
      <el-col :span="6">
        <div>
          <el-card shadow="always" style="height: 100%">
            <el-pagination
              small
              @current-change="handleCurrentChange"
              layout="prev, pager, next"
              :total="documents.length <= 0? 5:documents.length - 1"
              :page-size="pageSize"
            ></el-pagination>
            <el-scrollbar style="height: 100%">
              <div v-for="(entry, index) in documents">
                <el-button
                  v-if="(parseInt(index / pageSize) == pageNum)"
                  :type="curr == index?success:info"
                  round
                  style="margin-top:2px;"
                  @click="searchDoc(entry, index)"
                >Doc {{ entry }}
                </el-button>
              </div>
            </el-scrollbar>
          </el-card>
        </div>
      </el-col>
      <el-col :span="14">
        <div>
          <el-scrollbar style="height: 100%">
            <div shadow="always" style="height:40em">
              <span v-html="passage"></span>
            </div>
          </el-scrollbar>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
    name: "FileList",
    props: ["documents", "query"],
    data() {
      return {
        pageNum: 0,
        info: "",
        success: "success",
        curr: 0,
        passage: "",
        // fullHeight: document.documentElement.clientHeight,
        pageSize: 15,
        // passageStyle: {
        //   height: parseInt((fullHeight - 20) / 1.3) + 'px',
        // },
      };
    },
    // computed: {
    // getpageNum: function(){
    //   this.pageNum = parseInt(this.fullHeight - 200 / 45)
    //   return this.pageNum
    //   }
    // },
    mounted: function () {
      //在这里写试试
      // const that = this
      //   window.onresize = () => {
      //     return (() => {
      //       window.fullHeight = document.documentElement.clientHeight
      //       that.fullHeight = window.fullHeight
      //       that.pageSize = parseInt((that.fullHeight - 200) / 45)
      //     })()
      //   };
      let postData = this.$qs.stringify({
        docID: this.documents[0],
        s: this.query
      });

      this.$axios({
        method: "post",
        url: "/api/4/searchDoc",
        data: postData,
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        }
      })
        .then(response => {
          console.log(response.data);
          this.passage = response.data;
        })
        .catch(error => {
          console.log(error);
        });
    },
    methods: {
      handleCurrentChange(num) {
        this.pageNum = num - 1;
        console.log(this.pageSize);
      },
      searchDoc(docID, index) {
        let postData = this.$qs.stringify({
          docID: parseInt(docID),
          s: this.query,
        });

        this.$axios({
          method: "post",
          url: "/api/4/searchDoc",
          data: postData,
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          }
        })
          .then(response => {
            console.log(response.data);
            this.passage = response.data;
          })
          .catch(error => {
            console.log(error);
          });
        this.curr = index;
      },
      changeFixed(clientHeight) {
        //动态修改样式
        console.log(clientHeight);
      }
    },
    // watch: {
    //   // 如果 `clientHeight` 发生改变，这个函数就会运行
    //   fullHeight (val) {
    //       if(!this.timer) {
    //         this.fullHeight = val
    //         this.timer = true
    //         let that = this
    //         setTimeout(function (){
    //           that.timer = false
    //         },400)
    //         this.pageSize = parseInt((that.fullHeight - 200) / 45)
    //         console.log(this.fullHeight)
    //       }
    //     }
    //   }
  };
</script>

<style>
  .el-row {
    margin-bottom: 20px;
  }

  .el-col {
    border-radius: 4px;
  }

  .bg-purple-dark {
    background: #99a9bf;
  }

  .bg-purple {
    background: #d3dce6;
  }

  .bg-purple-light {
    background: #e5e9f2;
  }

  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }

  .row-bg {
    padding: 10px 0;
  }
</style>
