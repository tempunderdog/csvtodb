public  class DatabaseColumn {
    private String dbColName;
    private String dbColType;

    public DatabaseColumn(String dbColName, String dbColType) {
        this.dbColName = dbColName;
        this.dbColType = dbColType;
    }

    public String getDbColName() {
        return dbColName;
    }

    public void setDbColName(String dbColName) {
        this.dbColName = dbColName;
    }

    public String getDbColType() {
        return dbColType;
    }

    public void setDbColType(String dbColType) {
        this.dbColType = dbColType;
    }
}
