# razor
from https://github.com/cobub/razor

##修改内容：
1. activityInfo，eventInfo里面添加了appkey,deviceId完全是业务方需要，正常可以删除。因为后台支持gzip压缩，所以由此导致的流量消耗可以忽略
2. 事件埋点和页面埋点由原本的文件读写改为数据库记录，更加方便。改良建议：clientData表可以改为key-value，所需要的字段作为一条记录记录在数据库里，更加方便读写
3. 其它的小改动
