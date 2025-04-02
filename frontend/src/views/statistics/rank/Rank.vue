<template>
  <a-card :bordered="false" class="card-area">
    <div style="background:#ECECEC; padding:30px;margin-top: 30px;margin-bottom: 30px">
      <a-row :gutter="30">
        <a-col :span="6" :key="index">
          <a-month-picker @change="onChange" style="width: 100%" placeholder="Select month" />
        </a-col>
      </a-row>
    </div>
    <a-row :gutter="20">
      <a-col :span="24">
        <a-card hoverable :bordered="false" style="width: 100%">
          <a-skeleton active v-if="loading" />
          <apexchart v-if="!loading" type="bar" height="300" :options="chartOptions1" :series="series1"></apexchart>
        </a-card>
      </a-col>
      <br/>
      <br/>
      <a-col :span="24">
        <a-card hoverable :bordered="false" style="width: 100%">
          <a-skeleton active v-if="loading" />
          <apexchart v-if="!loading" type="bar" height="300" :options="chartOptions2" :series="series2"></apexchart>
        </a-card>
      </a-col>
    </a-row>
  </a-card>
</template>

<script>
import moment from "moment";

export default {
  name: 'Work',
  data () {
    return {
      workStatusList: [],
      loading: false,
      series1: [],
      chartOptions1: {
        chart: {
          type: 'bar',
          height: 300
        },
        title: {
          text: '数量统计',
          align: 'left'
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: ''
          }
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 单'
            }
          }
        }
      },
      series2: [],
      chartOptions2: {
        chart: {
          type: 'bar',
          height: 300
        },
        title: {
          text: '收益统计',
          align: 'left'
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: ''
          }
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + ' 元'
            }
          }
        }
      }
    }
  },
  mounted () {
    this.selectOrderDays(moment(new Date()).format('YYYY-MM'))
  },
  methods: {
    onChange (date, dateString) {
      this.selectOrderDays(dateString)
    },
    selectOrderDays (date) {
      date = date + '-01'
      this.loading = true
      this.$get(`/cos/merchant-info/selectStatisticsByMonth`, {
        date
      }).then((r) => {
        this.series1 = []
        this.series2 = []
        if (r.data.num.length !== 0) {
          this.chartOptions1.xaxis.categories = Array.from(r.data.num, ({name}) => name)
          let values = []
          let itemData = { name: '销量', data: Array.from(r.data.num, ({value}) => value) }
          values.push(itemData)
          this.series1 = values
        }
        if (r.data.price.length !== 0) {
          this.chartOptions2.xaxis.categories = Array.from(r.data.price, ({name}) => name)
          let values = []
          let itemData = { name: '销量金额', data: Array.from(r.data.price, ({value}) => value) }
          values.push(itemData)
          this.series2 = values
        }
        setTimeout(() => {
          this.loading = false
        }, 200)
      })
    }
  }
}
</script>

<style scoped>

</style>
