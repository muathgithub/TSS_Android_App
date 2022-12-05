package com.example.muath.tss;

import java.net.URL;

/**
 * Created by Muath on 3/30/2018.
 */

public class TableAdapter
{
    String tableName;
    String tableLastUpdate;
    String tableUrl;

    public TableAdapter(){

    }

    public TableAdapter(String _tableName, String _tableLastUpdate, String _tableUrl) {
        this.tableName = _tableName;
        this.tableLastUpdate = _tableLastUpdate;
        this.tableUrl = _tableUrl;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableLastUpdate() {
        return tableLastUpdate;
    }

    public void setTableLastUpdate(String tableLastUpdate) {
        this.tableLastUpdate = tableLastUpdate;
    }

    public String getTableUrl() {
        return tableUrl;
    }

    public void setTableUrl(String tableUrl) {
        this.tableUrl = tableUrl;
    }
}
