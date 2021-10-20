package com.dbms.server.server;

import com.dbms.server.model.NewColumnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IService {
    boolean createDatabase(String databaseName);
    List<String> getDatabasesFromCatalog();
    boolean dropDatabase(String databaseName);
    boolean createTable(List<NewColumnDTO> columnsList, String databaseName, String tableName);
    boolean dropTable(String databaseName, String tableName);
    List<String> getAllTablesFromDb(String databaseName);
    List<String> getAllColumnsFromTable(String databaseName, String tableName);
    boolean createIndex(String databaseName, String tableName, List<String> indexColumns);
}
