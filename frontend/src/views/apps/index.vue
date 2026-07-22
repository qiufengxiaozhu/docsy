<template>
  <div class="apps-page">
    <div class="page-header">
      <h2>应用管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon> 注册应用
      </el-button>
    </div>

    <el-table :data="apps" stripe border>
      <el-table-column prop="appId" label="应用ID" width="180" />
      <el-table-column prop="appName" label="应用名称" width="160" />
      <el-table-column prop="appSecret" label="密钥" min-width="200">
        <template #default="{ row }">
          <span class="secret-text">{{ showSecret[row.id] ? row.appSecret : '••••••••••••' }}</span>
          <el-button link size="small" @click="showSecret[row.id] = !showSecret[row.id]">
            {{ showSecret[row.id] ? '隐藏' : '显示' }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isActive === 1 ? 'success' : 'danger'" size="small">
            {{ row.isActive === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" align="center">
        <template #default="{ row }">
          <el-button link size="small" @click="toggleActive(row.id)">
            {{ row.isActive === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button link size="small" type="warning" @click="resetSecret(row.id)">重置密钥</el-button>
          <el-popconfirm title="确定删除此应用？" @confirm="deleteApp(row.id)">
            <template #reference>
              <el-button link size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建应用对话框 -->
    <el-dialog v-model="showCreateDialog" title="注册应用" width="450px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="应用名称" required>
          <el-input v-model="createForm.appName" placeholder="如：OA系统" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" placeholder="应用描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createApp">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const apps = ref<any[]>([])
const showSecret = ref<Record<number, boolean>>({})
const showCreateDialog = ref(false)
const createForm = reactive({ appName: '', description: '' })

async function fetchApps() {
  const res = await request.get('/api/apps')
  if (res.data.code === 200) apps.value = res.data.data
}

async function createApp() {
  const res = await request.post('/api/apps', createForm)
  if (res.data.code === 200) {
    ElMessage.success('应用创建成功')
    showCreateDialog.value = false
    createForm.appName = ''
    createForm.description = ''
    fetchApps()
  }
}

async function toggleActive(id: number) {
  await request.put(`/api/apps/${id}/toggle`)
  fetchApps()
}

async function resetSecret(id: number) {
  await request.put(`/api/apps/${id}/reset-secret`)
  ElMessage.success('密钥已重置')
  fetchApps()
}

async function deleteApp(id: number) {
  await request.delete(`/api/apps/${id}`)
  ElMessage.success('已删除')
  fetchApps()
}

onMounted(fetchApps)
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
.secret-text {
  font-family: monospace;
  font-size: 12px;
  margin-right: 8px;
}
</style>
