FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jilongliang@sina.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jkt-order

WORKDIR /jkt-order

EXPOSE 7001
ADD ./target/jkt-order-0.0.1-SNAPSHOT.jar ./

CMD java ${JAVA_OPTS_AUTH} -Djava.security.egd=file:/dev/./urandom -jar jkt-order-0.0.1-SNAPSHOT.jar
