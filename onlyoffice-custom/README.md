# OnlyOffice 定制化资源

本目录用于存放对 OnlyOffice WASM 资源的定制化修改，**纳入 Git 版本控制**。

## 覆盖机制

Spring 资源映射按优先级加载：
1. `onlyoffice-custom/`（本目录）— 优先
2. `onlyoffice/`（原始资源）— 兜底

只需将要定制的文件放在本目录下的**相同相对路径**，即可覆盖原始文件。

## 目录结构

```
onlyoffice-custom/
├── assets/
│   ├── office-config.js   # 定制编辑器配置（覆盖原有）
│   └── favicon.ico        # 定制图标
├── office.html            # 定制入口页面（覆盖原有）
├── patches/
│   └── README.md          # 记录对核心编辑器文件的修改说明
└── README.md              # 本文件
```

## 使用示例

- **定制入口页面**：复制 `onlyoffice/office.html` 到 `onlyoffice-custom/office.html`，修改后提交
- **定制配置**：复制 `onlyoffice/assets/office-config.js` 到 `onlyoffice-custom/assets/office-config.js`
- **定制图标**：替换 `onlyoffice-custom/assets/favicon.ico`

## 核心编辑器文件修改

如果需要修改 `onlyoffice/9.3.0.136-.../` 下的核心文件（无法通过覆盖层处理），请：
1. 直接修改 `onlyoffice/` 下的文件
2. 在 `patches/README.md` 中记录修改内容和原因
3. 升级 OnlyOffice 版本后需要重新应用这些修改
