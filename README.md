### 基于SpringBoot + Vue的库房订单物流调度系统.

#### 安装环境

JAVA 环境 

Node.js环境 [https://nodejs.org/en/] 选择14.17

Yarn 打开cmd， 输入npm install -g yarn !!!必须安装完毕nodejs

Mysql 数据库 [https://blog.csdn.net/qq_40303031/article/details/88935262] 一定要把账户和密码记住

redis

Idea 编译器 [https://blog.csdn.net/weixin_44505194/article/details/104452880]

WebStorm OR VScode 编译器 [https://www.jianshu.com/p/d63b5bae9dff]

#### 采用技术及功能

后端：SpringBoot、MybatisPlus、MySQL、Redis、
前端：Vue、Apex、Antd、Axios

平台前端：vue(框架) + vuex(全局缓存) + rue-router(路由) + axios(请求插件) + apex(图表)  + antd-ui(ui组件)

平台后台：springboot(框架) + redis(缓存中间件) + shiro(权限中间件) + mybatisplus(orm) + restful风格接口 + mysql(数据库)

开发环境：windows10 or windows7 ， vscode or webstorm ， idea + lambok


#### 前台启动方式
安装所需文件 yarn install 
运行 yarn run dev

#### 默认后台账户密码
[管理员]
admin
1234qwer

###### 管理员：
用户地址 、公告管理 、商品管理 、订单评价 、会员积分 、商家管理 、商家会员 、订单管理 、用户管理 、采购记录 、商品类型 、供应商管理 、商品采购 、库存预警 、采购物流 、商家库存 、车辆管理 、车次调度 、问题检查 、保质期预警、数据统计。

###### 商家：
商家注册登录、密码修改、商品管理 、订单评价 、商家会员 、订单管理 、商家信息 、会员积分 、采购记录 、商品采购 、库存预警 、采购物流 、商家库存 、问题检查 、保质期预警 、车辆管理  、车次调度。

###### 用户：
账户注册登录、密码修改、个人信息 、地址管理 、选购下单 、我的订单 、订单评价 、在线支付 、商品收藏。

#### 项目截图
暂无

|  |  |
|---------------------|---------------------|
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/2fe5d67a-2067-428a-a2be-2084092405b8.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/51d51f57-be0d-4b19-9597-d2591b10c337.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/0c66307b-b16b-4fcc-abe7-5c5fc441c720.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/32f1afeb-7dd3-4786-9b42-9e67a425f404.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/00c601e2-844d-4f92-b3eb-ffb77ad85b12.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/25f01d17-9094-4a21-8ce6-ce462c170c7e.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/feff565a-a94e-4e87-bc70-e3c29f04beaa.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/9fc4fc1c-2e62-4e04-8e30-06dbf42de89b.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/f1be105b-e74b-44a1-846d-3952e6be6531.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8c75b6ff-063a-4f9a-9ec5-b57903faa043.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e740b288-3427-4822-8c7d-b2dec9387577.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8c9bf041-cad4-4b0f-bed1-15828f9635f8.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/c5c8d8ca-2a3b-4055-ad6b-2a01ce35f695.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/6f834822-b3bd-4288-98a6-d1a4a1ebfbb9.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/c5acba66-8809-4033-8061-f143d832c045.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/6e981a5c-2c80-404d-8d02-b99e6b0e2874.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/10262052-96d8-47ac-915f-b2aefc203828.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/5f1faeb7-9515-44a2-945b-d43d2c5e83b5.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/7655b993-71ea-4183-87c6-4276026c6259.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/5b3dbb54-c06d-4dc1-bfe2-0885baeac38f.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/239f93c9-afe9-4968-b4fa-049f6035167f.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/4b4a7e0c-34b2-4751-9bea-087c67f70426.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/89b3c414-2d6e-43b5-a418-9abc9395001d.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/work/936e9baf53eb9a217af4f89c616dc19.png) |

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.黑奴价格

> 项目部署调试不好包退！功能逻辑没讲明白包退！

#### 其它资源

[2025年-答辩顺利通过-客户评价🍜](https://berserker287.github.io/2025/06/18/2025%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2024年-答辩顺利通过-客户评价👻](https://berserker287.github.io/2024/06/06/2024%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2023年-答辩顺利通过-客户评价🐢](https://berserker287.github.io/2023/06/14/2023%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2022年-答辩通过率100%-客户评价🐣](https://berserker287.github.io/2022/05/25/%E9%A1%B9%E7%9B%AE%E4%BA%A4%E6%98%93%E8%AE%B0%E5%BD%95/)

[毕业答辩导师提问的高频问题](https://berserker287.github.io/2023/06/13/%E6%AF%95%E4%B8%9A%E7%AD%94%E8%BE%A9%E5%AF%BC%E5%B8%88%E6%8F%90%E9%97%AE%E7%9A%84%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98/)

[50个高频答辩问题-技术篇](https://berserker287.github.io/2023/06/13/50%E4%B8%AA%E9%AB%98%E9%A2%91%E7%AD%94%E8%BE%A9%E9%97%AE%E9%A2%98-%E6%8A%80%E6%9C%AF%E7%AF%87/)

[计算机毕设答辩时都会问到哪些问题？](https://www.zhihu.com/question/31020988)

[计算机专业毕业答辩小tips](https://zhuanlan.zhihu.com/p/145911029)

#### 接JAVAWEB毕设，纯原创，价格公道，诚信第一

`网站建设、小程序、H5、APP、各种系统 选题+开题报告+任务书+程序定制+安装调试+项目讲解+论文+答辩PPT`

More info: [悲伤的橘子树](https://berserker287.github.io/)
