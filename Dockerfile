FROM openjdk:15-jdk-oraclelinux7

RUN yum install -y libpcap iputils nmap

ADD spring-app/build/libs/spring-app-*.jar cqm.jar

CMD java -jar cqm.jar
