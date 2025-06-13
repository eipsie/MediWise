<template>
  <div class="knowledge-detail-container">
    <div class="knowledge-detail">
      <el-card v-if="detail" class="detail-card" :body-style="{ padding: 0 }">
        <div class="card-inner">
          <div class="header">
            <h2 class="title">{{ detail.title }}</h2>
            <el-tag class="tag-type" :type="detail.entryType === 'DISEASE' ? 'danger' : 'success'">
              {{ detail.entryType === 'DISEASE' ? '疾病' : '药品' }}
            </el-tag>
            <el-tag class="tag-category" type="warning">{{ detail.category }}</el-tag>
          </div>
          
          <el-divider />
          
          <div class="info-section">
            <el-descriptions v-if="detail.entryType === 'DISEASE'" :column="2" border size="large">
              <el-descriptions-item label="疾病名称">
                {{ detail.diseaseName || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="疾病编码">
                {{ detail.diseaseCode || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatTime(detail.createTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="更新时间">
                {{ formatTime(detail.updateTime) }}
              </el-descriptions-item>
            </el-descriptions>

            <div v-else class="drug-info">
              <el-table
                :data="[{
                  name: detail.drugName || '-',
                  createTime: formatTime(detail.createTime),
                  updateTime: formatTime(detail.updateTime)
                }]"
                :show-header="true"
                border
                style="width: 100%"
              >
                <el-table-column prop="name" label="药品名称" width="320" />
                <el-table-column prop="createTime" label="创建时间" />
                <el-table-column prop="updateTime" label="更新时间" />
              </el-table>
            </div>
          </div>
          
          <div class="content-section">
            <div class="section-block" v-if="detail.description">
              <h3 class="section-title">描述</h3>
              <div class="section-content">{{ detail.description }}</div>
            </div>
            
            <div class="section-block" v-if="detail.symptoms">
              <h3 class="section-title">相关症状</h3>
              <div class="section-content">{{ detail.symptoms }}</div>
            </div>
            
            <div class="section-block" v-if="detail.treatmentGuide">
              <h3 class="section-title">治疗指南</h3>
              <div class="section-content">{{ detail.treatmentGuide }}</div>
            </div>
          </div>
          
          <div class="action-bar">
            <el-button type="primary" @click="goBack">返回列表</el-button>
          </div>
        </div>
      </el-card>
      <el-empty v-else description="未找到知识条目" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getKnowledgeDetail } from '../../api/knowledge'

const route = useRoute()
const router = useRouter()
const detail = ref(null)

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  try {
    const res = await getKnowledgeDetail(id)
    if (res.data && res.data.data) {
      detail.value = res.data.data
    } else {
      detail.value = null
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    detail.value = null
  }
}

const goBack = () => {
  router.back()
}

function formatTime(val) {
  if (!val) return '-'
  // 兼容字符串和Date对象
  const d = typeof val === 'string' ? new Date(val) : val
  if (isNaN(d.getTime())) return '-'
  return d.toLocaleString('zh-CN', { hour12: false })
}

onMounted(fetchDetail)
</script>

<style scoped>
.knowledge-detail-container {
  width: 100%;
  box-sizing: border-box;
  padding: 0;
  margin: 0;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.knowledge-detail {
  width: 95%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 0 30px 0;
}

.detail-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.card-inner {
  padding: 24px 32px;
  background: #fff;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-right: 16px;
}

.tag-type {
  margin-right: 8px;
}

.info-section {
  margin: 20px 0;
}

.drug-info {
  margin-bottom: 20px;
}

.content-section {
  margin-top: 24px;
}

.section-block {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 12px;
}

.section-content {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  background: #f8fafc;
  border-radius: 6px;
  padding: 16px;
  min-height: 32px;
}

.action-bar {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
}
</style> 