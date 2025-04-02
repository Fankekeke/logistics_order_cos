<template>
  <a-drawer
    title="问题检查"
    :maskClosable="false"
    width=1150
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="purchaseAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form" layout="vertical">
      <a-row :gutter="10">
        <a-divider orientation="left">
          <span style="font-size: 13px">基本信息</span>
        </a-divider>
        <a-col :span="8">
          <a-form-item label='选择药店'>
            <a-select disabled @change="pharmacyCheck" v-decorator="[
              'pharmacyId',
              { rules: [{ required: true, message: '请输入采购药店!' }] }
              ]">
              <a-select-option :value="item.id" v-for="(item, index) in pharmacyList" :key="index">{{ item.name }}</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='选择采购单'>
            <a-select @change="queryDrugByPurchaseCode" v-decorator="[
              'purchaseCode',
              { rules: [{ required: true, message: '请输入选择采购单!' }] }
              ]">
              <a-select-option :value="item.code" v-for="(item, index) in purchaseList" :key="index">{{ item.purchaser }} - {{ item.code }}</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='采购人' v-bind="formItemLayout">
            <a-input v-decorator="[
            'createBy',
            { rules: [{ required: true, message: '请输入采购人!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <br/>
        <br/>
        <a-col :span="24">
          <a-form-item label='问题描述' v-bind="formItemLayout">
            <a-textarea :rows="6" v-decorator="[
            'content',
             { rules: [{ required: true, message: '请输入问题描述!' }] }
            ]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <br/>
      <a-row :gutter="10">
        <a-divider orientation="left">
          <span style="font-size: 13px">药品信息</span>
        </a-divider>
        <a-col :span="24">
          <a-table :columns="columns" :data-source="dataList" :pagination="false" :rowSelection="{selectedRowKeys: checkedKeys, onChange: handleRowClick, type: 'radio'}">
            <template slot="nameShow" slot-scope="text, record">
              <a-select style="width: 100%" @change="handleChange($event, record)">
                <a-select-option v-for="(item, index) in drugList" :key="index" :value="item.id">{{ item.name }}</a-select-option>
              </a-select>
            </template>
            <template slot="rawMaterialShow" slot-scope="text, record">
              <span>{{ record.rawMaterial }}</span>
            </template>
            <template slot="portionShow" slot-scope="text, record">
              <span>{{ record.portion }}</span>
            </template>
            <template slot="endDateShow" slot-scope="text, record">
              <a-date-picker style="width: 100%" @change="endDateChange($event, record)" />
            </template>
<!--            <template slot="reserveShow" slot-scope="text, record">-->
<!--              <a-input-number v-model="record.reserve" :min="1" :step="1"/>-->
<!--            </template>-->
            <template slot="priceShow" slot-scope="text, record">
              <span>{{ record.receiveUnitPrice }}元</span>
            </template>
          </a-table>
<!--          <a-button @click="dataAdd" type="primary" ghost style="margin-top: 10px;width: 100%">-->
<!--            新增物品-->
<!--          </a-button>-->
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
import baiduMap from '@/utils/map/baiduMap'
import drawerMap from '@/utils/map/searchmap/drawerMap'
import {mapState} from 'vuex'
import moment from 'moment'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'purchaseAdd',
  props: {
    purchaseAddVisiable: {
      default: false
    }
  },
  components: {
    drawerMap
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.purchaseAddVisiable
      },
      set: function () {
      }
    },
    columns () {
      return [{
        title: '药品名称',
        ellipsis: true,
        dataIndex: 'drugName'
      }, {
        title: '数量',
        dataIndex: 'reserve'
      }, {
        title: '功效',
        dataIndex: 'rawMaterial',
        ellipsis: true
      }, {
        title: '药品图片',
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
        title: '保质期截至',
        dataIndex: 'endDate'
      }, {
        title: '进货单价',
        dataIndex: 'receiveUnitPrice'
      }]
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      checkedKeys: [],
      previewVisible: false,
      previewImage: '',
      localPoint: {},
      stayAddress: '',
      drugId: null,
      childrenDrawer: false,
      pharmacyList: [],
      supplierList: [],
      pharmacyInfo: null,
      dataList: [],
      purchaseList: [],
      drugList: [],
      merchantId: ''
    }
  },
  mounted () {
    this.getSupplier()
    this.$get('/cos/staff-info/selectMerchantByStaffId', {
      userId: this.currentUser.userId
    }).then(res => {
      this.merchantId = res.data.data.userId
      this.getPharmacy()
    })
  },
  methods: {
    handleRowClick (e, row) {
      this.checkedKeys = e
      this.drugId = row[0].drugId
    },
    queryDrugByPurchaseCode (purchaseCode) {
      this.$get('/cos/pharmacy-inventory/queryDrugByPurchaseCode/', {purchaseCode}).then((r) => {
        this.dataList = r.data.data
      })
    },
    getPharmacy () {
      this.$get('/cos/merchant-info/list').then((r) => {
        this.pharmacyList = r.data.data
        this.pharmacyList.forEach(e => {
          if (e.userId === this.merchantId) {
            this.form.setFieldsValue({pharmacyId: e.id})
            this.pharmacyInfo = e
            this.queryPurchaseByMerchant(e.id)
          }
        })
      })
    },
    handleChange (value, record) {
      if (value) {
        this.drugList.forEach(e => {
          if (e.id === value) {
            record.brand = e.brand
            record.rawMaterial = e.rawMaterial
            record.portion = e.portion
            record.receiveUnitPrice = e.receiveUnitPrice
            record.drugId = e.id
            console.log(record)
          }
        })
      }
    },
    endDateChange (value, record) {
      console.log(value)
      if (value) {
        record.endDate = moment(value).format('YYYY-MM-DD')
        console.log(record.endDate)
      }
    },
    pharmacyCheck (value) {
      if (value) {
        this.pharmacyList.forEach(e => {
          if (e.id === value) {
            this.pharmacyInfo = e
            this.queryPurchaseByMerchant(e.id)
          }
        })
      }
    },
    dataAdd () {
      this.dataList.push({drugId: null, reserve: 1, rawMaterial: '', portion: '', receiveUnitPrice: '', endDate: ''})
    },
    queryPurchaseByMerchant (merchantId) {
      this.$get('/cos/purchase-info/queryPurchaseByMerchant/', {merchantId}).then((r) => {
        this.purchaseList = r.data.data
      })
    },
    getDrug (merchantId) {
      this.$get('/cos/dishes-info/selectDishesByMerchant/drug/' + merchantId).then((r) => {
        this.drugList = r.data.data
      })
    },
    getSupplier () {
      this.$get('/cos/enterprise-info/list').then((r) => {
        this.supplierList = r.data.data
      })
    },
    handlerClosed (localPoint) {
      this.childrenDrawer = false
      if (localPoint !== null && localPoint !== undefined) {
        this.localPoint = localPoint
        console.log(this.localPoint)
        let address = baiduMap.getAddress(localPoint)
        address.getLocation(localPoint, (rs) => {
          if (rs != null) {
            if (rs.address !== undefined && rs.address.length !== 0) {
              this.stayAddress = rs.address
            }
            if (rs.surroundingPois !== undefined) {
              if (rs.surroundingPois.address !== undefined && rs.surroundingPois.address.length !== 0) {
                this.stayAddress = rs.surroundingPois.address
              }
            }
            let obj = {}
            obj['address'] = this.stayAddress
            obj['longitude'] = localPoint.lng
            obj['latitude'] = localPoint.lat
            this.form.setFieldsValue(obj)
          }
        })
      }
    },
    showChildrenDrawer () {
      this.childrenDrawer = true
    },
    onChildrenDrawerClose () {
      this.childrenDrawer = false
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    reset () {
      this.drugId = null
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      if (this.dataList.length === 0) {
        this.$message.warning('请添加采购信息')
        return false
      }
      if (this.drugId === null) {
        this.$message.warning('请选择问题药品')
        return false
      }
      this.form.validateFields((err, values) => {
        values.drugId = this.drugId
        if (!err) {
          this.loading = true
          this.$post('/cos/examine-info', {
            ...values
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
