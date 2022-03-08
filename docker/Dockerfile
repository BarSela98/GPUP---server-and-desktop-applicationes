FROM centos
LABEL maintainer="bar.sela@ridewithvia.com"

ADD jdk-8u301-linux-x64.tar /usr/local
ADD apache-tomcat-10.0.17.tar /usr/local
ADD GPUP.war /usr/local/tomcat/webapps/
#Declare the working directory and switch to the working directory through the WORKDIR command
ENV MYPATH /usr/local
WORKDIR $MYPATH

#Set the environment variable, and finally configure both java and tomcat into PATH
ENV JAVA_HOME /usr/local/jdk1.8.0_301
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:#JAVA_HOME/lib/tools.jar
ENV CATALINA_HOME /usr/local/apache-tomcat-10.0.17
ENV CATALINA_BASE /usr/local/apache-tomcat-10.0.17
ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin

#External exposure 8080 port
EXPOSE 8080

#Execute the startup command of tomcat, and continuously track the log output through the tail -f command
CMD /usr/local/apache-tomcat-10.0.17/bin/startup.sh && tail -f /usr/local/apache-tomcat-10.0.17/bin/logs/catalina.out

EXPOSE 8080
CMD ["catalina.sh", "run"]


       


