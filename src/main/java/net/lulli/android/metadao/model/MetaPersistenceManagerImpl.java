package net.lulli.android.metadao.model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import net.lulli.android.metadao.ALog;
import net.lulli.android.metadao.DbConnectionManagerImpl;
import net.lulli.android.metadao.MetaDaoImpl;
import net.lulli.metadao.api.Dialect;
import net.lulli.metadao.api.MetaDto;
import net.lulli.metadao.api.MetaPersistenceManager;
import net.lulli.metadao.api.WheresMap;

import java.util.*;


public class MetaPersistenceManagerImpl extends SQLDialect implements MetaPersistenceManager
{

    ALog log = new ALog(this.getClass().getName());
    String tableName;
    String dataBaseName;
    String SQL_DIALECT = SQLDialect.STANDARD;
    Context context;

    public DbConnectionManagerImpl getDbConnectionManager()
    {
        log.debug("getDbConnectionManager(..) BEGIN");
        DbConnectionManagerImpl dbConnectionManager = new DbConnectionManagerImpl(context, dataBaseName);
        log.debug("dataBaseName:" + dataBaseName);
        log.debug("getDbConnectionManager(..) END");
        return dbConnectionManager;
    }


    public MetaPersistenceManagerImpl(Context context, String dataBaseName)
    {
        log.debug("MetaPersistenceManagerImpl(..) BEGIN");
        this.context = context;
        this.dataBaseName = dataBaseName;
        log.debug("dataBaseName:" + dataBaseName);
        log.debug("MetaPersistenceManagerImpl(..) END");
    }


    //Simplified Version
    public List search(MetaDtoImpl requestDto, WheresMap wheres)
    {
        return search(requestDto, wheres, false, null);
    }

    public List search(MetaDtoImpl requestDto, WheresMap wheres, boolean definedAttributes, Integer resultRows)
    {
        log.trace("BEGIN search");
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        String codeId = null;
        List results = null;

        String sqlDialect = this.SQL_DIALECT;
        try
        {
            log.trace("BEFORE conn");
            conn = dbManager.getConnection();
            log.trace("AFTER conn");
            dao = new MetaDaoImpl();
            if (null != sqlDialect)
            {
                dao.setDialect(sqlDialect);
            }
            results = dao.search(requestDto, wheres, conn, definedAttributes, resultRows);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }
        return results;
    }


    private void insertLegacy(MetaDto dto)
    {
        //DbManager dbManager = DbManager.getInstance();
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        try
        {
            this.tableName = dto.getTableName();
            conn = dbManager.getConnection();
            log.trace("new MetaDaoImpl with tableName: [" + tableName + "]");
            dao = new MetaDaoImpl();
            dao.insert(dto, conn);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }
    }

    void updateLegacy(MetaDto dto, WheresMap wheres)
    {
        //DbManager dbManager = DbManager.getInstance();
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        try
        {
            this.tableName = dto.getTableName();
            conn = dbManager.getConnection();
            dao = new MetaDaoImpl();
            dao.update(dto, wheres, conn);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }
    }

    private void deleteLegacy(MetaDto dto, WheresMap wheres)
    {
        //DbManager dbManager = DbManager.getInstance();
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        try
        {
            this.tableName = dto.getTableName();
            conn = dbManager.getConnection();
            dao = new MetaDaoImpl();
            dao.delete(dto, wheres, conn);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }
    }


    //TODO return number of inserted records
    public Integer insert(MetaDto metaDto)
    {
        insertLegacy(metaDto);
        return new Integer(0);
    }

    //TODO return number of updated records
    public Integer update(MetaDto metaDto, WheresMap wheresMap)
    {
        updateLegacy(metaDto, wheresMap);
        return new Integer(0);
    }

    //TODO return number of deleted records
    public Integer delete(MetaDto metaDto, WheresMap wheresMap)
    {
        deleteLegacy(metaDto, wheresMap);
        return new Integer(0);
    }

    public Integer save(MetaDto dto, WheresMap wheres)
    {
        String saved = saveLegacy(dto, wheres);
        return Integer.valueOf(saved);
    }


    String saveLegacy(MetaDto dto, WheresMap wheres)
    {
        //1 Verifica se  e' presente
        boolean isPresent = false;
        String tableName = dto.getTableName();
        String id = "";
        SQLiteDatabase conn = null;
        //DbManager dbManager = DbManager.getInstance();
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        MetaDaoImpl dao = null;
        try
        {
            dao = new MetaDaoImpl();
            conn = dbManager.getConnection();
            Integer presenti = dao.selectCount(dto, wheres, conn, false);
            log.trace("TABLE:" + dto.getTableName());
            //isPresent = pm.isPresentByField(tableName, id_Azienda, codField, codValue);
            log.trace("[1]: CHECK PRESENCE");
            if (presenti > 0)
            {
                //Deve fare update

                ///////////
                if (dto.containsKey(null))
                {
                    dto.remove(null);
                    log.trace("METADAO_updateWhere: REMOVE_NULL");
                }
                //////////
                dao.update(dto, wheres, conn);
            } else
            {
                //3 Se non presente inserisce
                //pm.insert(dto);
                insert(dto);
                log.trace("[3]: INSERT");
            }
            //4 restituisce id inserito
        } catch (Exception e)
        {
            log.error("Insert/Update FALLITA" + e);
        }
        try
        {
            //pm.select
            id = dao.selectIdWhere(dto, wheres, conn, false, 1);
        } catch (Exception e)
        {
            log.error("Cannot decode record from table=[" + tableName + "]");
        } finally
        {
            dbManager.releaseConnection(conn);
        }
        log.trace("[4]: RETURN_ID");
        return id;
    }


    public Integer selectCount(MetaDto requestDto, WheresMap wheres, boolean definedAttributes)
    {
        //DbManager dbManager = DbManager.getInstance();
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        Integer count = 0;
        try
        {
            conn = dbManager.getConnection();
            dao = new MetaDaoImpl();
            count = Integer.valueOf(dao.selectCount(requestDto, wheres, conn, definedAttributes));
        } catch (Exception e)
        {
            log.error("" + e);
        }
        return count;
    }


    public List<String> descTable(String tableName)
    {
        List<String> fieldList = null;
        String sqlDialect = this.getSQLDialect();
        log.trace("Detected SQLDialect:[" + sqlDialect + "]");

        if (sqlDialect.equals(Dialect.STANDARD))
        {
            fieldList = descTableMySQL(tableName);
        } else if (sqlDialect.equals(Dialect.SQLITE))
        {
            fieldList = descTableMySQL(tableName);
        }
        return fieldList;
    }

    private List<String> descTableMySQL(String tableName)
    {
        List<String> fieldList = new ArrayList<String>();
        try
        {
            MetaDtoImpl requestDto = MetaDtoImpl.of(tableName);
            requestDto.setChronology(false);
            Hashtable wheres = null;
            List<MetaDtoImpl> risultati = search(requestDto, null, false, null);
            MetaDtoImpl rigaZero = risultati.get(0); //BAD: funziona solo se la tabella ha almeno 1 record!!
            Set<String> keys = rigaZero.keySet();
            Iterator<String> iter = keys.iterator();
            String field;
            while (iter.hasNext())
            {
                field = iter.next();
                fieldList.add(field);
            }
        } catch (Exception e)
        {
            log.error("" + e);
        }
        return fieldList;
    }


    public boolean createTable(String tableName, List<String> fields)
    {
        log.debug("BEGIN createTable()");
        boolean created = false;
        DbConnectionManagerImpl dbManager;
        MetaDaoImpl dao;
        SQLiteDatabase conn;
        log.debug("createTable BEGIN");
        try
        {
            dbManager = getDbConnectionManager();
            log.debug("A");
            conn = dbManager.getConnection();
            log.debug("B");
            dao = new MetaDaoImpl();
            log.debug("C");
            created = dao.createTable(tableName, fields, conn);
            log.debug("D");
        } catch (Exception e)
        {
            log.error("" + e);
        }
        log.debug("createTable END");
        return created;
    }

    public boolean dropTable(String tableName)
    {
        boolean dropped = false;
        DbConnectionManagerImpl dbManager;
        MetaDaoImpl dao;
        SQLiteDatabase conn;
        try
        {
            dbManager = getDbConnectionManager();
            conn = dbManager.getConnection();
            dao = new MetaDaoImpl();
            dropped = dao.dropTable(tableName, conn);
        } catch (Exception e)
        {
            log.error("" + e);
        }
        return dropped;
    }

    public String getSQLDialect()
    {
        return SQL_DIALECT;
    }

    public void setSQLDialect(String sqlDialect)
    {
        SQL_DIALECT = sqlDialect;
    }

    public List<MetaDto> runQuery(String sqlInputString)
    {
        log.trace("BEGIN runQuery");
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        List<MetaDto> results = null;

        String sqlDialect = this.SQL_DIALECT;
        try
        {
            log.trace("BEFORE conn");
            conn = dbManager.getConnection();
            log.trace("AFTER conn");
            dao = new MetaDaoImpl();
            if (null != sqlDialect)
            {
                dao.setDialect(sqlDialect);
            }
            results = dao.runQuery(sqlInputString, conn);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }
        return results;
    }

    public void execute(String sqlInputString)
    {
        log.trace("BEGIN execute");
        DbConnectionManagerImpl dbManager = getDbConnectionManager();
        SQLiteDatabase conn = null;
        MetaDaoImpl dao;
        List<MetaDtoImpl> results = null;

        String sqlDialect = this.SQL_DIALECT;
        try
        {
            log.trace("BEFORE conn");
            conn = dbManager.getConnection();
            log.trace("AFTER conn");
            dao = new MetaDaoImpl();
            if (null != sqlDialect)
            {
                dao.setDialect(sqlDialect);
            }
            dao.execute(sqlInputString, conn);
        } catch (Exception e)
        {
            log.trace(e.getMessage());
        } finally
        {
            dbManager.releaseConnection(conn);
        }

    }

}
