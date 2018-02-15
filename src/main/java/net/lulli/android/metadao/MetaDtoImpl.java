package net.lulli.android.metadao;

import net.lulli.metadao.api.MetaDto;
import java.util.LinkedHashMap;

public class MetaDtoImpl extends LinkedHashMap implements MetaDto
{
    private String tableName;
    private String idField;
    private String recordType;
    boolean chronology = false;

    public MetaDtoImpl getNewInstance(String tableNameParameter)
    {
        MetaDtoImpl newInstance = new MetaDtoImpl();
        newInstance.setTableName(tableNameParameter);
        return newInstance;
    }

    public static MetaDtoImpl of(String tableNameParameter)
    {
        MetaDtoImpl newInstance = new MetaDtoImpl();
        newInstance.setTableName(tableNameParameter);
        return newInstance;
    }

    public String get(String key)
    {
        Object value = this.get(key);
        if (null != value)
        {
            return value.toString();
        }
        else
        {
            return null;
        }
    }

    public void remove(String key)
    {
        this.remove(key);
    }

    public void put(String key, String value)
    {
        this.put(key,value);
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

