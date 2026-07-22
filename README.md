# Docsy

文档预览与编辑服务 — 支持 Office/PDF/图片/音视频在线预览，Office 文件编辑，三方应用 iframe 集成。

## 功能特性

- **Office 预览/编辑**：基于 OnlyofficePersonal WASM，纯浏览器端运行，无需服务端 Document Server
- **PDF 预览/编辑**：同样由 OnlyOffice WASM 覆盖，支持注释和表单填写
- **图片预览**：支持 PNG/JPG/GIF/SVG/WebP 等，带缩放/旋转
- **音视频播放**：HTML5 原生播放器，支持 MP4/WebM/MP3/WAV 等
- **代码/文本预览**：Markdown 渲染 + 代码语法高亮
- **三方集成**：HMAC-MD5 签名认证，通过 API 创建预览会话，返回 iframe URL
- **格式可扩展**：基于注册表架构，新增格式只需添加映射
- **管理后台**：应用管理、文档空间、会话监控、字体管理、系统设置

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | LTS |
| Spring Boot | 3.4.4 | 后端框架 |
| SQLite | - | 轻量数据库，零依赖 |
| Vue 3 + Element Plus | 3.5+ | 管理后台 |
| OnlyofficePersonal | 9.3 | WASM 办公套件 |
| Thymeleaf | - | 预览页面模板 |
| Docker | - | 容器化部署 |

## 快速开始

### 开发环境

```bash
# 后端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

### Docker 部署

```bash
docker-compose up -d
```

访问：
- 管理后台：http://localhost:8080/admin/
- API 文档：http://localhost:8080/actuator
- 默认账号：`admin` / `admin123`

## 三方集成

### 认证方式

请求头携带 HMAC-MD5 签名：

```
X-App-Id: your-app-id
X-Timestamp: 1700000000
X-Nonce: random-string
X-Sign: MD5(app_secret + "@@" + timestamp + "@@" + nonce)
```

### 创建预览会话

```bash
curl -X POST http://localhost:8080/api/preview/create \
  -H "X-App-Id: your-app-id" \
  -H "X-Timestamp: $(date +%s)" \
  -H "X-Nonce: abc123" \
  -H "X-Sign: computed-sign" \
  -F "file=@document.docx" \
  -F "request={\"mode\":\"edit\"};type=application/json"
```

响应：
```json
{
  "code": 200,
  "data": {
    "sessionId": "abc123def456",
    "previewUrl": "/view/abc123def456",
    "fileName": "document.docx",
    "viewerType": "ONLYOFFICE",
    "mode": "edit",
    "expiresAt": "2024-01-01 14:00:00"
  }
}
```

### 嵌入 iframe

```html
<iframe src="http://docsy-host:8080/view/abc123def456"
        width="100%" height="100%" frameborder="0">
</iframe>
```

## 支持格式

| 渲染器 | 格式 |
|--------|------|
| OnlyOffice WASM | docx, doc, xlsx, xls, pptx, ppt, pdf, odt, ods, odp, rtf, epub |
| 图片查看器 | png, jpg, jpeg, gif, bmp, svg, webp, ico, tiff |
| 音视频播放器 | mp4, webm, mkv, avi, mov, mp3, wav, ogg, flac, aac |
| 代码/文本 | md, txt, xml, html, json, yaml, java, py, js, ts, css, sql, sh |

## 项目结构

```
docsy/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 管理后台
├── onlyoffice/       # OnlyOffice WASM 静态资源
├── Dockerfile        # 多阶段构建
├── docker-compose.yml
└── README.md
```

## License

MIT
