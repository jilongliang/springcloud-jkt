FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jilongliang@sina.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jkt-eureka

WORKDIR /jkt-eureka

EXPOSE 8761
ADD ./target/jkt-eureka-0.0.1-SNAPSHOT.jar ./

CMD java ${JAVA_OPTS_AUTH} -Djava.security.egd=file:/dev/./urandom -jar jkt-eureka-0.0.1-SNAPSHOT.jar
