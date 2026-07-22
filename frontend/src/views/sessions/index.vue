<template>
  <div class="sessions-page">
    <div class="page-header">
      <h2>会话监控</h2>
      <el-button @click="fetchSessions"><el-icon><Refresh /></el-icon> 刷新</el-button>
    </div>

    <el-card>
      <el-table :data="sessions" stripe>
        <el-table-column prop="sessionId" label="会话ID" width="140">
          <template #default="{ row }">
            <span class="mono-text">{{ row.sessionId?.substring(0, 12) }}...</span>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="180" />
        <el-table-column prop="viewerType" label="渲染器" width="120" />
        <el-table-column prop="mode" label="模式" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.mode === 'edit' ? 'success' : 'info'" size="small">
              {{ row.mode === 'edit' ? '编辑' : '预览' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="appId" label="应用" width="140" />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column prop="expiresAt" label="过期时间" width="170" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-link :href="'/view/' + row.sessionId" target="_blank" type="primary" size="small">
              打开
            </el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const sessions = ref<any[]>([])

async function fetchSessions() {
  const res = await request.get('/api/dashboard/sessions')
  if (res.data.code === 200) sessions.value = res.data.data
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
.mono-text {
  font-family: monospace;
  font-size: 12px;
}
</style>
