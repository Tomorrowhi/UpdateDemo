# UpdateFun

[原文在此](https://github.com/hugeterry/UpdateDemo?utm_source=tuicool&utm_medium=referral)
   
   感谢hugeterry，最近用到了fir，所以修改了此Demo，用的话请直接下载本项目，在自己的项目中以module的方式引用，并添加dependencies即可
   
   更新日志：
      
      1、修复使用通知栏下载时，Home键到桌面，下载停止，当在此进入app时，下载开始，系统通知栏的进度条显示错位的问题，
      其实针对此问题，更好的解决方法办法是，当使用系统通知栏方式进行下载时，不使用 UpdateFunGO.onStop(this); 方法
     
      2、当点击立即下载前，会检查缓存目录中是否已经存在了下载好的apk,如果存在，则直接安装，不存在才会下载。
      检查时会检测当前app的版本、apk的版本、网上apk的版本，根据versionCode值决定接下来的业务是下载还是直接安装还是说已经是最新版了

## LICENSE


    Copyright 2016 HugeTerry.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


