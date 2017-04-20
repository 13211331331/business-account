
* 1 在throw-account目录下：mvn install
* 2 在core里面生产core/target 里面生产core-1.0-SNAPSHOT.jar（core-1.0-SNAPSHOT.jar可以改名成任何xxx.jar）
* 3 修改core-1.0-SNAPSHOT.jar同目录下的config.properties文件， 配置数据库
* 4 把查询的sql语句保存到xxx.sql文件(某某账务明细.sql是示例)  将这个xxx.xql放到core-1.0-SNAPSHOT.jar同目录
* 5 直接运行 java -jar core-1.0-SNAPSHOT.jar
