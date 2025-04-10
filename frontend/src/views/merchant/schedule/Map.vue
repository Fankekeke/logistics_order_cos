<template>
  <a-drawer
    placement="right"
    width="100%"
    :closable="false"
    :visible="orderShow"
    destroyOnClose
    wrapClassName="aa"
    :getContainer="false"
  >
    <div style="width: 100%">
      <a-icon type="arrow-left" style="position: absolute;z-index: 999;color: red;font-size: 20px;margin: 15px" @click="home"/>
      <a-row style="height:100vh;font-family: SimHei">
        <a-col :span="15" style="height: 100%;">
          <div id="areas" style="width: 100%;height: 100%;box-shadow: 3px 3px 3px rgba(0, 0, 0, .2);background:#ec9e3c;color:#fff"></div>
        </a-col>
        <a-col :span="9" style="height: 100%;box-shadow: 3px 3px 3px rgba(0, 0, 0, .2);color:#fff">
          <div>
            <div class="scenicInfo" style="height: 100vh; overflow-y: auto;padding-left: 5px;overflow-x: hidden">
              <a-carousel autoplay style="height: 300px;" v-if="merchantInfo.images !== undefined && merchantInfo.images !== ''">
                <div style="width: 100%;height: 300px" v-for="(item, index) in merchantInfo.images.split(',')" :key="index">
                  <img :src="'http://127.0.0.1:9527/imagesWeb/' + item" style="width: 100%;height: 100%">
                </div>
              </a-carousel>
              <a-card :title="merchantInfo.name" :bordered="false">
              </a-card>
              <div style="font-size: 12px;font-family: SimHei;color: #404040;margin-top: 15px">
                <div style="font-size: 12px;font-family: SimHei" v-if="orderInfo !== null">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">订单信息</span></a-col>
                    <a-col :span="8"><b>订单编号：</b>
                      {{ orderInfo.code }}
                    </a-col>
                    <a-col :span="8"><b>订单价格：</b>
                      {{ orderInfo.orderPrice ? orderInfo.orderPrice + '元' : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>折后价格：</b>
                      {{ orderInfo.afterOrderPrice ? orderInfo.afterOrderPrice + '元' : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="8"><b>会员折扣：</b>
                      {{ orderInfo.discount }} 元
                    </a-col>
                    <a-col :span="8"><b>订单积分：</b>
                      {{ orderInfo.integral }}
                    </a-col>
                    <a-col :span="8"><b>订单状态：</b>
                      <span v-if="orderInfo.status === '-3'" style="color: pink">已退货</span>
                      <span v-if="orderInfo.status === '-2'" style="color: red">退货中</span>
                      <span v-if="orderInfo.status === '-1'" style="color: red">等待审核</span>
                      <span v-if="orderInfo.status === '0'" style="color: red">未支付</span>
                      <span v-if="orderInfo.status === '1'" style="color: blue">已支付</span>
                      <span v-if="orderInfo.status === '2'" style="color: orange">配送中</span>
                      <span v-if="orderInfo.status === '3'" style="color: green">已收货</span>
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;" v-if="orderInfo.type === '1'">
                    <a-col :span="8"><b>订单类型：</b>
                      <span v-if="orderInfo.type === '0'">店内购买</span>
                      <span v-if="orderInfo.type === '1'">配送</span>
                    </a-col>
                    <a-col :span="8"><b>下单时间：</b>
                      {{ orderInfo.createDate }}
                    </a-col>
                    <a-col :span="8"><b>公里数：</b>
                      {{ orderInfo.kilometre }}公里
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;" v-if="orderInfo.type === '1'">
                    <a-col :span="8"><b>配送价格：</b>
                      {{ orderInfo.distributionPrice ? orderInfo.distributionPrice + '元' : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>支付时间：</b>
                      {{ orderInfo.payDate ? orderInfo.payDate : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>送达时间：</b>
                      {{ orderInfo.serviceDate }}
                    </a-col>
                  </a-row>
                  <br/>
                </div>
                <br/>
                <div style="font-size: 12px;font-family: SimHei" v-if="userInfo !== null">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">用户信息</span></a-col>
                    <a-col :span="8"><b>会员编号：</b>
                      {{ userInfo.code }}
                    </a-col>
                    <a-col :span="8"><b>用户姓名：</b>
                      {{ userInfo.name ? userInfo.name : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>邮箱地址：</b>
                      {{ userInfo.mail ? userInfo.mail : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="8"><b>联系电话：</b>
                      {{ userInfo.phone }}
                    </a-col>
                  </a-row>
                  <br/>
                </div>
                <br/>
                <div style="font-size: 12px;font-family: SimHei" v-if="merchantInfo !== null">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">商家信息</span></a-col>
                    <a-col :span="8"><b>商家编号：</b>
                      {{ merchantInfo.code }}
                    </a-col>
                    <a-col :span="16"><b>商家名称：</b>
                      {{ merchantInfo.name ? merchantInfo.name : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="8"><b>联系方式：</b>
                      {{ merchantInfo.phone }}
                    </a-col>
                    <a-col :span="8"><b>负责人：</b>
                      {{ merchantInfo.principal }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="24"><b>地 址：</b>
                      {{ merchantInfo.address ? merchantInfo.address : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                </div>
                <br/>
                <br/>
                <div style="font-size: 12px;font-family: SimHei" v-if="vehicleInfo !== null">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">车辆信息</span></a-col>
                    <a-col :span="8"><b>车辆编号：</b>
                      {{ vehicleInfo.vehicleNo }}
                    </a-col>
                    <a-col :span="8"><b>车牌号：</b>
                      {{ vehicleInfo.vehicleNumber ? vehicleInfo.vehicleNumber : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>车辆颜色：</b>
                      {{ vehicleInfo.vehicleColor ? vehicleInfo.vehicleColor : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="8"><b>排放标准：</b>
                      {{ vehicleInfo.emissionStandard ? vehicleInfo.emissionStandard : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>发动机号码：</b>
                      {{ vehicleInfo.engineNo ? vehicleInfo.engineNo : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>联系电话：</b>
                      {{ vehicleInfo.phone ? vehicleInfo.phone : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col :span="8"><b>车辆名称：</b>
                      {{ vehicleInfo.name ? vehicleInfo.name : '- -' }}
                    </a-col>
                    <a-col :span="8"><b>燃料类型：</b>
                      <span v-if="vehicleInfo.fuelType == 1" style="color: green">燃油</span>
                      <span v-if="vehicleInfo.fuelType == 2" style="color: green">柴油</span>
                      <span v-if="vehicleInfo.fuelType == 3" style="color: green">油电混动</span>
                      <span v-if="vehicleInfo.fuelType == 4" style="color: green">电能</span>
                    </a-col>
                    <br/>
                    <br/>
                    <a-col :span="24"><b>备注：</b>
                      {{ vehicleInfo.content }}
                    </a-col>
                  </a-row>
                  <br/>
                  <br/>
                </div>
                <div style="font-size: 12px;font-family: SimHei" v-if="scheduleList.length !== 0">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">配送订单</span></a-col>
                    <a-col style="margin-bottom: 15px">
                      <a-steps :current="scheduleList.length" direction="vertical" progress-dot size="small">
                        <a-step v-for="(item, index) in scheduleList" :key="index" :title="item.address.address">
                          <template slot="subTitle">
                            <span>距离 {{ (item.item.distance / 1000).toFixed(2) }}公里</span>
                          </template>
                          <template slot="description">
                            <p style="margin-bottom: 10px">
                              <a-row :gutter="25">
                                <a-col :span="12">
                                  <a-tag v-if="item.item.status == 1" color="#108ee9">已送达</a-tag>
                                  <a-tag v-if="item.item.status == 0" color="#f50">未配送</a-tag>
                                  <a-tooltip>
                                    <template slot="title">
                                      点击进行道路导航
                                    </template>
                                    <a-icon type="car" theme="twoTone" style="margin-left: 15px" @click="getRoadData(item.item.previousLongitude, item.item.previousLatitude, item.item.currentLongitude, item.item.currentLatitude)"/>
                                  </a-tooltip>
                                </a-col>
                                <a-col :span="12">
                                  <b>收货人：</b>{{ item.address.contactPerson }} - {{ item.address.contactMethod }}
                                </a-col>
                              </a-row>
                            </p>
                            <a-table :columns="columns" :data-source="item.orderItem" :pagination="false"></a-table>
                          </template>
                        </a-step>
                      </a-steps>
                    </a-col>
                  </a-row>
                </div>
                <br/>
                <div style="font-size: 12px;font-family: SimHei" v-if="roadData.length !== 0">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">道路导航</span></a-col>
                    <a-col :span="24" style="height: 450px;overflow-y: auto">
                      <a-skeleton active v-if="loading" />
                      <a-timeline v-if="!loading">
                        <a-timeline-item v-for="(item,index) in roadData" :key="index">
                          <div v-html="item"></div>
                        </a-timeline-item>
                      </a-timeline>
                    </a-col>
                  </a-row>
                  <br/>
                </div>
                <br/>
                <div style="font-size: 12px;font-family: SimHei" v-if="evaluateInfo !== null">
                  <a-row style="padding-left: 24px;padding-right: 24px;">
                    <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">订单评价</span></a-col>
                    <a-col :span="8"><b>评价分数：</b>
                      <a-rate :default-value="evaluateInfo.score" disabled />
                    </a-col>
                    <a-col :span="8"><b>评价内容：</b>
                      <a-tooltip>
                        <template slot="title">
                          {{ evaluateInfo.content}}
                        </template>
                        {{ evaluateInfo.content.slice(0, 8) }} ...
                      </a-tooltip>
                    </a-col>
                    <a-col :span="8"><b>评价时间：</b>
                      {{ evaluateInfo.createDate ? evaluateInfo.createDate : '- -' }}
                    </a-col>
                  </a-row>
                  <br/>
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </a-drawer>
</template>

<script>
import baiduMap from '@/utils/map/baiduMap'
export default {
  name: 'Map',
  props: {
    orderShow: {
      type: Boolean,
      default: false
    },
    orderData: {
      type: Object
    }
  },
  data () {
    return {
      userInfo: null,
      orderInfo: null,
      merchantInfo: null,
      vehicleInfo: [],
      scheduleList: [],
      loading: false,
      orderItemInfo: [],
      addressInfo: null,
      staffInfo: null,
      evaluateInfo: null,
      staffList: [],
      communityRent: 0,
      rentShow: false,
      communityShow: false,
      communityData: null,
      checkedList: ['Apple', 'Orange'],
      indeterminate: true,
      checkAll: false,
      plainOptions: ['Apple', 'Pear', 'Orange'],
      visible: false,
      rentList: [],
      communityList: [],
      community: null,
      nowPoint: null,
      roadData: [],
      checkLoading: false,
      echartsShow: false,
      getShop: null,
      putShop: null,
      series: [{
        name: '得分',
        data: []
      }],
      chartOptions: {
        chart: {
          height: 380,
          type: 'radar'
        },
        title: {
          text: ''
        },
        xaxis: {
          categories: ['交付得分', '价格得分', '质量得分', '准时得分', '服务得分', '综合得分']
        }
      }
    }
  },
  computed: {
    columns () {
      return [{
        title: '商品名称',
        dataIndex: 'dishesName'
      }, {
        title: '图片',
        dataIndex: 'images',
        customRender: (text, record, index) => {
          if (!record.images) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.images.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '购买数量',
        dataIndex: 'amount'
      }, {
        title: '单价',
        dataIndex: 'unitPrice'
      }, {
        title: '总价格',
        dataIndex: 'totalPrice',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }]
    }
  },
  watch: {
    'orderShow': function (value) {
      if (value) {
        this.dataInit(this.orderData.scheduleCode)
      }
    }
  },
  methods: {
    dataInit (scheduleCode) {
      this.$get(`/cos/schedule-info/querySchedule`, {scheduleCode}).then((r) => {
        // this.userInfo = r.data.user
        // this.orderInfo = r.data.order
        this.merchantInfo = r.data.merchant
        this.vehicleInfo = r.data.vehicle
        this.scheduleList = r.data.schedule
        console.log(this.scheduleList)
        // this.orderItemInfo = r.data.orderItem
        // this.addressInfo = r.data.address
        // this.staffInfo = r.data.staff
        // this.evaluateInfo = r.data.evaluate
        setTimeout(() => {
          baiduMap.initMap('areas')
          this.getLocal()
          this.scheduleList.forEach((itemOrder, index) => {
            let item = itemOrder.item
            if (index === 0) {
              this.navigation(item.previousLongitude, item.previousLatitude, item.currentLongitude, item.currentLatitude)
            } else {
              this.navigation(item.currentLongitude, item.currentLatitude, item.nextLongitude, item.nextLatitude)
            }
          })
        }, 200)
      })
    },
    navigation (startLongitude, startLatitude, endLongitude, endLatitude) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let driving = new BMap.DrivingRoute(baiduMap.rMap(), {renderOptions: {map: baiduMap.rMap(), autoViewport: true}})
      // eslint-disable-next-line no-undef
      driving.search(new BMap.Point(startLongitude, startLatitude), new BMap.Point(endLongitude, endLatitude))
      // this.getRoadData()
    },
    getRoadData (startLongitude, startLatitude, endLongitude, endLatitude) {
      this.loading = true
      let options = {
        onSearchComplete: results => {
          // eslint-disable-next-line eqeqeq,no-undef
          if (driving.getStatus() == BMAP_STATUS_SUCCESS) {
            // 获取第一条方案
            let plan = results.getPlan(0)
            // 获取方案的驾车线路
            // eslint-disable-next-line no-unused-vars
            let route = plan.getRoute(0)
            // 获取每个关键步骤,并输出到页面
            let s = []
            for (let j = 0; j < plan.getNumRoutes(); j++) {
              let route = plan.getRoute(j)
              for (let i = 0; i < route.getNumSteps(); i++) {
                let step = route.getStep(i)
                s.push((i + 1) + '. ' + step.getDescription())
              }
            }
            this.roadData = s
            this.loading = false
          }
        }
      }
      // eslint-disable-next-line no-undef
      let driving = new BMap.DrivingRoute(baiduMap.rMap(), options)
      // eslint-disable-next-line no-undef
      let start = new BMap.Point(startLongitude, startLatitude)
      let end = null
      end = new BMap.Point(endLongitude, endLatitude)

      // eslint-disable-next-line no-undef
      driving.search(start, end)
    },
    checkEvaluate (score) {
      let data = [score.deliverScore, score.priceScore, score.qualityScore, score.scheduleScore, score.serviceScore, score.overScore, 100]
      this.series[0].data = data
    },
    home () {
      this.$emit('close')
    },
    gisOnChange (e) {
      let data = null
      switch (e.target.value) {
        case '1':
          data = this.getShop
          break
        case '2':
          data = this.putShop
          break
      }
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let driving = new BMap.DrivingRoute(baiduMap.rMap(), {renderOptions: {map: baiduMap.rMap(), autoViewport: true}})
      // eslint-disable-next-line no-undef
      driving.search(new BMap.Point(this.nowPoint.lng, this.nowPoint.lat), new BMap.Point(data.longitude, data.latitude))
    },
    getLocal () {
      // eslint-disable-next-line no-undef
      let geolocation = new BMap.Geolocation()
      geolocation.getCurrentPosition(r => {
        this.nowPoint = r.point
      }, {enableHighAccuracy: true})
    }
  }
}
</script>

<style scoped>
  >>> .ant-drawer-body {
    padding: 0 !important;
  }
  >>> .ant-card-meta-title {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-card-meta-description {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-divider-with-text-left {
    margin: 0;
  }

  >>> .ant-card-head-title {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-card-extra {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-radio-button-wrapper {
    border-radius: 0;
  }

  >>> .ant-steps-item-content {
    width: 93% !important;
  }

  >>> .ant-steps-item-title {
    margin-bottom: 15px;
  }
</style>
