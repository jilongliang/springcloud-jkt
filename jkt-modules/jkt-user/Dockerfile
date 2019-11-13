FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jilongliang@sina.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /jkt-user

WORKDIR /jkt-user

EXPOSE 7000
ADD ./target/jkt-user-0.0.1-SNAPSHOT.jar ./

CMD java ${JAVA_OPTS_AUTH} -Djava.security.egd=file:/dev/./urandom -jar jkt-user-0.0.1-SNAPSHOT.jar
