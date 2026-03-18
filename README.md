# MedicineTimeTask

一个轻量化的安卓用药提醒 App：

- Kotlin + Jetpack Compose 页面
- Room 本地存储
- AlarmManager 定时提醒
- 未处理后的重复提醒
- 今日、计划、记录三个页面

## 当前能力

- 新建提醒计划
- 支持 `每天` 和 `每 N 天` 提醒
- 支持设置再次提醒间隔和最大重复次数
- 通知支持 `已服药`、`跳过`
- 开机后自动恢复提醒

## 项目结构

- `app/src/main/java/com/medicinetimetask/ui` 页面与导航
- `app/src/main/java/com/medicinetimetask/data` Room 和 Repository
- `app/src/main/java/com/medicinetimetask/reminder` 闹钟、通知、Receiver

## 运行方式

### Android Studio

1. 用 Android Studio 打开项目根目录 `medicine-timetask`
2. 等待 Gradle Sync 完成
3. 连接安卓手机或启动模拟器
4. 运行 `app`

### 命令行

Windows:

```bat
gradlew.bat assembleDebug
```

macOS / Linux / Git Bash:

```bash
./gradlew assembleDebug
```

## 环境要求

- Android Studio Hedgehog 以上版本均可
- JDK 17 或 21
- 首次构建需要联网下载 Gradle 和 Android 依赖

## 当前已知事项

- 这个仓库已经补上 `Gradle Wrapper`
- Android 13+ 需要手动授予通知权限
- 不同国产 ROM 对精确提醒和后台限制较多，真机需要再做稳定性验证

## 下一步建议

- 补 `稍后提醒` 通知动作
- 增加编辑/删除计划
- 增加通知权限与精确闹钟权限引导
- 优化首页信息卡片与记录状态文案
