<template>
  <div class="radar-chart-container">
    <div ref="chartRef" class="chart"></div>
    <div v-if="chartError" class="chart-error">{{ chartError }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  personalData: {
    type: Object,
    default: null
  },
  jobData: {
    type: Object,
    default: null
  }
})

const chartRef = ref(null)
const chartError = ref('')
let chartInstance = null

const dimensions = [
  { name: '技术深度', key: 'technicalDepth' },
  { name: '项目经验', key: 'projectExp' },
  { name: '学历背景', key: 'educationBg' },
  { name: '稳定性', key: 'stability' },
  { name: '沟通协作', key: 'communication' }
]

const initChart = () => {
  if (!chartRef.value) return
  
  try {
    if (chartInstance) {
      chartInstance.dispose()
    }
    
    chartInstance = echarts.init(chartRef.value)
    const option = buildOption()
    chartInstance.setOption(option)
    chartError.value = ''
  } catch (error) {
    console.error('雷达图初始化失败', error)
    chartError.value = '图表加载失败，请刷新重试'
  }
}

const buildOption = () => {
  const radarIndicator = dimensions.map(d => ({
    name: d.name,
    max: 100
  }))
  
  const seriesData = []
  
  if (props.personalData) {
    const personalValues = dimensions.map(d => props.personalData[d.key] || 0)
    seriesData.push({
      value: personalValues,
      name: '个人能力',
      lineStyle: { color: '#1677FF' },
      areaStyle: { color: 'rgba(22, 119, 255, 0.2)' },
      itemStyle: { color: '#1677FF' }
    })
  }
  
  if (props.jobData) {
    const jobValues = dimensions.map(d => props.jobData[d.key] || 0)
    seriesData.push({
      value: jobValues,
      name: '岗位要求',
      lineStyle: { 
        color: '#FAAD14',
        type: 'dashed'
      },
      areaStyle: { color: 'rgba(250, 173, 20, 0.1)' },
      itemStyle: { color: '#FAAD14' }
    })
  }
  
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      data: seriesData.map(s => s.name),
      bottom: 0,
      textStyle: { color: '#333' }
    },
    radar: {
      indicator: radarIndicator,
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: '#1E3A8A',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: ['#e8ecf1', '#d0d7de', '#b8c4ce', '#a0adc6', '#8898ae']
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['rgba(232, 236, 241, 0.5)', 'rgba(208, 215, 222, 0.5)']
        }
      },
      axisLine: {
        lineStyle: { color: '#d0d7de' }
      }
    },
    series: [{
      type: 'radar',
      data: seriesData
    }]
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

watch(() => [props.personalData, props.jobData], () => {
  initChart()
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.radar-chart-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.chart {
  width: 100%;
  height: 100%;
  min-height: 300px;
}

.chart-error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #ff4d4f;
  font-size: 14px;
  text-align: center;
}
</style>