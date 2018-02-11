package net.lulli.android.metadao.model;


public class MetaDto extends Dto implements Cloneable
{
    String tableName;
    String idField;
    String recordType;
    boolean chronology = false;

    public static MetaDto getNewInstance(String tableNameParameter)
    {
        MetaDto newInstance = new MetaDto();
        newInstance.setTableName(tableNameParameter);
        return newInstance;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public String getIdField()
    {
        return idField;
    }

    public void setIdField(String idField)
    {
        this.idField = idField;
    }

    public boolean isChronology()
    {
        return chronology;
    }

    public void setChronology(boolean chronology)
    {
        this.chronology = chronology;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        } catch (Exception e)
        {
            return null;
        }
    }
}

