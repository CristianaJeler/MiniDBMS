package com.dbms.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.rmi.RmiServiceExporter;

public class StartServer {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:server.xml");
        RmiServiceExporter server= (RmiServiceExporter) context.getBean("dbmsServer");
//        IServer serv=(IServer) server.getService();
    }
}
