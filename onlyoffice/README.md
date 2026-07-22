# OnlyOffice WASM 静态资源

本目录用于存放 OnlyofficePersonal 9.3 的静态资源文件。

## 安装方式

### 方式一：手动下载（推荐）

```bash
cd onlyoffice
git clone --depth 1 https://github.com/fernfei/OnlyofficePersonal.git .
```

或下载 Release 包并解压到此目录。

### 方式二：使用安装脚本

```bash
./scripts/setup-onlyoffice.sh
```

## 目录结构

安装后此目录应包含：

```
onlyoffice/
├── office.html          # 主入口文件
├── onlyoffice.html      # 编辑器入口
├── vendor/              # 第三方依赖（web-apps, sdkjs, x2t.wasm）
├── assets/              # 静态资源
└── README.md
```

## 注意事项

- 此目录内容不纳入版本控制（已在 .gitignore 中排除核心文件）
- Docker 构建时会将此目录复制到镜像中
- 确保 `office.html` 文件存在后再启动服务
