# 题库助手 - 编译指南

## 方法一：Android Studio（推荐）

### 1. 打开项目
```
打开 Android Studio → File → Open → 选择 d:\Android 文件夹 → OK
```

### 2. 等待同步完成
- 首次打开会下载 Gradle 和依赖（约需5-10分钟）
- 底部状态栏显示 "Gradle sync finished" 表示完成

### 3. 编译 APK
```
右侧 Gradle 面板 → app → build → 双击 assembleDebug
```

### 4. 获取 APK
```
d:\Android\app\build\outputs\apk\debug\app-debug.apk
```

---

## 方法二：使用系统 Gradle（需先安装）

### 1. 安装 Gradle
下载并安装 Gradle 8.0+：https://gradle.org/releases/

### 2. 配置环境变量
```
GRADLE_HOME = C:\gradle\gradle-8.0
PATH 添加 %GRADLE_HOME%\bin
```

### 3. 编译
```bash
cd d:\Android
gradle assembleDebug
```

---

## 方法三：使用 Gradle Wrapper（推荐下载后使用）

### 1. 下载 gradle-wrapper.jar
访问：https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar
保存到：`d:\Android\gradle\wrapper\gradle-wrapper.jar`

### 2. 运行编译
```bash
cd d:\Android
.\gradlew.bat assembleDebug
```

---

## 常见问题

### Q: Gradle sync 失败
**A:** 检查网络连接，或在 `File → Settings → Gradle` 中更换 Maven 仓库镜像

### Q: 编译错误 "Unsupported class file major version"
**A:** Java 版本过高，Android Gradle Plugin 8.0 需要 Java 17，建议使用 JDK 17

### Q: 找不到 R 类
**A:** 检查 `build.gradle` 中 `namespace` 是否正确，确认所有资源文件存在

---

## 项目结构
```
d:\Android\
├── app\
│   ├── src\main\
│   │   ├── java\com\example\quizhelper\
│   │   │   ├── model\          # 数据模型
│   │   │   ├── data\           # 数据库
│   │   │   ├── ui\             # 界面
│   │   │   ├── service\        # 服务
│   │   │   └── utils\          # 工具类
│   │   ├── res\                # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle                 # 项目配置
├── settings.gradle
└── gradle.properties
```

---

## 编译输出
- Debug APK: `app\build\outputs\apk\debug\app-debug.apk`
- Release APK: `app\build\outputs\apk\release\app-release-unsigned.apk`

---

## 依赖版本
- Kotlin: 1.8.0
- Android Gradle Plugin: 8.0.0
- compileSdk: 34
- minSdk: 24
- targetSdk: 34
