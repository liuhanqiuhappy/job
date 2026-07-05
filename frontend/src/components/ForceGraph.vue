<template>
  <div class="force-graph-container">
    <div v-if="isEmpty" class="empty-hint">暂无技能数据</div>
    <div v-else>
      <div ref="chartRef" class="chart"></div>
      <div v-if="chartError" class="chart-error">{{ chartError }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  personalSkills: {
    type: Array,
    default: () => []
  },
  jobSkills: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
const chartError = ref('')
let chartInstance = null

const isEmpty = computed(() => {
  return (!props.personalSkills || props.personalSkills.length === 0) && 
         (!props.jobSkills || props.jobSkills.length === 0)
})

const initChart = () => {
  if (!chartRef.value || isEmpty.value) return
  
  try {
    if (chartInstance) {
      chartInstance.dispose()
    }
    
    chartInstance = echarts.init(chartRef.value)
    const option = buildOption()
    chartInstance.setOption(option)
    chartError.value = ''
  } catch (error) {
    console.error('力导向图初始化失败', error)
    chartError.value = '图表加载失败，请刷新重试'
  }
}

const buildOption = () => {
  const personalSet = new Set(props.personalSkills || [])
  const jobSet = new Set(props.jobSkills || [])
  
  const intersection = [...personalSet].filter(skill => jobSet.has(skill))
  const personalOnly = [...personalSet].filter(skill => !jobSet.has(skill))
  const jobOnly = [...jobSet].filter(skill => !personalSet.has(skill))
  
  const nodes = []
  const links = []
  
  const addNode = (name, category, symbol, itemStyle) => {
    nodes.push({
      name: name,
      category: category,
      symbol: symbol,
      itemStyle: itemStyle
    })
  }
  
  intersection.forEach(skill => {
    addNode(skill, 2, 'circle', { 
      color: '#52C41A',
      borderColor: '#389E0D',
      borderWidth: 3
    })
  })
  
  personalOnly.forEach(skill => {
    addNode(skill, 0, 'circle', { color: '#1677FF' })
  })
  
  jobOnly.forEach(skill => {
    addNode(skill, 1, 'diamond', { color: '#FF4D4F' })
  })
  
  personalOnly.forEach(pSkill => {
    intersection.forEach(iSkill => {
      links.push({
        source: pSkill,
        target: iSkill,
        lineStyle: { color: '#91caff', width: 1 }
      })
    })
  })
  
  jobOnly.forEach(jSkill => {
    intersection.forEach(iSkill => {
      links.push({
        source: jSkill,
        target: iSkill,
        lineStyle: { color: '#ffccc7', width: 1 }
      })
    })
  })
  
  intersection.forEach((s1, i) => {
    intersection.slice(i + 1).forEach(s2 => {
      links.push({
        source: s1,
        target: s2,
        lineStyle: { color: '#b7eb8f', width: 1.5 }
      })
    })
  })
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (params.dataType === 'node') {
          let type = ''
          if (params.data.category === 0) type = '个人技能'
          else if (params.data.category === 1) type = '职位要求'
          else if (params.data.category === 2) type = '交集技能'
          return `<div style="padding: 4px;">
            <div style="font-weight: bold; color: #1E3A8A;">${params.name}</div>
            <div style="color: #666; font-size: 12px;">${type}</div>
          </div>`
        }
        return ''
      }
    },
    legend: {
      data: ['个人技能', '职位要求', '交集技能'],
      bottom: 0,
      textStyle: { color: '#333' },
      itemWidth: 14,
      itemHeight: 14
    },
    series: [{
      type: 'graph',
      layout: 'force',
      data: nodes,
      links: links,
      categories: [
        { name: '个人技能', itemStyle: { color: '#1677FF' } },
        { name: '职位要求', itemStyle: { color: '#FF4D4F' } },
        { name: '交集技能', itemStyle: { color: '#52C41A' } }
      ],
      roam: true,
      draggable: true,
      label: {
        show: true,
        position: 'bottom',
        fontSize: 12,
        color: '#333'
      },
      force: {
        repulsion: 300,
        gravity: 0.1,
        edgeLength: 100,
        layoutAnimation: true
      },
      emphasis: {
        focus: 'adjacency',
        lineStyle: {
          width: 2
        }
      }
    }]
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

watch(() => [props.personalSkills, props.jobSkills], () => {
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
.force-graph-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.chart {
  width: 100%;
  height: 100%;
  min-height: 300px;
}

.empty-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #999;
  font-size: 14px;
  text-align: center;
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