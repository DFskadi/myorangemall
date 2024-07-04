外卖项目
http://localhost:8080/backend/page/login/login.html 橘子外卖系统登录页面

http://localhost:8080/backend/index.html  橘子外卖系统主页面

http://localhost:8080/front/page/login.html 橘子外卖平台前台手机号验证页面

主从库一些命令
slave失效

stop slave

set GLOBAL SQL_SLAVE_SKIP_COUNTER=1;

start slave

虚拟机远程连接：防止读取系统二进制文件导致远程连接出错
SET VAGRANT_PREFER_SYSTEM_BIN=0
虚拟机启动命令
vagrant  up 

vagrant ssh development
vagrant ssh production

阿里云短信验证功能前置条件：模板、签名、子密钥
