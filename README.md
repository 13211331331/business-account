
在throw-account目录下：mvn install

在core里面生产core/target 里面生产original-core-1.0-SNAPSHOT.jar（original-core-1.0-SNAPSHOT.jar可以改名成任何xxx.jar）
修改original-core-1.0-SNAPSHOT.jar同目录下的config.properties文件， 配置数据库
把查询的sql语句保存到xxx.sql文件  将这个xxx.xql放到original-core-1.0-SNAPSHOT.jar同目录

直接运行 java -jar original-core-1.0-SNAPSHOT.jar
