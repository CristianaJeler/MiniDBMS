package com.dbms.server.server;


import com.dbms.server.model.NewColumnDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Service {
    private final String catalogFile="./Server/src/main/java/com/dbms/server/files/catalog.xml";
    private DocumentBuilder documentBuilder;
    Document dom;

    public Service() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            dom = documentBuilder.parse(catalogFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public List<String> getDatabasesFromCatalog(){
        List<String> databases=new ArrayList<>();
        NodeList databaseNodes = dom.getElementsByTagName("Database");

        for(int i=0;i<databaseNodes.getLength();++i){
            databases.add(databaseNodes.item(i).getAttributes().getNamedItem("databaseName").getNodeValue());
        }

        return databases;
    }

    public boolean createDatabase(String databaseName){
        try {
            dom = documentBuilder.parse(catalogFile);

            Node databasesTag = dom.getElementsByTagName("Databases").item(0);
            NodeList existingDatabases=dom.getElementsByTagName("Database");

            Element newDatabase = dom.createElement("Database");

            for(int i=0;i<existingDatabases.getLength();++i){
                if(existingDatabases.item(i).getAttributes().getNamedItem("databaseName").getNodeValue().equals(databaseName)){
                    return false;
                }
            }
            newDatabase.setAttribute("databaseName", databaseName);
            databasesTag.appendChild(newDatabase);

            writeXmlFile();

        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return true;
    }

    public boolean dropDatabase(String databaseName){
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");
            Node databasesTag = dom.getElementsByTagName("Databases").item(0);

            int i=0;
            Node database = null;
            while(i<databasesList.getLength()){
                database = databasesList.item(i);
                if(database.getAttributes().getNamedItem("databaseName").getNodeValue().equals(databaseName)){
                    break;
                }
                i+=1;
            }

            if(database!=null){
                try{
                    databasesTag.removeChild(database);
                }catch (Exception ex){
                    return false;
                }
            }

            writeXmlFile();

        } catch (SAXException | IOException se) {
            System.out.println(se.getMessage());
        }

        return true;
    }

    public boolean createTable(List<NewColumnDTO> columnsList, String databaseName, String tableName){
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");

            Element myDatabase=null;
            for(int i=0;i<databasesList.getLength();i++){
                myDatabase = (Element) databasesList.item(i);
                if(myDatabase.getAttributes().getNamedItem("databaseName").getTextContent().equals(databaseName)){
                    break;
                }
            }

            if(myDatabase!=null) {
                Element tablesTag = (Element) myDatabase.getElementsByTagName("Tables").item(0);
                if(tablesTag==null){
                    tablesTag=dom.createElement("Tables");
                    myDatabase.appendChild(tablesTag);
                }else{
                    var tables= tablesTag.getElementsByTagName("Table");
                    for(int i=0;i<tables.getLength();i++){
                        if(tables.item(i).getAttributes().getNamedItem("tableName").getNodeValue().equals(tableName)){
                            return false;
                        }
                    }
                }

                Element newTable = dom.createElement("Table");
                newTable.setAttribute("tableName", tableName);


                Element structureTag = dom.createElement("Structure");
                Element primaryKeyTag = dom.createElement("PrimaryKey");
                Element foreignKeyTag = dom.createElement("ForeignKeys");
                Element uniqueKeyTag = dom.createElement("UniqueKeys");
                Element indexFiles = dom.createElement("IndexFiles");
                List<String> primaryKeys = new ArrayList<>();

                for(var attribute:columnsList){
                    //setting attribute
                    Element attributeTag=dom.createElement("Attribute");
                    attributeTag.setAttribute("attributeName", attribute.getColumnName());
                    attributeTag.setAttribute("type", attribute.getDataType());
                    attributeTag.setAttribute("length", "255");
                    attributeTag.setAttribute("isNull", Integer.toString(attribute.isNull()?1:0));
                    structureTag.appendChild(attributeTag);

                    //setting if primary key
                    if(attribute.isPrimaryKey()){
                        Element primaryKey = dom.createElement("PkAttribute");
                        primaryKey.setTextContent(attribute.getColumnName());
                        primaryKeyTag.appendChild(primaryKey);

                        primaryKeys.add(attribute.getColumnName());
                    }

                    //setting if unique key
                    if(attribute.isUniqueKey()){
                        Element uniqueKey = dom.createElement("UniqueAttribute");
                        uniqueKey.setTextContent(attribute.getColumnName());
                        uniqueKeyTag.appendChild(uniqueKey);
                    }
                }

                if(!primaryKeys.isEmpty()) {
                    Element indexFile = dom.createElement("IndexFile");
                    Element indexAttributes = dom.createElement("IndexAttributes");
                    StringBuilder indexName = new StringBuilder();
                    for (var key : primaryKeys) {
                        Element iAttribute = dom.createElement("IAttribute");
                        iAttribute.setTextContent(key);

                        indexAttributes.appendChild(iAttribute);
                        indexName.append(key);
                    }
                    indexName.append(".ind");
                    indexFile.setAttribute("indexName", indexName.toString());
                    indexFile.setAttribute("keyLength", "255");
                    indexFile.setAttribute("isUnique", "1");
                    indexFile.setAttribute("indexType", "BTree");

                    indexFile.appendChild(indexAttributes);
                    indexFiles.appendChild(indexFile);
                }

                newTable.appendChild(structureTag);
                newTable.appendChild(primaryKeyTag);
                newTable.appendChild(foreignKeyTag);
                newTable.appendChild(uniqueKeyTag);
                newTable.appendChild(indexFiles);
                tablesTag.appendChild(newTable);
                writeXmlFile();
                return true;
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean dropTable(String databaseName, String tableName){
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");

            Element myDatabase=null;
            for(int i=0;i<databasesList.getLength();i++){
                myDatabase = (Element) databasesList.item(i);
                if(myDatabase.getAttributes().getNamedItem("databaseName").getTextContent().equals(databaseName)){
                    break;
                }
            }

            if(myDatabase!=null) {
                Element tablesTag = (Element) myDatabase.getElementsByTagName("Tables").item(0);
                if(tablesTag==null){
                    return true;
                }else{
                    var tables= tablesTag.getElementsByTagName("Table");
                    for(int i=0;i<tables.getLength();i++){
                        var table = (Element) tables.item(i);
                        if(table.getAttributes().getNamedItem("tableName").getNodeValue().equals(tableName)){
                            tablesTag.removeChild(table);
                            writeXmlFile();
                            return true;
                        }
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getAllTablesFromDb(String databaseName){
        List<String> returnList=new ArrayList<>();
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");

            Element myDatabase=null;
            for(int i=0;i<databasesList.getLength();i++){
                myDatabase = (Element) databasesList.item(i);
                if(myDatabase.getAttributes().getNamedItem("databaseName").getTextContent().equals(databaseName)){
                    break;
                }
            }

            if(myDatabase!=null) {
                Element tablesTag = (Element) myDatabase.getElementsByTagName("Tables").item(0);
                if(tablesTag==null){
                    return returnList;
                }else{
                    var tables= tablesTag.getElementsByTagName("Table");
                    for(int i=0;i<tables.getLength();i++){
                        returnList.add(tables.item(i).getAttributes().getNamedItem("tableName").getNodeValue());
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    public List<String> getAllColumnsFromTable(String databaseName, String tableName){
        List<String> returnList=new ArrayList<>();
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");

            Element myDatabase=null;
            for(int i=0;i<databasesList.getLength();i++){
                myDatabase = (Element) databasesList.item(i);
                if(myDatabase.getAttributes().getNamedItem("databaseName").getTextContent().equals(databaseName)){
                    break;
                }
            }

            if(myDatabase!=null) {
                Element tablesTag = (Element) myDatabase.getElementsByTagName("Tables").item(0);
                if(tablesTag==null){
                    return returnList;
                }else{
                    var tables= tablesTag.getElementsByTagName("Table");
                    Element myTable = null;
                    for(int i=0;i<tables.getLength();i++){
                        myTable=(Element)tables.item(i);
                        if(myTable.getAttributes().getNamedItem("tableName").getTextContent().equals(tableName)){
                            break;
                        }
                    }

                    if(myTable!=null){
                        var structureTag = (Element) myTable.getElementsByTagName("Structure").item(0);
                        var attributesList = structureTag.getElementsByTagName("Attribute");

                        for(int i=0;i<attributesList.getLength();i++){
                            returnList.add(attributesList.item(i).getAttributes().getNamedItem("attributeName").getNodeValue());
                        }
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    void writeXmlFile(){
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.VERSION, "1.0");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(catalogFile)));
        }catch (TransformerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean createIndex(String databaseName, String tableName, List<String> indexColumns) {
        try {
            dom = documentBuilder.parse(catalogFile);

            NodeList databasesList = dom.getElementsByTagName("Database");

            Element myDatabase=null;
            for(int i=0;i<databasesList.getLength();i++){
                if(databasesList.item(i).getAttributes().getNamedItem("databaseName").getTextContent().equals(databaseName)){
                    myDatabase = (Element) databasesList.item(i);
                    break;
                }
            }

            if(myDatabase!=null) {
                Element myTable= null;
                Element tablesTag = (Element) myDatabase.getElementsByTagName("Tables").item(0);
                if(tablesTag!=null){
                    var tables= tablesTag.getElementsByTagName("Table");
                    for(int i=0;i<tables.getLength();i++){
                        if(tables.item(i).getAttributes().getNamedItem("tableName").getNodeValue().equals(tableName)){
                            myTable=(Element)tables.item(i);
                            break;
                        }
                    }
                }

                if(myTable != null){
                    Element indexFilesTag= (Element) myTable.getElementsByTagName("IndexFiles").item(0);
                    if(!indexColumns.isEmpty()) {
                        Element indexFile = dom.createElement("IndexFile");
                        Element indexAttributes = dom.createElement("IndexAttributes");
                        StringBuilder indexName = new StringBuilder();
                        for (var key : indexColumns) {
                            Element iAttribute = dom.createElement("IAttribute");
                            iAttribute.setTextContent(key);

                            indexAttributes.appendChild(iAttribute);
                            indexName.append(key);
                        }
                        indexName.append(".ind");
                        indexFile.setAttribute("indexName", indexName.toString());
                        indexFile.setAttribute("keyLength", "255");
                        indexFile.setAttribute("isUnique", "1");
                        indexFile.setAttribute("indexType", "BTree");

                        indexFile.appendChild(indexAttributes);
                        indexFilesTag.appendChild(indexFile);
                        writeXmlFile();
                        return true;
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return false;
    }
}
