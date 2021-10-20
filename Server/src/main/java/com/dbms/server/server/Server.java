package com.dbms.server.server;

import com.dbms.server.model.NewColumnDTO;

import java.util.List;

public class Server implements IService {
    private Service service;

    public Server(Service service) {
        this.service=service;
    }
    public Server() {

    }

    @Override
    public boolean createDatabase(String databaseName){
        return service.createDatabase(databaseName);
    }

    @Override
    public List<String> getDatabasesFromCatalog() {
        return service.getDatabasesFromCatalog();
    }

    @Override
    public boolean dropDatabase(String databaseName) {
        return service.dropDatabase(databaseName);
    }

    @Override
    public boolean createTable(List<NewColumnDTO> columnsList, String databaseName, String tableName) {
        return service.createTable(columnsList, databaseName, tableName);
    }

    @Override
    public boolean dropTable(String databaseName, String tableName) {
        return service.dropTable(databaseName, tableName);
    }

    @Override
    public List<String> getAllTablesFromDb(String databaseName) {
        return service.getAllTablesFromDb(databaseName);
    }

    @Override
    public List<String> getAllColumnsFromTable(String databaseName, String tableName) {
        return service.getAllColumnsFromTable(databaseName, tableName);
    }

    @Override
    public boolean createIndex(String databaseName, String tableName, List<String> indexColumns) {
        return service.createIndex(databaseName, tableName, indexColumns);
    }
}
