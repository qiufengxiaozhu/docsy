<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><Monitor /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeSessions || 0 }}</div>
              <div class="stat-label">活跃会话</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><Connection /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalApps || 0 }}</div>
              <div class="stat-label">注册应用</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalSessions || 0 }}</div>
              <div class="stat-label">累计会话</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 支持格式说明 -->
    <el-card class="format-card">
      <template #header>
        <span>支持的文件格式</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <h4>📄 Office（预览+编辑）</h4>
          <el-tag v-for="f in ['docx','doc','xlsx','xls','pptx','ppt','pdf']" :key="f" size="small" class="format-tag">{{ f }}</el-tag>
        </el-col>
        <el-col :span="6">
          <h4>🖼️ 图片</h4>
          <el-tag v-for="f in ['png','jpg','gif','bmp','svg','webp']" :key="f" size="small" type="success" class="format-tag">{{ f }}</el-tag>
        </el-col>
        <el-col :span="6">
          <h4>🎬 音视频</h4>
          <el-tag v-for="f in ['mp4','webm','mp3','wav','ogg','flac']" :key="f" size="small" type="warning" class="format-tag">{{ f }}</el-tag>
        </el-col>
        <el-col :span="6">
          <h4>📝 代码/文本</h4>
          <el-tag v-for="f in ['md','txt','xml','html','json','yaml']" :key="f" size="small" type="info" class="format-tag">{{ f }}</el-tag>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref<any>({})

onMounted(async () => {
  try {
    const res = await request.get('/api/dashboard/stats')
    if (res.data.code === 200) {
      stats.value = res.data.data
    }
  } catch (e) {
    console.error('获取统计失败', e)
  }
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-card {
  border-radius: 8px;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  font-size: 40px;
}
.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}
.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.format-card {
  border-radius: 8px;
}
.format-card h4 {
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
}
.format-tag {
  margin: 2px 4px 2px 0;
}
</style>
