# 背景
实现从文件导入数据功能，并对文件做相应的分析
# 流程图
![流程图](http://oo0ow3409.bkt.clouddn.com/%E6%96%87%E4%BB%B6%E6%89%B9%E9%87%8F%E5%AF%BC%E5%85%A5%E6%B5%81%E7%A8%8B%E5%9B%BE.png)
# 使用
## 运行jar包方式
### 下载mvnDemo-0.1-release.jar
### 参照以下方式创建属性文件
```text
#导入的文件路径
filePath=C:\\Users\\hugh\\Downloads\\数据
#数据库驱动
jdbcDriver=com.mysql.jdbc.Driver
#数据库位置
dbUrl=jdbc:mysql://localhost:32773/wifidb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
#数据库用户名
dbUserName=root
#数据库密码
dbPassword=0001
```
### 运行命令
```shell
jar mvnDemo-0.1-release.jar 属性文件路径
```
## idea运行方式
### idea拉取代码
### 切换分支为release
### 导入pom
### 将\src\main\resource 右键mark为resource文件夹
### 修改 \src\main\resource\application.properties文件
### 运行 Application.main()
### 查看分析结果
在原文件夹下的analysisData文件夹下
# 分析结果
* 分析结果在 原路径下的analysisData文件夹下
# 优势
* 支持对文件的多次重试操作，即支持幂等操作，包括导入数据库和分析结果
* 分析出的文件按小时有时间的排序
* 支持Log的输出，可修改log4j2的配置文件修改输出log等级
* 采用连接池的方式保持数据库连接
# 其他
多线程处理方式待开发完成
