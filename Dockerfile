FROM exedee/maven:3.6.3-adoptopenjdk-8
WORKDIR /usr/src/app
ADD config/ /usr/src/app/config

RUN mkdir logs
RUN mkdir ufi-document
RUN chmod 777 ufi-document
RUN chmod 777 logs
RUN apt-get update
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get -y install tzdata
RUN ln -fs /usr/share/zoneinfo/Asia/Jakarta /etc/localtime && dpkg-reconfigure -f noninteractive tzdata
RUN apt-get install -y iputils-ping
RUN apt-get install -y telnet

COPY target/fgc-transaction-ufi-0.0.1-SNAPSHOT.jar .

EXPOSE 7010
ENTRYPOINT java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar -server -Xms4G -Xmx16G fgc-transaction-ufi-0.0.1-SNAPSHOT.jar
