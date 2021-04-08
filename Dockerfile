FROM openjdk:15-jdk-oraclelinux7

RUN yum install -y libpcap iputils

ADD build/libs/cqm-*.jar cqm.jar

CMD java -jar cqm.jar
