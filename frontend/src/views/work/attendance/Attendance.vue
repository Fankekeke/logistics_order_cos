<template>
  <a-card :bordered="false" class="card-area">
    <a-button type="primary" @click="checkAttendance" :disabled="checkCount != null && checkCount.putTakeDate != null && checkCount.outTakeDate != null">{{ checkWork() }}</a-button>
    <a-calendar>
      <ul slot="dateCellRender" slot-scope="value" class="events">
        <li v-for="item in getListData(value)" :key="item.content">
          <a-badge :status="item.type" :text="item.content" />
        </li>
      </ul>
    </a-calendar>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {RangeDate},
  data () {
    return {
      checkCount: null,
      courseInfo: [],
      attendanceList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.dataInit()
    this.queryTodayCheck()
  },
  methods: {
    checkWork () {
      if (this.checkCount == null) {
        return '上班打卡'
      }
      if (this.checkCount != null && this.checkCount.outTakeDate == null) {
        return '下班打卡'
      }
      if (this.checkCount != null && this.checkCount.outTakeDate != null) {
        return '已打卡'
      }
    },
    queryTodayCheck () {
      this.$get(`/cos/attendance-info/queryTodayCheck`, {userId: this.currentUser.userId}).then((r) => {
        console.log(r.data.data)
        this.checkCount = r.data.data
      })
    },
    checkAttendance () {
      if (this.checkCount == null) {
        this.$post(`/cos/attendance-info`, {staffId: this.currentUser.userId}).then((r) => {
          this.$message.success('上班打卡成功')
          this.dataInit()
          this.queryTodayCheck()
        })
        return false
      }
      if (this.checkCount != null && this.checkCount.outTakeDate == null) {
        this.$put(`/cos/attendance-info`, this.checkCount).then((r) => {
          this.$message.success('下班打卡成功')
          this.dataInit()
          this.queryTodayCheck()
        })
      }
    },
    dataInit () {
      this.$get(`/cos/attendance-info/queryAttendanceRecordByUserId`, {userId: this.currentUser.userId}).then((r) => {
        this.courseInfo = r.data.data
      })
    },
    isDuringDate (beginDateStr, endDateStr, curDataStr) {
      let curDate = new Date(curDataStr)
      let beginDate = new Date(beginDateStr)
      let endDate = new Date(endDateStr)
      if (curDate >= beginDate && curDate <= endDate) {
        return true
      }
      return false
    },
    getListData (value) {
      let listData = []
      this.courseInfo.forEach(item => {
        if ((moment(value).format('YYYY-MM-DD')) === (moment(item.putTakeDate).format('YYYY-MM-DD'))) {
          listData.push({type: 'success', content: '上班- ' + item.putTakeDate + ' 下班-' + (item.outTakeDate ? item.outTakeDate : '未打卡')})
        }
      })
      return listData || []
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
