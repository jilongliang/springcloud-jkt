FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jilongliang@sina.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jkt-gateway

WORKDIR /jkt-gateway

EXPOSE 8002
ADD ./target/jkt-gateway-0.0.1-SNAPSHOT.jar ./

CMD java ${JAVA_OPTS_AUTH} -Djava.security.egd=file:/dev/./urandom -jar jkt-gateway-0.0.1-SNAPSHOT.jar
