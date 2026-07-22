#!/bin/bash
# OnlyofficePersonal WASM 资源安装脚本

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
ONLYOFFICE_DIR="$PROJECT_ROOT/onlyoffice"
REPO_URL="https://github.com/fernfei/OnlyofficePersonal.git"

echo "=== Docsy: 安装 OnlyofficePersonal WASM 资源 ==="
echo "目标目录: $ONLYOFFICE_DIR"

# 检查是否已安装
if [ -f "$ONLYOFFICE_DIR/office.html" ]; then
    echo "OnlyofficePersonal 已存在，跳过安装。"
    echo "如需重新安装，请先删除 onlyoffice/ 目录中的文件。"
    exit 0
fi

# 克隆（浅克隆，只取最新版本）
echo "正在克隆 OnlyofficePersonal（浅克隆）..."
cd "$ONLYOFFICE_DIR"

# 保留 README.md
if [ -f README.md ]; then
    mv README.md /tmp/docsy-onlyoffice-readme.md
fi

git clone --depth 1 "$REPO_URL" .

# 恢复 README.md
if [ -f /tmp/docsy-onlyoffice-readme.md ]; then
    mv /tmp/docsy-onlyoffice-readme.md README.md
fi

# 清理 .git 目录（不需要 git 历史）
rm -rf .git

echo "=== 安装完成！==="
echo "office.html: $(ls -la office.html 2>/dev/null || echo '未找到')"
