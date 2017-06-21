# DMusic for Android

![Logo](https://github.com/Dsiner/DMusic/blob/master/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png) 

## Screenshot
![Artboard](https://github.com/Dsiner/DMusic/blob/master/screenshot/screenshot0.png)

## Design
* greenDAO作为数据库，共24张Table：当前播放、本地歌曲、我的收藏、歌单列表，及20张自建歌单（其中歌单列表中pointer字段指向具体的自建歌单）
* 歌曲的扫描：通过系统ContentProvider（MediaStore.Audio.Media.EXTERNAL_CONTENT_URI），获取本机所有媒体文件数据库cursor，遍历cursor过滤出查询文件（过滤条件为路径，即全盘扫描和自定义文件夹扫描），将文件信息存储到数据库
* MusicDBUtil单例为数据库统一管理类，提供通用的"一条语句式"增删改查及跨表单添加（无视表单类型，通过MusicModel提供两种clone()方法完成预先类型转换及克隆）
* MusicControl单例统一管理歌曲播放/暂停、上一首、下一首、删除，列表加载等播放控制
* MusicService持有MusicControl单例，只能通过MusicService获取，MusicService属于Service服务，拥有整个应用的生命周期(启动→退出)
* PlayActivity为当前播放页面，播放/暂定、上一首、下一首等播放控制全部发送广播，MusicService接收这些广播后，通过持有的MusicControl完成播放控制，
MusicControl完成相应播放控制后，发送EventBus(MusicInfoEvent，携带当前正在播放的信息)回传，注册此EventBus的地方有3处（MusicService用于更新通知栏，PlayActivity和首页MainActivity用于更新当前歌曲信息）。
* 本地歌曲4个TAB采用延迟加载提升性能等

## Dependencies
* MVP  -MVP模式
- [greenDAO](https://github.com/greenrobot/greenDAO)  -ORM数据库
- [EventBus](https://github.com/greenrobot/EventBus)  -通信
- [TinyPinyin](https://github.com/promeG/TinyPinyin)  -汉字转拼音
- [RxJava2](https://github.com/ReactiveX/RxJava)  -线程切换
- [RxPermissions](https://github.com/tbruyelle/RxPermissions)  -运行时权限
- [xrv](https://github.com/Dsiner/xRecyclerViewF)  -通用RecyclerView&CommenAdapter
- [SlideLayout](https://github.com/Dsiner/SlideLayout)  -侧滑菜单控件
- [UIUtil](https://github.com/Dsiner/UIUtil)  -歌词lrc、排序SideBar、等ui
* Percent  -百分比布局
* ConstraintLayout  -约束布局
- [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)  -兼容型动画
- [ButterKnife](https://github.com/JakeWharton/butterknife)  -注解
- [Android-Skin-Loader](https://github.com/fengjundev/Android-Skin-Loader)  -换肤

## For developer

* 对于布局xml换肤报错,解决方式：
先点击定位到任意错误处，Alt+Enter——>选择Disable inspection(忽略错误检查)

## About DMusic -中文名(畅听音乐) v1.0.0

##### ----追求速度、简约和安全的本地音乐播放器----

* √便捷的侧滑菜单，歌曲拖曳管理、及更多人性化
* √多彩焕肤，清新有你
* √音乐模式由你开启

##### ----开启畅听之旅----
* 本地音乐：歌曲、歌手、专辑、文件夹分类管理、按字母排序，一触直达
* 自建歌单：随心而动
* 支持歌词：歌词文件需和歌曲文件在同一文件夹内
* 睡眠定时：管理好你的睡眠
* 皮肤：19套肤色可供选择，随心情切换
* 显示模式切换：歌曲操作子菜单可选下拉或弹窗模式、是否显示新建歌单图标？是否管理更多自由，由你而定
* 启动时自动播放：可选
* 心印：定义个性的标签于首页显示（默认“畅音乐，享自由”）
* 自由度操作：首页自建歌单，有置顶、删除两种操作，侧滑显示；自建歌单支持名称、时间及自定义排序、排序页支持按住拖曳排序，侧滑删除、多选加入歌单、批量删除、和撤销操作
* 模式选择：3种模式可供选择，普通模式、极简模式、通知栏模式

## Licence

```txt
Copyright 2017 D

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
