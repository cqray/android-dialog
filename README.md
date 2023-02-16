# android-dialog

### 介绍
简单的对话框封装

### 引入

#### 第一步
在根build.gradle文件中添加如下代码
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
#### 第二步
添加依赖
```
implementation 'com.github.cqray:android-dialog:0.5.4'
```

### 如何使用

[消息类对话框 BaseDialog](./library/src/main/java/cn/cqray/android/dialog/BaseDialog.java)

[底部消息类对话框 BaseDialog](./library/src/main/java/cn/cqray/android/dialog/BaseDialog.java)

**自定义实现可继承以下基类。**

[基础类对话框 BaseDialog](./library/src/main/java/cn/cqray/android/dialog/BaseDialog.java)

[Alter类对话框 AlterDialog](./library/src/main/java/cn/cqray/android/dialog/AlterDialog.java)

[底部Alter类对话框 BottomAlterDialog](./library/src/main/java/cn/cqray/android/dialog/BottomAlterDialog.java)

更多使用请阅读源码。


最终选择Dialog而不是使用DialogFragment，是为了在Compose中也方便使用