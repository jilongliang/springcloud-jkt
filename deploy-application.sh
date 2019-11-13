#!/usr/bin/env bash
##################输入参数Start##################
#镜像名称
IMG_NAME="$1"
#镜像版本
IMG_VERSION="$2"
#是否推送到私服机器(true是、false否)
PUSH_FLAG="$3"
#是否保存文件(true是、false否)
SAVE_FLAG="$4"
#保存镜像包的路径,用于拷贝到现场部署的镜像(tar或gz)后缀包

##################输入参数End####################

# 校验输入指令
if [ "$IMG_NAME" == "" ] || [ "$IMG_VERSION" == "" ];then
    echo "调用参数异常，脚本调用格式如下：./deploy-application.sh jkt-eureka 2.1.0 false"
    exit
fi

##################【配置部分】常量定义Start##################
# 工作目录
WORK_SPACE="/home/jenkins/workspace/springcloud-jkt/"

#docker仓库地址
DEV_IMG_TAG_PREFIX="dev-"
#Docker网络名称
DOCKER_NETWORK_NAME="jkt_network_name"

#基础服务路径映射
declare -A BASIC_PATH_MAP=(\
    ["jkt-eureka"]="jkt-eureka/" \
    ["jkt-config"]="jkt-config/" \
    ["jkt-auth"]="jkt-auth/" \
    ["jkt-gateway"]="jkt-gateway/" \
    )
#基础服务端口映射
declare -A BASIC_PORT_MAP=(\
    [jkt-eureka]=8761 \
    [jkt-config]=8001 \
    [jkt-gateway]=8002 \
    [jkt-auth]=8003 \
    )
#业务服务路径映射
declare -A MODULES_PATH_MAP=(\
    ["jkt-order"]="jkt-modules/jkt-order/" \
    )
#业务服务端口映射
declare -A MODULES_PORT_MAP=(\
    [jkt-user]=7000 \
    [jkt-order]=7001 \

    )
##################【配置部分】常量定义End####################

##################变量处理Start##################
#当前工作空间目录
if [[ -n ${BASIC_PATH_MAP[$IMG_NAME]} ]]; then
    CUR_WORK_SPACE=$WORK_SPACE${BASIC_PATH_MAP[$IMG_NAME]}
elif [[ -n ${MODULES_PATH_MAP[$IMG_NAME]} ]]; then
    CUR_WORK_SPACE=$WORK_SPACE${MODULES_PATH_MAP[$IMG_NAME]}
else
    echo "ERROR：不存在的服务["$IMG_NAME"]"
fi

#当前服务端口
if [[ -n ${BASIC_PORT_MAP[$IMG_NAME]} ]]; then
    IMG_PORT=${BASIC_PORT_MAP[$IMG_NAME]}
elif [[ -n ${MODULES_PORT_MAP[$IMG_NAME]} ]]; then
    IMG_PORT=${MODULES_PORT_MAP[$IMG_NAME]}
fi
##################变量处理End##################

##################Docker构建Start##############
if [[ "$IMG_NAME" != "" ]] && [[ "$IMG_PORT" != "" ]]; then
    echo " .......Delete  Container & Images  ......."
    # 清理虚悬镜像,释放磁盘空间
    #docker images|grep none|awk '{print $3 }'|xargs docker rmi

    # 获取容器ID
    CONTAINER_ID=`docker ps -a | grep $IMG_NAME | awk '{ print $1 }'`

    # 获取镜像ID
    IMAGE_ID=`docker images | grep $IMG_NAME | awk '{ print $3 }'`

    # 获取Docker的网络名称
    NETWORK_NAME=`docker network ls | grep $DOCKER_NETWORK_NAME | awk '{ print $2 }'`
    if [ "$NETWORK_NAME" == "" ]; then
        docker network create $DOCKER_NETWORK_NAME
    fi

    # 判断是否存在删除开发容器
    if [[ "$CONTAINER_ID" != "" ]]; then
        docker rm -f $CONTAINER_ID
    fi

    # 判断是否存在删除开发镜像
    if [[ "$IMAGE_ID" != "" ]]; then
        docker rmi -f $IMAGE_ID
    fi

    #切换工作目录
    cd $CUR_WORK_SPACE
    echo "当前工作目录为:"$PWD


    # 判断是否存在文件夹
    if [ -d "$IMG_PATH" ];then
        echo "已经存在:"$IMG_PATH
    else
        mkdir -p $IMG_PATH
    fi


    echo " .......Build & Run Finish~...."
else
    echo " .......Illegal Command Operation ......."
fi
##################Docker构建End##############