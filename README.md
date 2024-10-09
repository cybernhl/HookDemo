# Xposed框架Hook Demo实例教程
## _Hyb的小站，分享生活，分享互联网及软件IT技术_

[![N|Solid](https://cravatar.cn/avatar/1ec3d052eead08a6ddb090dfefb3c64b?s=96&d=mm&r=g)](hhttps://hyb.life/archives/60)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://hyb.life/)

Xposed是GitHub上rovo89大神设计的一个针对Android平台的动态劫持项目，通过替换/system/bin/app_process程序控制zygote进程，使得app_process在启动过程中会加载XposedBridge.jar这个jar包，从而完成对Zygote进程及其创建的Dalvik虚拟机的劫持。

Xposed模块本质上也是一个Android App程序，完成一个Xposed模块需要有以下几步：

- 创建一个Android App程序，并让Xposed知道这个程序是一个Xposed模块
- App程序中导入Xposed API的Jar包
- 编写具体Hook代码1
- 添加入口，使Xposed能知道这个Xposed模块的Hook类入口

## 一、创建App程序，并让Xposed认识你

