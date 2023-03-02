<template>
  <div class="app-container">
    <div style="margin-bottom: 10px;">
      <el-button
        @click="dialogVisible = true"
        type="primary"
        size="mini"
        icon="el-icon-download"
      >
        导入Excel
      </el-button>

      <el-button
        @click="exportData"
        type="primary"
        size="mini"
        icon="el-icon-upload2"
      >
        导出Excel
      </el-button>
    </div>

    <el-dialog title="数据字典导入" :visible.sync="dialogVisible" width="30%">
      <el-form>
        <el-form-item label="请选择Excel文件">
          <el-upload
            :auto-upload="true"
            :multiple="false"
            :limit="1"
            :on-exceed="fileUploadExceed"
            :on-success="fileUploadSuccess"
            :on-error="fileUploadError"
            :action="BASE_API + '/admin/core/dict/import'"
            name="file"
            accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          >
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
      </div>
    </el-dialog>

    <el-table :data="list" border row-key="id" lazy :load="load">
      <el-table-column label="名称" align="left">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="编码">
        <template slot-scope="row">
          <span>{{ row.dictCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="值" align="left">
        <template slot-scope="scope">
          <span>{{ scope.row.value }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import dictApi from '@/api/core/dict'
export default {
  data() {
    return {
      dialogVisible: false, // 对话框是否显示
      BASE_API: process.env.VUE_APP_BASE_API, // 获取后端地址
      list: []
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    fetchData() {
      dictApi.listByParentId(1).then(response => {
        this.list = response.data.list
      })
    },
    fileUploadExceed() {
      this.$message.warning('只能上传一个文件')
    },
    fileUploadSuccess(response) {
      if (response.code === 0) {
        this.$message.success('数据导入成功')
        this.dialogVisible = false
      } else {
        this.$message.error(response.message)
      }
    },

    fileUploadError() {
      this.$message.error('数据导入失败')
    },

    exportData() {
      window.location.href = this.BASE_API + '/admin/core/dict/export'
    },

    load(tree, treeNode, resolve) {
      dictApi.listByParentId(tree.id).then(response => {
        resolve(response.data.list)
      })
    }
  }
}
</script>
