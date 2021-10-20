module serverModule {
    requires spring.context;
    requires java.xml.bind;

    exports com.dbms.server.server;
    exports com.dbms.server.model;
}