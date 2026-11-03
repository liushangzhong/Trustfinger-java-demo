# TrustFinger Java Demo 运行环境说明

本文档面向客户测试演示场景，说明如何运行 TrustFinger Java Demo。

---

## 一、硬件要求

| 项目 | 要求 |
|------|------|
| 指纹仪 | Aratek A700 或其他 TrustFinger SDK V3.3 兼容设备 |
| 接口 | USB（设备支持 USB 连接） |
| 操作系统 | Windows 7 / 10 / 11（64 位） |
| 内存 | 4GB 以上 |

> **注意**：本项目仅支持 Windows 平台，DLL 为 x64 版本，需要 64 位操作系统。

---

## 二、软件环境

### 2.1 JDK 版本

| 项目 | 要求 |
|------|------|
| JDK 版本 | **JDK 8 及以上**（推荐 JDK 8 或 11） |
| 说明 | pom.xml 中 source/target 设为 1.8，JDK 8 及以上均可编译运行 |

**验证 JDK 是否安装**：
```bash
java -version
```
输出示例：
```
java version "1.8.0_202"
Java(TM) SE Runtime Environment (build 1.8.0_202-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.202-b08, mixed mode)
```

### 2.2 Maven 版本

| 项目 | 要求 |
|------|------|
| Maven 版本 | **3.6 及以上** |
| 说明 | 用于编译、打包和依赖管理 |

**验证 Maven 是否安装**：
```bash
mvn -version
```
输出示例：
```
Apache Maven 3.9.9
Maven home: E:\apache-maven-3.9.9
Java version: 1.8.0_202, vendor: Oracle Corporation
```

### 2.3 依赖清单

| 依赖 | 版本 | 用途 |
|------|------|------|
| JNA (jna-platform) | 5.14.0 | Java 调用原生 DLL |
| SLF4J API | 1.7.25 | 日志接口 |
| SLF4J Simple | 1.7.36 | 日志实现（输出到控制台） |
| JUnit | 4.12 | 单元测试（仅测试用） |

> Maven 会自动下载以上依赖，无需手动安装。

---

## 三、项目结构

```
Trustfinger java demo/
├── pom.xml                          # Maven 构建配置
├── src/
│   ├── main/
│   │   ├── java/com/aratek/jna/trustfinger/
│   │   │   ├── TrustFingerDemo.java     # ★ 主入口：完整开发流程示例
│   │   │   ├── AraTrustFingerMainTest.java  # 旧版测试入口
│   │   │   ├── JnaTest.java             # JNA 底层调用测试
│   │   │   ├── library/                 # JNA 接口映射
│   │   │   ├── service/                 # SDK 服务封装
│   │   │   ├── pojo/                    # 数据对象
│   │   │   └── util/                    # 工具类
│   │   └── resources/
│   │       └── windows/x64/            # ★ 内置 DLL（免环境变量）
│   └── test/                           # 单元测试
└── README.md                          # 本文档
```

> **DLL 已内置**：`src/main/resources/windows/x64/` 下包含所有必需的 DLL 文件，运行时自动解压到临时目录加载，**无需设置任何环境变量**。

---

## 四、运行方式

### 方式一：IDEA 运行（推荐开发调试）

1. **导入项目**：
   - 打开 IntelliJ IDEA
   - `File` → `Open` → 选择项目根目录 `pom.xml`
   - 等待 Maven 自动下载依赖

2. **配置运行参数**：
   - `Run` → `Edit Configurations`
   - 点击 `+` → `Application`
   - 设置：
     - **Name**: `TrustFingerDemo`
     - **Main class**: `com.aratek.jna.trustfinger.TrustFingerDemo`
   - **VM options** 填写（解决中文乱码）：
     ```
     -Dfile.encoding=UTF-8
     -Dstdout.encoding=UTF-8
     -Dstderr.encoding=UTF-8
     ```

3. **运行**：点击绿色 ▶ 按钮

### 方式二：命令行运行（推荐客户演示）

```bash
# 1. 进入项目目录
cd "Trustfinger java demo"

# 2. 编译打包（生成带依赖的 fat jar）
mvn clean package

# 3. 运行（使用 assembly 打包的 jar）
java -jar target/trust-finger-for-java-1.0-SNAPSHOT.jar
```

### 方式三：命令行直接运行（不打包）

```bash
# 1. 编译
cd "Trustfinger java demo"
mvn clean compile

# 2. 获取依赖 JAR 路径
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt

# 3. 运行
java -cp "target/classes;$(cat cp.txt)" com.aratek.jna.trustfinger.TrustFingerDemo
```

---

## 五、客户操作手册（jar 包运行后如何测试）

### 5.1 首次使用：安装驱动

1. 找到驱动安装程序：
   ```
   Driver/Aratek Finger Module Drivers.exe
   ```
   （SDK 包内的 Driver 目录）

2. 双击安装驱动

   > **注意**：不装驱动，指纹仪插上 USB 系统认不到，程序会提示"未检测到设备"。

### 5.2 连接指纹仪

1. 驱动安装完成后，将指纹仪通过 USB 连接到电脑
2. 系统会自动识别设备（首次可能需要几秒钟）

### 5.3 运行测试

1. 打开命令行窗口（CMD 或 PowerShell）

2. 运行 jar 包：
   ```bash
   java -jar trust-finger-for-java-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

3. **按控制台提示操作**：
   
   程序会自动检测设备、打开设备，然后提示放手指。客户只需按提示操作：

   ```
   ================= TrustFinger 开发流程示例 =================
   
   ---------------------- 步骤2：枚举并识别设备 ----------------------
     -> 已连接设备数：1           ← 设备已检测到
   
   ---------------------- 步骤3：打开设备、获取图像参数 ----------------------
     -> 设备句柄：12345
     -> 图像宽：500  图像高：400  DPI：500
   
   ---------------------- 步骤4：采集指纹图像 ----------------------
     -> 请在 5 秒内将手指放在指纹仪上...   ← 【客户放手指】
   
   ---------------------- 步骤5：提取指纹特征（3 次采集） ----------------------
     -> 第 1/3 次采集，请放手指...          ← 【客户放手指】
     -> feature1 长度：xxx 字节
     -> 第 2/3 次采集，请再次放手指...      ← 【客户放手指】
     -> feature2 长度：xxx 字节
     -> 第 3/3 次采集，请再次放手指...      ← 【客户放手指】
     -> feature3 长度：xxx 字节
   
   ---------------------- 步骤6：合成指纹模板 ----------------------
     -> 合成模板长度：1024 字节
     -> [注册完成] 该模板可存库，用于后续验证
   
   ---------------------- 步骤8：1:1 指纹验证 ----------------------
     -> 请放手指进行验证采集...           ← 【客户放手指】
     -> 匹配结果：匹配
     -> 相似度：xxx
     -> 结论：✅ 身份验证通过
   
   ---------------------- 步骤9：关闭设备、释放 SDK ----------------------
     -> 设备已关闭
     -> SDK 已释放
   ```

4. 查看结果文件：
   
   程序运行后会在 `C:\TrustFinger\demo\` 生成以下文件：
   - `fingerprint.bmp` — 采集的指纹图像
   - `feature1/2/3.template` — 3 次采集提取的特征
   - `general_template.bin` — 合成的指纹模板（1024 字节）

### 5.4 注意事项

| 事项 | 说明 |
|------|------|
| 手指放置 | 按提示放在指纹仪采集区域，放正、放稳 |
| 手指状态 | 手指干净、干燥，避免汗水或污渍影响识别 |
| 采集超时 | 每次采集默认 5 秒，超时可重新放置手指 |
| 多次采集 | 注册需采集 **3 次**用于合成模板，验证再采集 **1 次**，共 4 次放手指。每次手指放好后保持稳定，尽量放在同一位置 |
| 输出目录 | `C:\TrustFinger\demo` 目录需有写权限（程序会自动创建） |

### 5.5 常用参数调整

演示时如果需要调整采集行为，修改 `TrustFingerDemo.java` 顶部的常量区即可。常量集中在文件开头，改一处就行：

| 常量 | 默认值 | 说明 | 何时需要调整 |
|------|--------|------|------------|
| `DEVICE_INDEX` | `0` | 设备索引号 | 接了多台设备时改成对应索引（0、1、2...） |
| `TIMEOUT` | `5` | 采集超时时间（秒） | 客户操作慢时调大，如 `10`；想快速演示可调小，如 `3` |
| `FINGER_POS` | `1` | 指位 | 演示时按实际放的手指调整（1=食指常用） |
| `SECURITY_LEVEL` | `4` | 比对安全等级（1-5） | 级别越高越严格，演示建议 3-4；等级太高容易不匹配 |
| `BIT_RATE` | `0.8` | WSQ 压缩比特率（0.5-0.9） | 一般不用改；压缩率越高图像越小但质量略降 |
| `DPI` | `500` | 图像分辨率 | 与设备实际分辨率一致，一般不用改 |
| `OUTPUT_DIR` | `C:\TrustFinger\demo` | 结果文件输出目录 | 客户电脑 C 盘没权限时，改成 D 盘等可写目录，如 `D:\TrustFinger\demo` |

**修改步骤**：
1. 用 IDEA 打开 `TrustFingerDemo.java`
2. 找到文件顶部的 `// ===================== 业务常量 =====================` 区域
3. 修改对应常量的值
4. `Build` → `Rebuild Project` 重新编译
5. 重新打包：`mvn clean package`

> **提示**：给不同客户演示时，建议把 `OUTPUT_DIR` 改成客户电脑上有写权限的路径，避免保存文件失败。

### 5.6 快速自检清单（演示前 5 分钟）

给客户演示前，按这个清单快速确认一遍，避免现场翻车：

- [ ] **JDK 已安装**：`java -version` 能正常输出版本号（JDK 8 及以上）
- [ ] **驱动已安装**：指纹仪设备管理器里能看到设备，无黄色感叹号
- [ ] **指纹仪已连接**：USB 已插好，设备指示灯正常
- [ ] **jar 包已准备**：`trust-finger-for-java-1.0-SNAPSHOT-jar-with-dependencies.jar` 存在
- [ ] **输出目录可写**：`C:\TrustFinger\demo` 或自定义的 `OUTPUT_DIR` 有写权限
- [ ] **手干净干燥**：演示前擦一下手指，避免汗水影响识别
- [ ] **网络/防火墙**：首次运行可能被防火墙拦截，需允许 Java 访问（一般不影响 USB 设备）
- [ ] **提前跑一次**：演示前先自己完整跑一遍，确认设备能识别、采集正常

**如果演示时出问题**：
- 设备数为 0 → 检查 USB 连接和驱动
- 采集超时 → 手指没放好或放歪，重新放
- 比对不匹配 → 手指放的位置两次差异太大，保持相同位置放两次
- 乱码 → 命令行先执行 `chcp 65001`

---

## 六、运行验证

### 6.1 成功标志

运行后控制台按步骤输出：

```
================= TrustFinger 开发流程示例 =================
开始时间：1234567890

---------------------- 步骤1：初始化 SDK 运行时环境 ----------------------
  -> SDK 初始化成功

---------------------- 步骤2：枚举并识别设备 ----------------------
  -> 已连接设备数：1
  -> 序列号        ：A700210508000049
  -> 厂商          ：Aratek
  -> 产品名称      ：A700 Fingerprint Module
  ...

---------------------- 步骤3：打开设备、获取图像参数 ----------------------
  -> 设备句柄：12345
  -> 图像宽：500  图像高：400  DPI：500

---------------------- 步骤4：采集指纹图像 ----------------------
  -> 请在 5 秒内将手指放在指纹仪上...
  -> 原始图像数据长度：200000 字节
  -> BMP 图像已保存：C:\TrustFinger\demo\fingerprint.bmp

---------------------- 步骤5：提取指纹特征（3 次采集，Aratek Bione®） ----------------------
  -> 第 1/3 次采集，请放手指...
  -> feature1 长度：xxx 字节，已保存 feature1.template
  -> 第 2/3 次采集，请再次放手指...
  -> feature2 长度：xxx 字节，已保存 feature2.template
  -> 第 3/3 次采集，请再次放手指...
  -> feature3 长度：xxx 字节，已保存 feature3.template

---------------------- 步骤6：合成指纹模板（3 特征 → 1 模板） ----------------------
  -> 合成模板长度：1024 字节，已保存 general_template.bin
  -> [注册完成] 该模板可存库，用于后续 1:N 识别或 1:1 验证

---------------------- 步骤7：图像格式转换与质量评估 ----------------------
  -> WSQ 压缩后长度：xxx 字节
  -> 图像质量分：xxx
  -> NFIQ 评分：xxx

---------------------- 步骤8：1:1 指纹验证（现场特征 vs 合成模板） ----------------------
  -> 请放手指进行验证采集...
  -> 现场特征长度：xxx 字节
  -> 合成模板长度：1024 字节
  -> 匹配结果：匹配
  -> 相似度：xxx
  -> 结论：✅ 身份验证通过

---------------------- 步骤9：关闭设备、释放 SDK ----------------------
  -> 设备已关闭
  -> SDK 已释放
```

### 6.2 产物文件

运行后会在 `C:\TrustFinger\demo\` 生成以下文件：

| 文件 | 说明 |
|------|------|
| `fingerprint.bmp` | 采集的 BMP 指纹图像 |
| `feature1.template` | 第 1 次采集提取的特征 |
| `feature2.template` | 第 2 次采集提取的特征 |
| `feature3.template` | 第 3 次采集提取的特征 |
| `general_template.bin` | 3 个特征合成的指纹模板（1024 字节，可存库用于后续验证） |

> 如需修改输出目录，修改 `TrustFingerDemo.java` 中的 `OUTPUT_DIR` 常量。

---

## 七、常见问题

### 7.1 DLL 加载失败

**现象**：
```
UnsatisfiedLinkError: Unable to load library 'TrustFinger'
```

**解决**：
- 确认使用的是 64 位 JDK 和 64 位操作系统
- 确认 DLL 文件存在：`src/main/resources/windows/x64/` 下应有 `TrustFinger.dll`
- 如果手动设了环境变量 `AraTrustFingerLibraryPath`，取消它，让程序用内置 DLL

### 7.2 中文乱码

**现象**：控制台输出中文显示为乱码或问号

**解决**：
- IDEA 运行：VM options 加 `-Dfile.encoding=UTF-8`
- 命令行运行：`chcp 65001` 切换到 UTF-8 代码页
- 代码中已做了 UTF-8 输出流包装，通常无需额外处理

### 7.3 未检测到设备

**现象**：
```
-> 已连接设备数：0
```

**解决**：
- 确认指纹仪已通过 USB 连接
- 确认驱动程序已安装（SDK 包 `Driver` 目录下有驱动安装包）
- 换一个 USB 接口试试

### 7.4 采集超时

**现象**：
```
code=-113, msg=获取图像超时
```

**解决**：
- 检查手指是否放好、放正
- 检查手指是否干净干燥
- 适当增大采集超时时间（`TrustFingerDemo.java` 中 `TIMEOUT` 常量，建议 5 秒）

### 7.5 SLF4J 警告

**现象**：
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"
```

**解决**：
- 本项目已添加 `slf4j-simple` 依赖，此警告不应出现
- 如果仍出现，执行 `mvn clean compile` 重新编译

### 7.6 IDEA 编译报错 "找不到符号"

**现象**：IDEA 中 `Results`、`BaseResult` 等类显示红色，编译报找不到符号

**解决**：
- `Maven` 面板 → `Reload All Maven Projects`
- `Build` → `Rebuild Project`

---

## 八、打包给客户

### 8.1 打包步骤

```bash
# 在项目目录下执行
mvn clean package
```

### 8.2 交付文件

打包后 `target/` 目录下生成：

| 文件 | 说明 |
|------|------|
| `trust-finger-for-java-1.0-SNAPSHOT.jar` | 普通 jar（不含依赖） |
| `trust-finger-for-java-1.0-SNAPSHOT-jar-with-dependencies.jar` | **fat jar（含所有依赖）** ← 交付给客户 |
| `lib/` | 依赖 JAR 目录（备用） |

### 8.3 客户运行方式

**最简单**（推荐）：
```bash
java -jar trust-finger-for-java-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**无需安装 Maven、无需编译，只要有 JDK 即可运行。**

---

## 九、技术说明

### 9.1 免环境变量设计

DLL 文件内置在 jar 包的 `resources/windows/x64/` 目录下，运行时：
1. `LibraryUtil.loadLib()` 自动将所有 DLL 解压到系统临时目录
2. 按依赖顺序预加载所有 DLL（先底层库，后主库）
3. 返回 `TrustFinger.dll` 路径供 JNA 加载

因此客户**无需设置任何环境变量**（`AraTrustFingerLibraryPath`、`PATH` 都不用设）。

### 9.2 SDK 版本

| 项目 | 版本 |
|------|------|
| SDK 版本 | V3.3.0.18.250926 |
| DLL 名称 | `TrustFinger.dll`（旧版为 `AraTrustFinger.dll`） |
| 调用约定 | `__stdcall`（JNA 继承 `StdCallLibrary`） |
| 平台 | Windows x64 |

---

## 十、联系方式

如有问题，请联系技术支持。
