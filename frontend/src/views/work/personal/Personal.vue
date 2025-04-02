<template>
  <div style="padding: 12px;width: 100%">
    <a-row :gutter="15">
      <a-col :span="8">
        <a-card :bordered="false">
          <a-form :form="form" layout="vertical">
            <a-row :gutter="20">
              <a-col :span="24">
                <a-form-item label='员工姓名' v-bind="formItemLayout">
                  <a-input v-decorator="[
            'name',
            { rules: [{ required: true, message: '请输入名称!' }] }
            ]"/>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label='联系方式' v-bind="formItemLayout">
                  <a-input v-decorator="[
            'phone',
            { rules: [{ required: true, message: '请输入联系方式!' }] }
            ]"/>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label='员工性别' v-bind="formItemLayout">
                  <a-select v-decorator="[
              'sex',
              { rules: [{ required: true, message: '请输入员工性别!' }] }
              ]">
                    <a-select-option value="1">男</a-select-option>
                    <a-select-option value="2">女</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label='照片' v-bind="formItemLayout">
                  <a-upload
                    name="avatar"
                    action="http://127.0.0.1:9527/file/fileUpload/"
                    list-type="picture-card"
                    :file-list="fileList"
                    @preview="handlePreview"
                    @change="picHandleChange"
                  >
                    <div v-if="fileList.length < 1">
                      <a-icon type="plus" />
                      <div class="ant-upload-text">
                        Upload
                      </div>
                    </div>
                  </a-upload>
                  <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                    <img alt="example" style="width: 100%" :src="previewImage" />
                  </a-modal>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
          <a-button @click="handleSubmit" type="primary" :loading="loading">修改</a-button>
          <drawerMap :childrenDrawerShow="childrenDrawer" @handlerClosed="handlerClosed"></drawerMap>
        </a-card>
      </a-col>
      <a-col :span="14">
<!--        <div id="areas" style="width: 100%;height: 700px;box-shadow: 0 0 0 10px white;"></div>-->
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {mapState} from 'vuex'
import baiduMap from '@/utils/map/baiduMap'
import drawerMap from '@/utils/map/searchmap/drawerMap'
import moment from 'moment'
moment.locale('zh-cn')

const plainOptions = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const formItemLayout = {
  labelCol: {span: 24},
  wrapperCol: {span: 24}
}
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'merchant',
  data () {
    return {
      checkedList: [],
      indeterminate: true,
      checkAll: false,
      plainOptions,
      rowId: null,
      mapId: 'area',
      cardShow: false,
      localPoint: {},
      stayAddress: '',
      local: '',
      localData: [],
      formItemLayout,
      childrenDrawer: false,
      form: this.$form.createForm(this),
      userId: '',
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      merchantInfo: null
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  components: {
    drawerMap
  },
  mounted () {
    this.getmerchantByUser()
    baiduMap.initMap('areas')
  },
  methods: {
    moment,
    onChange (checkedList) {
      this.indeterminate = !!checkedList.length && checkedList.length < plainOptions.length
      this.checkAll = checkedList.length === plainOptions.length
      console.log(this.checkedList)
    },
    onCheckAllChange (e) {
      Object.assign(this, {
        checkedList: e.target.checked ? plainOptions : [],
        indeterminate: false,
        checkAll: e.target.checked
      })
    },
    getmerchantByUser () {
      this.$get('/cos/staff-info/queryStaffByUserId', { userId: this.currentUser.userId }).then((r) => {
        this.merchantInfo = r.data.data
        this.rowId = this.merchantInfo.id
        this.setFormValues(r.data.data)
      })
    },
    localhost (scenic) {
      baiduMap.clearOverlays()
      baiduMap.rMap().enableScrollWheelZoom(true)
      // eslint-disable-next-line no-undef
      let point = new BMap.Point(scenic.longitude, scenic.latitude)
      baiduMap.pointAdd(point)
      baiduMap.findPoint(point, 16)
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
    handlerClosed (localPoint) {
      this.childrenDrawer = false
      if (localPoint !== null && localPoint !== undefined) {
        this.localPoint = localPoint
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
            this.form.getFieldDecorator('address')
            let obj = {}
            obj['address'] = this.stayAddress
            this.form.setFieldsValue(obj)
          }
        })
      }
    },
    addPoint (point) {
      this.localPoint = point
    },
    onSearch () {
      let localData = []
      var options = {
        onSearchComplete: (results) => {
          // 判断状态是否正确
          // eslint-disable-next-line eqeqeq,no-undef
          if (local.getStatus() == BMAP_STATUS_SUCCESS) {
            for (var i = 0; i < results.getCurrentNumPois(); i++) {
              if (i === 0) {
                setTimeout(() => {
                  baiduMap.findPoint(results.getPoi(0).point, 15)
                }, 10)
              }
              localData.push(results.getPoi(i))
              if (results.getPoi(i).point !== undefined) {
                baiduMap.localPointAdd(results.getPoi(i))
              }
            }
            this.localData = localData
            this.cardShow = true
          }
        }
      }
      // eslint-disable-next-line no-undef
      var local = new BMap.LocalSearch(baiduMap.rMap(), options)
      local.search(this.local)
    },
    onClose () {
      this.loading = false
      this.form.resetFields()
    },
    showChildrenDrawer () {
      this.childrenDrawer = true
    },
    onChildrenDrawerClose () {
      this.childrenDrawer = false
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    setFormValues ({...user}) {
      this.userId = user.id
      let fields = ['name', 'status', 'sex', 'deptId', 'phone', 'baseSalary']
      Object.keys(user).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(user['images'])
        }
        if (key === 'sex' || key === 'status') {
          user[key] = user[key].toString()
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = user[key]
          this.form.setFieldsValue(obj)
        }
      })
    },
    handleSubmit () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          let user = this.form.getFieldsValue()
          user.images = images.length > 0 ? images.join(',') : null
          user.id = this.rowId
          this.$put('/cos/staff-info', {
            ...user
          }).then((r) => {
            this.loading = false
            this.$message.success('修改成功')
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
