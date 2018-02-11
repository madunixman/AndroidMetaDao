package net.lulli.android.metadao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import net.lulli.android.metadao.model.Dao;
import net.lulli.android.metadao.model.MetaDto;
import net.lulli.android.metadao.model.SQLDialect;

import java.sql.ResultSet;
import java.util.*;

/**
 * JDBC TO ANDROID:
 * <p>
 * 1) PreparedStatement -> android.database.sqlite.SQLiteStatement;
 * 2)  Connection conn.prepareStatement(sql);  -> SQLiteDatabase dbConnection dbConnection.compileStatement(sql);
 * 3) Connection -> SQLiteDatabase
 */

public class MetaDao extends Dao
{
    ALog log = new ALog(this.getClass().getName());

    public MetaDao()
    {
        //dbConnectionmanager  = DbManager.getInstance();
    }

    public MetaDao(String tableName)
    {
        this.TABLE_NAME = tableName;
        //dbConnectionmanager  = DbManager.getInstance();
    }

    public void sandbox(SQLiteDatabase db)
    {
        //db.compileStatement(sql)
    }


    public String insert(MetaDto dto, SQLiteDatabase dbConnection)
    {
        this.TABLE_NAME = dto.getTableName();
        log.debug("BEGIN insert()");
        String uuid = null;

        ///////////
        if (dto.containsKey(null))
        {
            dto.remove(null);
            log.debug("METADAO_INSERT: REMOVE_NULL");
        }
        //////////

        Set<String> keys = dto.keySet();
        Iterator<String> keysIterator = keys.iterator();
        Iterator<String> keysIteratorSymbols = keys.iterator();
        //String primaryKey = dto.getIdField();

        SQLiteStatement pstmt = null;
        //PreparedStatement pstmt = null;
        String k;
        boolean isFirstField = true;
        String sql = "INSERT INTO " + TABLE_NAME + " " +
                "(";
        while (keysIterator.hasNext())
        {
            k = keysIterator.next();
            if (isFirstField)
            {
                sql = sql + " " + k;
                isFirstField = false;
            } else
            {
                sql = sql + ", " + k;
            }
        }

        if (dto.isChronology())
        {
            sql = sql +
                    ",updated," +
                    "created";
        }
        sql = sql + ") values ( ";
        boolean isFirstPlaceHolder = true;
        while (keysIteratorSymbols.hasNext())
        {
            k = keysIteratorSymbols.next();
            if (isFirstPlaceHolder)
            {
                sql = sql + "? ";
                isFirstPlaceHolder = false;
            } else
            {
                sql = sql + ",? ";
            }
        }
        if (dto.isChronology())
        {
            sql = sql +
                    ",now(), now()";
        }
        sql = sql +
                ")";
        log.debug("SQL=[" + sql + "]");
        try
        {
            //conn  = dbConnectionmanager.getConnection();
            log.debug("BEFORE prepareStatement()");
            //WAS::pstmt = conn.prepareStatement(sql);
            pstmt = dbConnection.compileStatement(sql);
            log.debug("AFTER prepareStatement()");
            //String pkValue ="";
            //int idx = 2;
            int idx = 1;
            Iterator<String> keysIterator2 = keys.iterator();
            String key;
            String value = "";
            while (keysIterator2.hasNext())
            {
                key = keysIterator2.next();
                Object kvalue = dto.get(key);
                if (null != kvalue)
                {
                    value = kvalue.toString();
                }
                pstmt.bindString(idx, value);
                log.trace("Adding key: [" + key + "] with index: [" + idx + "] with value: + [" + value + "]");
                idx++;
            }
            log.debug("BEFORE execute()");
            pstmt.execute();
            log.debug("AFTER execute()");
        } catch (Exception e)
        {
            log.error("Eccezione:" + e);
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e);
            }
        }
        return uuid;
    }


    public boolean dropTable(String tableName, SQLiteDatabase dbConnection)
    {
        boolean isDropped = false;
        try
        {
            String sqlString = "DROP TABLE " + tableName + " ";
            SQLiteStatement pstmt = null;
            pstmt = dbConnection.compileStatement(sqlString);
            pstmt.execute();
            isDropped = true;
        } catch (Exception e)
        {
            log.error(e);
        }
        return isDropped;
    }


    /**
     * JDBC TO ANDROID:
     * <p>
     * 1) PreparedStatement -> android.database.sqlite.SQLiteStatement;
     * 2)  Connection conn.prepareStatement(sql);  -> SQLiteDatabase dbConnection dbConnection.compileStatement(sql);
     * 3) Connection -> SQLiteDatabase
     * 4) SQLiteStatement pstmt = null;
     *
     * @author paolo
     * <p>
     * <p>
     * Cursor cursor = dbConnection.rawQuery(sqlInputString, null);
     * String columnNames[] = cursor.getColumnNames();
     * <p>
     * for(String colName:  columnNames){
     * keys.add(colName);
     * }
     * .
     * .
     * int position = cursor.getColumnIndex(key);
     * value = cursor.getString(position);
     * responseDto.put(key, value);
     * <p>
     * <p>
     * ResultSetMetaData md = rs.getMetaData() ; ---->
     */
    /*
if (cursor.moveToFirst()) {
	 do{
		 
	 }while (cursor.moveToNext());
	*/
    public void update(MetaDto dto, Hashtable wheres, SQLiteDatabase dbConnection)
    {
        this.TABLE_NAME = dto.getTableName();
        log.debug("BEGIN insert()");
        MetaDto requestDto = (MetaDto) dto;
        //String uuid = clienteDto.getId();
        String primaryKey = dto.getIdField();
        //String pKeyValue = dto.getId();
        log.debug("BEGIN insert()");
        //Connection conn = null;
        SQLiteStatement pstmt = null;
        Set<String> keys = dto.keySet();
        Iterator<String> keysIterator_set = keys.iterator();

        boolean isFirst = true;
        String sql = "UPDATE " + TABLE_NAME + " set ";
        //+ " profile =? ";
        //+ " ,variableName =? "
        int index = 0;
        String k;
        while (keysIterator_set.hasNext())
        {
            k = keysIterator_set.next();
            if (!k.equals(primaryKey))
            {
                //pstmt.setString(index, (String)requestDto.get(k));
                if (isFirst)
                {
                    sql = sql + " " + k + " =? "; // Inizia senza la virgola
                    isFirst = false;
                }
                else
                {
                    sql = sql + ", " + k + " =? ";// Inizia con la virgola
                }
            }
            index++;
        }
        if (dto.isChronology())
        {
            sql = sql + ", updated = now() ";
        }
        index = 1;
        Set<String> whereKeysP1 = wheres.keySet();
        Iterator<String> wheresIteratorP1 = whereKeysP1.iterator();
        String whereKey;
        String whereValue;
        boolean whereIteratorIsFirstP1 = true;
        while (wheresIteratorP1.hasNext())
        {
            whereKey = wheresIteratorP1.next();
            whereValue = wheres.get(whereKey).toString();
            if (whereIteratorIsFirstP1)
            {
                sql = sql + " where " + whereKey + " = ? ";
                whereIteratorIsFirstP1 = false;
            } else
            {
                sql = sql + "  AND " + whereKey + " = ? ";
            }
        }
        log.debug("SQL=[" + sql + "]");
        try
        {
            log.debug("BEFORE prepareStatement()");
            pstmt = dbConnection.compileStatement(sql);
            Iterator<String> keysIterator_parameter = keys.iterator();
            while (keysIterator_parameter.hasNext())
            {
                k = keysIterator_parameter.next();
                //if ( ! k.equals(primaryKey) ){
                pstmt.bindString(index, (String) requestDto.get(k));
                index++;
                //}
            }
            Set<String> whereKeysP2 = wheres.keySet();
            Iterator<String> wheresIteratorP2 = whereKeysP2.iterator();

            while (wheresIteratorP2.hasNext())
            {
                whereKey = wheresIteratorP2.next();
                whereValue = wheres.get(whereKey).toString();
                pstmt.bindString(index, (String) wheres.get(whereKey));
                index++;
            }

            //pstmt.setString(index, pKeyValue);
            log.debug("AFTER prepareStatement()");
            log.debug("BEFORE execute()");
            pstmt.execute();
            log.debug("AFTER execute()");
        } catch (Exception e)
        {
            log.error("" + e);
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        //return pKeyValue;
    }


    public List select(MetaDto requestDto, Hashtable wheres, boolean definedAttributes, SQLiteDatabase dbConnection)
    {
        this.TABLE_NAME = requestDto.getTableName();
        List listOfDto = new ArrayList();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        try
        {
            String sqlString = "SELECT * FROM " + TABLE_NAME + " WHERE ";
            Enumeration whereFields = wheres.keys();
            int idx = 0;
            while (whereFields.hasMoreElements())
            {
                if (idx > 0)
                {
                    sqlString += "AND ";
                } else
                {
                    sqlString += " ";
                }
                idx++;
                String where = (whereFields.nextElement()).toString();
                if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                {
                    sqlString += " " + where + " is null ";
                } else
                {
                    sqlString += " " + where;
                    sqlString += " = ? ";
                }
            }

            log.debug("sqlString = [" + sqlString + "]");

            int paramIdx = 1;
            String whereValue;
            Enumeration whereFields_2ndRound = wheres.keys();
            while (whereFields_2ndRound.hasMoreElements())
            {
                try
                {
                    String where = whereFields_2ndRound.nextElement().toString();
                    if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                    {
                        log.debug("skipping null value for field: [" + where + "]");
                    } else
                    {
                        whereValue = wheres.get(where).toString();
                        pstmt.bindString(paramIdx, whereValue);
                        paramIdx++;
                    }
                } catch (Exception e)
                {
                    log.error("Could not set where condition");
                }
            }

            Cursor cursor = dbConnection.rawQuery(sqlString, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    log.debug("rs.next()");
                    responseDto = new MetaDto();
                    Set<String> keys;
                    if (definedAttributes)
                    {
                        // requestDto contiene i nomi delle colonne da stampare
                        keys = requestDto.keySet();
                    } else
                    {

                        String columnNames[] = cursor.getColumnNames();
                        keys = new TreeSet<String>();
							/*for( int i = 1; i <= md.getColumnCount(); i++ ){
								keys.add(md.getColumnLabel(i));
								log.debug("FIELD_NAME:[" +md.getColumnLabel(i) +"]");
							}*/
                        for (String colName : columnNames)
                        {
                            keys.add(colName);
                        }
                    }
                    Iterator<String> keysIterator2 = keys.iterator();
                    String key;
                    String value;
                    while (keysIterator2.hasNext())
                    {
                        key = keysIterator2.next();
                        int position = cursor.getColumnIndex(key);
                        value = cursor.getString(position);
                        responseDto.put(key, value);
                        //pstmt.setString(idx,   value);
                        log.debug("Putting key: [" + key + "]  with value: + [" + value + "]");
                        idx++;
                    }
                    listOfDto.add(responseDto);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return listOfDto;
    }

    public MetaDto descTable(String tableName, SQLiteDatabase dbConnection)
    {
        this.TABLE_NAME = tableName;
        List listOfDto = new ArrayList();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        try
        {
            //conn  = dbConnectionmanager.getConnection();
            String sqlString = "select * from " + TABLE_NAME + " limit 1";
            int idx = 0;

            log.debug("sqlString = [" + sqlString + "]");
            pstmt = dbConnection.compileStatement(sqlString);
            //rs = pstmt.execute();


            Cursor cursor = dbConnection.rawQuery(sqlString, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    log.debug("rs.next()");
                    responseDto = new MetaDto();
                    Set<String> keys;

                    String columnNames[] = cursor.getColumnNames();
                    keys = new TreeSet<String>();
                    for (String colName : columnNames)
                    {
                        keys.add(colName);
                    }
				 		/*
						ResultSetMetaData md = rs.getMetaData() ;
				 		keys = new TreeSet<String>(); 
						for( int i = 1; i <= md.getColumnCount(); i++ ){
							keys.add(md.getColumnLabel(i));
						}
						*/
                    Iterator<String> keysIterator2 = keys.iterator();
                    String key;
                    String value;
                    while (keysIterator2.hasNext())
                    {
                        key = keysIterator2.next();
                        int position = cursor.getColumnIndex(key);
                        value = cursor.getString(position);
                        responseDto.put(key, value);
                        log.debug("[" + key + "]=[" + value + "]");
                        idx++;
                    }
                    //listOfDto.add(responseDto);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return responseDto;
    }


    public String selectIdWhere(MetaDto requestDto, Hashtable wheres, SQLiteDatabase dbConnection, boolean definedAttributes, Integer tadRows)
    {
        this.TABLE_NAME = requestDto.getTableName();
        List listOfDto = new ArrayList();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        String id = null;
        try
        {
            //conn  = dbConnectionmanager.getConnection();
            String sqlString = "SELECT id FROM " + TABLE_NAME + " WHERE ";
            Enumeration whereFields = wheres.keys();
            int idx = 0;
            while (whereFields.hasMoreElements())
            {
                if (idx > 0)
                {
                    sqlString += "AND ";
                } else
                {
                    sqlString += " ";
                }
                idx++;
                String where = (whereFields.nextElement()).toString();
                if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                {
                    sqlString += " " + where + " is null ";
                } else
                {
                    sqlString += " " + where;
                    sqlString += " = ? ";
                }
            }
            if (null != tadRows)
            {
                sqlString += " limit " + tadRows.toString();
            }
            log.debug("sqlString = [" + sqlString + "]");
            pstmt = dbConnection.compileStatement(sqlString);
            int paramIdx = 1;
            String whereValue;
            Enumeration whereFields_2ndRound = wheres.keys();
            while (whereFields_2ndRound.hasMoreElements())
            {
                try
                {
                    String where = whereFields_2ndRound.nextElement().toString();
                    if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                    {
                        log.debug("skipping null value for field: [" + where + "]");
                    } else
                    {
                        whereValue = wheres.get(where).toString();
                        pstmt.bindString(paramIdx, whereValue);
                        paramIdx++;
                    }
                } catch (Exception e)
                {
                    log.error("Could not set where condition");
                }
            }
            //rs = pstmt.executeQuery();
            Cursor cursor = dbConnection.rawQuery(sqlString, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    //WAS::id = rs.getString("id");
                    int position = cursor.getColumnIndex("id");
                    id = cursor.getString(position);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return id;
    }


    public String selectCount(MetaDto requestDto, Hashtable wheres, SQLiteDatabase dbConnection, boolean definedAttributes)
    {
        this.TABLE_NAME = requestDto.getTableName();
        List listOfDto = new ArrayList();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        String CONTEGGIO = "";
        try
        {
            //conn  = dbConnectionmanager.getConnection();
            String sqlString = "SELECT count(*) as CONTEGGIO FROM " + TABLE_NAME + " WHERE ";
            Enumeration whereFields = wheres.keys();
            int idx = 0;
            while (whereFields.hasMoreElements())
            {
                if (idx > 0)
                {
                    sqlString += "AND ";
                } else
                {
                    sqlString += " ";
                }
                idx++;
                String where = (whereFields.nextElement()).toString();
                if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                {
                    sqlString += " " + where + " is null ";
                } else
                {
                    sqlString += " " + where;
                    sqlString += " = ? ";
                }
            }
            log.debug("sqlString = [" + sqlString + "]");
            pstmt = dbConnection.compileStatement(sqlString);
            int paramIdx = 1;
            String whereValue;
            Enumeration whereFields_2ndRound = wheres.keys();
            while (whereFields_2ndRound.hasMoreElements())
            {
                try
                {
                    String where = whereFields_2ndRound.nextElement().toString();
                    if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                    {
                        log.debug("skipping null value for field: [" + where + "]");
                    } else
                    {
                        whereValue = wheres.get(where).toString();
                        pstmt.bindString(paramIdx, whereValue);
                        paramIdx++;
                    }
                } catch (Exception e)
                {
                    log.error("Could not set where condition");
                }
            }

            Cursor cursor = dbConnection.rawQuery(sqlString, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    //CONTEGGIO = rs.getString("CONTEGGIO");
                    CONTEGGIO = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return CONTEGGIO;
    }


    public List search(MetaDto requestDto, Hashtable wheres, SQLiteDatabase dbConnection, boolean definedAttributes, Integer resultRows)
    {
        List listOfDto = null;
        String sqlDialect = this.SQL_DIALECT;
        log.debug("BEGIN search()");
        try
        {
            if (sqlDialect.equals(SQLDialect.STANDARD))
            {
                listOfDto = searchSQLite(requestDto, wheres, dbConnection, definedAttributes, resultRows);
            } else if (sqlDialect.equals(SQLDialect.SQLITE))
            {
                listOfDto = searchSQLite(requestDto, wheres, dbConnection, definedAttributes, resultRows);
            }
            log.debug("END search()");
        } catch (Exception e)
        {
            log.error(e);
        }
        return listOfDto;
    }

    private List searchSQLite(MetaDto requestDto, Hashtable wheres, SQLiteDatabase dbConnection, boolean definedAttributes, Integer resultRows)
    {
        this.TABLE_NAME = requestDto.getTableName();
        List listOfDto = new ArrayList();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        try
        {
            //conn  = dbConnectionmanager.getConnection();
            String sqlString = "SELECT * FROM " + TABLE_NAME;
            int idx = 0;
            if (null != wheres)
            {
                sqlString += " WHERE ";
                Enumeration whereFields = wheres.keys();
                //WAS_OK::int idx=0;
                while (whereFields.hasMoreElements())
                {
                    if (idx > 0)
                    {
                        sqlString += "AND ";
                    } else
                    {
                        sqlString += " ";
                    }
                    idx++;
                    String where = (whereFields.nextElement()).toString();
                    if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                    {
                        sqlString += " " + where + " is null ";
                    } else
                    {
                        sqlString += " " + where;
                        sqlString += " = ? ";
                    }
                }
            }
            if (null != resultRows)
            {
                if (null == resultRows)
                {
                    log.debug("NO LIMITS");
                } else
                {
                    sqlString += " limit " + resultRows.toString();
                }
            }
            log.debug("sqlString = [" + sqlString + "]");
            //pstmt = dbConnection.compileStatement(sqlString);
            int paramIdx = 1;
            String whereValue;

            if (null != wheres)
            {
                Enumeration whereFields_2ndRound = wheres.keys();
                while (whereFields_2ndRound.hasMoreElements())
                {
                    try
                    {
                        String where = whereFields_2ndRound.nextElement().toString();
                        if (null == wheres.get(where) || (wheres.get(where).toString().equals("")))
                        {
                            log.debug("skipping null value for field: [" + where + "]");
                        } else
                        {
                            whereValue = wheres.get(where).toString();
                            pstmt.bindString(paramIdx, whereValue);
                            paramIdx++;
                        }
                    } catch (Exception e)
                    {
                        log.error("Could not set where condition");
                    }
                }
            }
            //rs = pstmt.executeQuery();
            Cursor cursor = dbConnection.rawQuery(sqlString, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    log.debug("rs.next()");
                    responseDto = new MetaDto();
                    Set<String> keys;
                    if (definedAttributes)
                    {
                        // requestDto contiene i nomi delle colonne da stampare
                        keys = requestDto.keySet();
                    } else
                    {
                        //ResultSetMetaData md = rs.getMetaData() ;
                        String columnNames[] = cursor.getColumnNames();
                        keys = new TreeSet<String>();
                        for (String colName : columnNames)
                        {
                            keys.add(colName);
                        }
                    }

                    Iterator<String> keysIterator2 = keys.iterator();
                    String key;
                    String value;
                    while (keysIterator2.hasNext())
                    {
                        key = keysIterator2.next();
					 /*
					 value = rs.getString(key);
					 responseDto.put(key, rs.getString(key));
					 */
                        int position = cursor.getColumnIndex(key);
                        value = cursor.getString(position);
                        responseDto.put(key, value);

                        //pstmt.setString(idx,   value);
                        log.debug("[" + key + "]=[" + value + "]");
                        idx++;
                    }
                    listOfDto.add(responseDto);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return listOfDto;
    }


    public boolean createTable(String tableName, List<String> fields, SQLiteDatabase dbConnection)
    {
        boolean isCreated = false;
        String sql = "CREATE TABLE " + tableName + " (";
        String firstField;
        String nthField;
        Iterator<String> fieldIterator = fields.iterator();
        if (fields.size() == 0)
        {
            return false;
        } else
        {
            firstField = fieldIterator.next();
            sql += " " + firstField + " text";
        }
        while (fieldIterator.hasNext())
        {
            nthField = fieldIterator.next();
            sql += ", " + nthField + " text";
        }
        sql += " );";
        try
        {
            SQLiteStatement pstmt = null;
            log.debug("SQL:[" + sql + "]");
            pstmt = dbConnection.compileStatement(sql);
            pstmt.execute();
            isCreated = true;
        } catch (Exception e)
        {
            log.error(e);
        }
        return isCreated;
    }


    public List<MetaDto> runQuery(String sqlInputString, SQLiteDatabase dbConnection)
    {

        List<MetaDto> listOfDto = new ArrayList<MetaDto>();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        try
        {
            //int idx=0;
            log.debug("sqlInputString = [" + sqlInputString + "]");
            //pstmt = dbConnection.compileStatement(sqlInputString);
            int paramIdx = 1;
            String whereValue;

            Cursor cursor = dbConnection.rawQuery(sqlInputString, null);
            //rs = pstmt.executeQuery();

            if (cursor.moveToFirst())
            {
                do
                {
                    log.debug("rs.next()");
                    responseDto = new MetaDto();
                    Set<String> keys; //NO DUPLICATES
                    String columnNames[] = cursor.getColumnNames();
                    //ResultSetMetaData md = rs.getMetaData() ;
                    keys = new TreeSet<String>();
				
			 		/* WAS::
			 		for( int i = 1; i <= md.getColumnCount(); i++ ){
						keys.add(md.getColumnLabel(i));
					}
			 		*/
                    for (String colName : columnNames)
                    {
                        keys.add(colName);
                    }
                    Iterator<String> keysIterator2 = keys.iterator();
                    String key;
                    String value;
                    while (keysIterator2.hasNext())
                    {
                        key = keysIterator2.next();
                        //WAS::  value = rs.getString(key);
                        int position = cursor.getColumnIndex(key);
                        value = cursor.getString(position);
                        responseDto.put(key, value);

                        log.debug("[" + key + "]=[" + value + "]");
                    }
                    listOfDto.add(responseDto);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return listOfDto;
    }

    public void execute(String sqlInputString, SQLiteDatabase dbConnection)
    {

        List<MetaDto> listOfDto = new ArrayList<MetaDto>();
        SQLiteStatement pstmt = null;
        MetaDto responseDto = null;
        ResultSet rs;
        try
        {
            //int idx=0;
            log.debug("sqlInputString = [" + sqlInputString + "]");
            //pstmt = dbConnection.compileStatement(sqlInputString);
            int paramIdx = 1;
            String whereValue;
            dbConnection.execSQL(sqlInputString);
        } catch (Exception e)
        {
            log.error(e.getMessage());
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
    }

    public void delete(MetaDto dto, Hashtable wheres, SQLiteDatabase dbConnection)
    {
        this.TABLE_NAME = dto.getTableName();
        log.debug("BEGIN insert()");
        MetaDto requestDto = (MetaDto) dto;
        //String uuid = clienteDto.getId();
        String primaryKey = dto.getIdField();
        //String pKeyValue = dto.getId();
        log.debug("BEGIN insert()");
        //Connection conn = null;
        SQLiteStatement pstmt = null;
        Set<String> keys = dto.keySet();
        Iterator<String> keysIterator_set = keys.iterator();

        String sql = "DELETE FROM " + TABLE_NAME + " ";
        int index = 0;
        String k;
        index = 1;
        Set<String> whereKeysP1 = wheres.keySet();
        Iterator<String> wheresIteratorP1 = whereKeysP1.iterator();
        String whereKey;
        String whereValue;
        boolean whereIteratorIsFirstP1 = true;
        while (wheresIteratorP1.hasNext())
        {
            whereKey = wheresIteratorP1.next();
            whereValue = wheres.get(whereKey).toString();
            if (whereIteratorIsFirstP1)
            {
                sql = sql + " where " + whereKey + " = ? ";
                whereIteratorIsFirstP1 = false;
            } else
            {
                sql = sql + "  AND " + whereKey + " = ? ";
            }
        }
        log.debug("SQL=[" + sql + "]");
        try
        {
            log.debug("BEFORE prepareStatement()");
            pstmt = dbConnection.compileStatement(sql);
            Iterator<String> keysIterator_parameter = keys.iterator();
            while (keysIterator_parameter.hasNext())
            {
                k = keysIterator_parameter.next();
                pstmt.bindString(index, (String) requestDto.get(k));
                index++;
            }
            Set<String> whereKeysP2 = wheres.keySet();
            Iterator<String> wheresIteratorP2 = whereKeysP2.iterator();

            while (wheresIteratorP2.hasNext())
            {
                whereKey = wheresIteratorP2.next();
                whereValue = wheres.get(whereKey).toString();
                pstmt.bindString(index, (String) wheres.get(whereKey));
                index++;
            }
            log.debug("AFTER prepareStatement()");
            log.debug("BEFORE execute()");
            pstmt.execute();
            log.debug("AFTER execute()");
        } catch (Exception e)
        {
            log.error("" + e);
        } finally
        {
            try
            {
                if (null != pstmt)
                {
                    pstmt.close();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
    }
}
