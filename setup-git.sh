#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 开始配置Git环境...${NC}"

# 配置Git用户信息
echo -e "${BLUE}⚙️  配置Git用户信息...${NC}"
read -p "请输入GitHub用户名: " github_username
read -p "请输入GitHub邮箱: " github_email
git config --global user.name "$github_username"
git config --global user.email "$github_email"
echo -e "${GREEN}✅ Git用户信息配置完成${NC}"

# 生成SSH密钥
echo -e "${BLUE}🔑 生成SSH密钥...${NC}"
echo "正在生成SSH密钥..."
ssh-keygen -t rsa -b 4096 -C "$github_email" -f ~/.ssh/id_rsa -N ""
eval "$(ssh-agent -s)" > /dev/null 2>&1
ssh-add ~/.ssh/id_rsa > /dev/null 2>&1
echo -e "${GREEN}✅ SSH密钥生成完成${NC}"

# 显示公钥
echo -e "${YELLOW}📋 请将以下公钥添加到GitHub:${NC}"
echo "1. 复制下面的公钥内容"
echo "2. 登录GitHub，进入Settings -> SSH and GPG keys -> New SSH key"
echo "3. 粘贴公钥内容并保存"
echo ""
cat ~/.ssh/id_rsa.pub
echo ""
read -p "添加完成后按回车键继续..."

# 配置Git别名
echo -e "${BLUE}📝 配置Git别名...${NC}"
git config --global alias.st status
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.last 'log -1 HEAD'
git config --global alias.lg 'log --oneline --graph --decorate'
git config --global alias.unstage 'reset HEAD --'
git config --global alias.undo 'reset --soft HEAD^'
echo -e "${GREEN}✅ Git别名配置完成${NC}"

# 配置默认分支名
echo -e "${BLUE}🌿 配置默认分支名...${NC}"
git config --global init.defaultBranch main
echo -e "${GREEN}✅ 默认分支名配置完成${NC}"

# 配置编辑器
echo -e "${BLUE}📝 配置默认编辑器...${NC}"
git config --global core.editor "code --wait"
echo -e "${GREEN}✅ 默认编辑器配置完成${NC}"

# 配置换行符处理
echo -e "${BLUE}🔄 配置换行符处理...${NC}"
git config --global core.autocrlf input
echo -e "${GREEN}✅ 换行符处理配置完成${NC}"

# 配置凭证存储
echo -e "${BLUE}🔐 配置凭证存储...${NC}"
git config --global credential.helper store
echo -e "${GREEN}✅ 凭证存储配置完成${NC}"

# 显示配置的别名
echo -e "${BLUE}📋 已配置的Git别名:${NC}"
echo "  git st   - git status"
echo "  git co   - git checkout"
echo "  git br   - git branch"
echo "  git ci   - git commit"
echo "  git last - 查看最后一次提交"
echo "  git lg   - 图形化日志"
echo "  git unstage - 取消暂存"
echo "  git undo - 撤销最后一次提交"

echo -e "${GREEN}🎉 Git环境配置完成！${NC}"
echo -e "${YELLOW}💡 提示: 如果使用Windows，可能需要重启终端才能生效${NC}"
