<template>
  <div class="fonts-page">
    <div class="page-header">
      <h2>字体管理</h2>
      <el-upload
        action="/api/fonts/upload"
        :headers="{ Authorization: 'Bearer ' + userStore.token }"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
        accept=".ttf,.otf,.woff,.woff2"
      >
        <el-button type="primary"><el-icon><Upload /></el-icon> 上传字体</el-button>
      </el-upload>
    </div>

    <el-card>
      <el-table :data="fonts" stripe>
        <el-table-column prop="fontName" label="字体名称" min-width="200" />
        <el-table-column prop="fileName" label="文件名" width="250" />
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ (row.fileSize / 1024).toFixed(1) }} KB
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.source === 'system' ? 'info' : 'success'" size="small">
              {{ row.source === 'system' ? '系统' : '上传' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="添加时间" width="170" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确定删除此字体？" @confirm="deleteFont(row.id)">
              <template #reference>
                <el-button link size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
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
const fonts = ref<any[]>([])

async function fetchFonts() {
  const res = await request.get('/api/fonts')
  if (res.data.code === 200) fonts.value = res.data.data
}

function handleUploadSuccess() {
  ElMessage.success('字体上传成功')
  fetchFonts()
}

async function deleteFont(id: number) {
  await request.delete(`/api/fonts/${id}`)
  ElMessage.success('已删除')
  fetchFonts()
}

onMounted(fetchFonts)
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
