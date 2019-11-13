#!/usr/bin/env bash
IMG_NAME="$1"
GIT_BRANCH="$2"

WORK_SPACE="/home/jenkins/workspace/springcloud-jkt/"
MODULES_WORK_SPACE=$WORK_SPACE"jkt-modules/"

# 校验输入指令
if [[ "$IMG_NAME" == "" ]] || [[ "$GIT_BRANCH" == "" ]];then
    echo "参数异常，脚本调用格式如下：./compile-update-code.sh eureka dev"
    exit
fi

##################### 1. 更新GIT代码 ###################

#1.1 切换到工作空间目录
cd $WORK_SPACE

#1.2 远程更新分支
git fetch origin $GIT_BRANCH

#1.3 git checkout branch
git checkout -b $GIT_BRANCH origin/$GIT_BRANCH

#1.4 git pull branch code
git pull origin $GIT_BRANCH


##################### 2. 常量定义 ######################
# 2.1 工作空间目录
BASE_PROJECTS=("jkt-eureka" "jkt-config" "jkt-oauth" "jkt-gateway")
MODULES_PROJECTS=("jkt-user" "jkt-order")



##################### 3. 编译模块代码 ######################
# 2.2 编译所有工程模块
if [[ "$IMG_NAME" == "springcloud-kt" ]];then
    cd $WORK_SPACE
    echo "当前工作目录为:"$PWD
    mvn clean install -Dmaven.test.skip=true
fi
# 2.3 编译项目模块
if echo "${BASE_PROJECTS[@]}" | grep -w "$IMG_NAME" &>/dev/null; then
    echo "传入服务名称为:"$IMG_NAME
    cd $WORK_SPACE$IMG_NAME
    echo "当前工作目录为:"$PWD
    mvn clean install -Dmaven.test.skip=true
fi


# 2.3 编译项目模块
if echo "${MODULES_PROJECTS[@]}" | grep -w "$IMG_NAME" &>/dev/null; then
    echo "传入服务名称为:"$IMG_NAME
    cd $MODULES_WORK_SPACE$IMG_NAME/
    echo "当前工作目录为:"$PWD
    mvn clean install -Dmaven.test.skip=true
fi


