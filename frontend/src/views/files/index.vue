<template>
  <div class="files-page">
    <div class="page-header">
      <h2>文档空间</h2>
      <el-upload
        :action="'/api/files/upload'"
        :headers="{ Authorization: 'Bearer ' + userStore.token }"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
      >
        <el-button type="primary"><el-icon><Upload /></el-icon> 上传文件</el-button>
      </el-upload>
    </div>

    <el-card>
      <el-table :data="sessions" stripe>
        <el-table-column prop="fileName" label="文件名" min-width="200">
          <template #default="{ row }">
            <el-link :href="'/view/' + row.sessionId" target="_blank" type="primary">
              {{ row.fileName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.fileType?.toUpperCase() }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewerType" label="渲染器" width="120" />
        <el-table-column prop="mode" label="模式" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.mode === 'edit' ? 'success' : 'info'" size="small">
              {{ row.mode === 'edit' ? '编辑' : '预览' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appId" label="应用" width="140" />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const sessions = ref<any[]>([])

async function fetchSessions() {
  const res = await request.get('/api/dashboard/sessions')
  if (res.data.code === 200) sessions.value = res.data.data
}

function handleUploadSuccess() {
  ElMessage.success('上传成功')
  fetchSessions()
}

onMounted(fetchSessions)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  font-size: 18px;
  color: #333;
}
</style>
