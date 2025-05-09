<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="订单编号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.code"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="用户名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.userName"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="商家名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.merchantName"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
<!--        <a-button type="primary" ghost @click="add">添加订单</a-button>-->
        <a-button @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="file-search" @click="orderViewOpen(record)" title="详 情"></a-icon>
          <a-icon v-if="record.status == -2" type="warning" @click="returnAudit(record)" title="退 货" style="margin-left: 15px"></a-icon>
<!--          <a-icon v-if="record.status == 1 && record.type == 0" type="check" @click="orderComplete(record)" title="订单完成" style="margin-left: 15px"></a-icon>-->
          <a-icon v-if="record.addressId != null && (record.status == 1) && record.scheduleCode == null" type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="orderAuditOpen(record)" title="修 改" style="margin-left: 15px"></a-icon>
          <a-icon v-if="record.type == 1" type="cluster" @click="orderMapOpen(record)" title="地 图" style="margin-left: 15px"></a-icon>
        </template>
      </a-table>
    </div>
    <order-audit
      @close="handleorderAuditViewClose"
      @success="handleorderAuditViewSuccess"
      :orderShow="orderAuditView.visiable"
      :orderData="orderAuditView.data">
    </order-audit>
    <order-status
      @close="handleorderStatusViewClose"
      @success="handleorderStatusViewSuccess"
      :orderStatusShow="orderStatusView.visiable"
      :orderStatusData="orderStatusView.data">
    </order-status>
    <order-view
      @close="handleorderViewClose"
      @success="handleorderViewSuccess"
      :orderShow="orderView.visiable"
      :orderData="orderView.data">
    </order-view>
    <order-add
      @close="handleorderAddClose"
      @success="handleorderAddSuccess"
      :orderAddShow="orderAdd.visiable">
    </order-add>
    <MapView
      @close="handleorderMapViewClose"
      :orderShow="orderMapView.visiable"
      :orderData="orderMapView.data">
    </MapView>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
import OrderAdd from './OrderAdd'
import OrderAudit from './OrderAudit'
import OrderView from './OrderView'
import OrderStatus from './OrderStatus.vue'
import MapView from '../../manage/map/Map.vue'
moment.locale('zh-cn')

export default {
  name: 'order',
  components: {OrderView, OrderAudit, RangeDate, OrderStatus, OrderAdd, MapView},
  data () {
    return {
      advanced: false,
      orderAdd: {
        visiable: false
      },
      orderEdit: {
        visiable: false
      },
      orderMapView: {
        visiable: false,
        data: null
      },
      orderView: {
        visiable: false,
        data: null
      },
      orderStatusView: {
        visiable: false,
        data: null
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      orderAuditView: {
        visiable: false,
        data: null
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '订单编号',
        dataIndex: 'code',
        ellipsis: true
      }, {
        title: '下单用户',
        dataIndex: 'userName',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '用户头像',
        dataIndex: 'userImages',
        customRender: (text, record, index) => {
          if (!record.userImages) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.userImages.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.userImages.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '所属商家',
        dataIndex: 'merchantName',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '商家图片',
        dataIndex: 'merchantImages',
        customRender: (text, record, index) => {
          if (!record.merchantImages) return <a-avatar shape="square" icon="user" />
          return <a-popover>
            <template slot="content">
              <a-avatar shape="square" size={132} icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.merchantImages.split(',')[0] } />
            </template>
            <a-avatar shape="square" icon="user" src={ 'http://127.0.0.1:9527/imagesWeb/' + record.merchantImages.split(',')[0] } />
          </a-popover>
        }
      }, {
        title: '订单价格（元）',
        dataIndex: 'orderPrice',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text + '元'
          } else {
            return '- -'
          }
        }
      }, {
        title: '折后价格（元）',
        dataIndex: 'afterOrderPrice',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text + '元'
          } else {
            return '- -'
          }
        }
      }, {
        title: '订单状态',
        dataIndex: 'status',
        customRender: (text, row, index) => {
          switch (text) {
            case '-3':
              return <a-tag>已退货</a-tag>
            case '-2':
              return <a-tag>退货中</a-tag>
            case '-1':
              return <a-tag color="pink">等待审核</a-tag>
            case '0':
              return <a-tag color="red">未支付</a-tag>
            case '1':
              return <a-tag>已支付</a-tag>
            case '2':
              return <a-tag>配送中</a-tag>
            case '3':
              return <a-tag>已收货</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '订单类型',
        dataIndex: 'type',
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag>店内购买</a-tag>
            case '1':
              return <a-tag>配送</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '下单时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    orderComplete (row) {
      this.$get(`/cos/order-info/audit`, {
        'orderCode': row.code,
        'status': 3
      }).then((r) => {
        this.$message.success('订单完成')
        this.fetch()
      })
    },
    orderMapOpen (row) {
      this.orderMapView.data = row
      this.orderMapView.visiable = true
    },
    handleorderMapViewClose () {
      this.orderMapView.visiable = false
    },
    orderStatusOpen (row) {
      this.orderStatusView.data = row
      this.orderStatusView.visiable = true
    },
    returnAudit (record) {
      let that = this
      this.$confirm({
        title: '确定审核退货当前订单?',
        content: '当您点击确定按钮后，此订单商品会回退到库存中',
        centered: true,
        onOk () {
          that.$get('/cos/order-info/returnAudit', {orderCode: record.code}).then((r) => {
            that.$message.success('退货审核成功')
            that.fetch()
          })
        },
        onCancel () {
        }
      })
    },
    orderAuditOpen (row) {
      this.orderAuditView.data = row
      this.orderAuditView.visiable = true
    },
    orderViewOpen (row) {
      this.orderView.data = row
      this.orderView.visiable = true
    },
    handleorderViewClose () {
      this.orderView.visiable = false
    },
    handleorderViewSuccess () {
      this.orderView.visiable = false
      this.$message.success('审核成功')
      this.fetch()
    },
    handleorderStatusViewClose () {
      this.orderStatusView.visiable = false
    },
    handleorderStatusViewSuccess () {
      this.orderStatusView.visiable = false
      this.$message.success('修改成功')
      this.fetch()
    },
    handleorderAuditViewClose () {
      this.orderAuditView.visiable = false
    },
    handleorderAuditViewSuccess () {
      this.orderAuditView.visiable = false
      this.$message.success('设置成功')
      this.fetch()
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.orderAdd.visiable = true
    },
    handleorderAddClose () {
      this.orderAdd.visiable = false
    },
    handleorderAddSuccess () {
      this.orderAdd.visiable = false
      this.$message.success('添加平台订单成功')
      this.search()
    },
    edit (record) {
      this.$refs.orderEdit.setFormValues(record)
      this.orderEdit.visiable = true
    },
    handleorderEditClose () {
      this.orderEdit.visiable = false
    },
    handleorderEditSuccess () {
      this.orderEdit.visiable = false
      this.$message.success('修改成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/order-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.status === undefined) {
        delete params.status
      }
      params.merchantId = this.currentUser.userId
      this.$get('/cos/order-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
