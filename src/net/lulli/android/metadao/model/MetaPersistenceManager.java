package net.lulli.android.metadao.model;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import net.lulli.android.metadao.ALog;
import net.lulli.android.metadao.DbConnectionManager;
import net.lulli.android.metadao.MetaDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class MetaPersistenceManager extends SQLDialect{
	
	ALog log = new ALog(this.getClass().getName());
	String tableName;
	String dataBaseName;
	String SQL_DIALECT = SQLDialect.STANDARD;
	Context context;
	
	public  DbConnectionManager getDbConnectionManager(){
		log.debug("getDbConnectionManager(..) BEGIN");
		DbConnectionManager dbConnectionManager = new DbConnectionManager(context, dataBaseName);
		log.debug("dataBaseName:"+dataBaseName);
		log.debug("getDbConnectionManager(..) END");
		return dbConnectionManager;
	}
	
	
	public MetaPersistenceManager(Context context, String dataBaseName){
		log.debug("MetaPersistenceManager(..) BEGIN");
		this.context = context;
		this.dataBaseName = dataBaseName;
		log.debug("dataBaseName:"+dataBaseName);
		log.debug("MetaPersistenceManager(..) END");
	}
	
	
	//Simplified Version
	public List search(MetaDto requestDto, Hashtable wheres){
		return  search( requestDto,  wheres,   false,  null);
	}
	
	public List search(MetaDto requestDto, Hashtable wheres,  boolean definedAttributes, Integer resultRows){
		log.trace("BEGIN search");
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		String codeId = null;
		List results = null;
		
		String sqlDialect = this.SQL_DIALECT;
		try{
			log.trace("BEFORE conn");
			 conn = dbManager.getConnection();
			 log.trace("AFTER conn");
			 dao = new MetaDao();
			 if (null != sqlDialect){
				 dao.setSqlDialect(sqlDialect);
			 }
			 results = dao.search(requestDto, wheres, conn, definedAttributes, resultRows);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
			return results;
		}
	
	
	public  void insert(MetaDto dto){
		//DbManager dbManager = DbManager.getInstance();
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		try{
			 this.tableName = dto.getTableName();
			 conn = dbManager.getConnection();
			 log.trace("new MetaDao with tableName: [" +tableName +"]");
			 dao = new MetaDao();
			 dao.insert(dto, conn);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
	}
	
	public void update(MetaDto dto, Hashtable wheres){
		//DbManager dbManager = DbManager.getInstance();
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		try{
			 this.tableName = dto.getTableName();
			 conn = dbManager.getConnection();
			 dao = new MetaDao();
			 dao.update(dto, wheres, conn);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
	}
	
	public void delete(MetaDto dto, Hashtable wheres){
		//DbManager dbManager = DbManager.getInstance();
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		try{
			 this.tableName = dto.getTableName();
			 conn = dbManager.getConnection();
			 dao = new MetaDao();
			 dao.delete(dto, wheres, conn);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
	}
	
	
	
	public String save(MetaDto dto,  Hashtable wheres){
		//1 Verifica se  e' presente
			boolean isPresent = false;
			String tableName = dto.getTableName();
			String id="";
			SQLiteDatabase conn = null;
			//DbManager dbManager = DbManager.getInstance();
			DbConnectionManager dbManager = getDbConnectionManager();
			MetaDao dao = null;
			try{
			dao = new MetaDao();
			conn = dbManager.getConnection();
			String presenti = dao.selectCount(dto, wheres, conn, false);
			log.trace("TABLE:" +dto.getTableName());
		//isPresent = pm.isPresentByField(tableName, id_Azienda, codField, codValue);
			log.trace("[1]: CHECK PRESENCE");
			if (Integer.valueOf(presenti) > 0){
				//Deve fare update
				
				///////////
				if (dto.containsKey(null)){
					dto.remove(null);
					log.trace("METADAO_updateWhere: REMOVE_NULL");
				}
				//////////
				dao.update(dto, wheres, conn);
			} else {
		//3 Se non presente inserisce
			//pm.insert(dto);
			insert(dto);
			log.trace("[3]: INSERT");
		}
		//4 restituisce id inserito
			} catch (Exception e) {
				log.error("Insert/Update FALLITA" +e );
			} 
			try{
			//pm.select
			id = dao.selectIdWhere(dto, wheres, conn, false, 1);
		} catch (Exception e) {
			log.error("Cannot decode record from table=["+tableName+"]");
		}finally{
			dbManager.releaseConnection(conn);
		}
		log.trace("[4]: RETURN_ID");
		return id;
	}
	
	public Integer selectCount(MetaDto requestDto, Hashtable wheres, boolean definedAttributes ){	
		//DbManager dbManager = DbManager.getInstance();
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		Integer count = 0;
		try{
			conn = dbManager.getConnection();
			dao = new MetaDao();
			count = Integer.valueOf(dao.selectCount(requestDto, wheres, conn, definedAttributes));
		}catch (Exception e) {
			log.error(""+e);
		}
		return count;
	}
	
	
	public List<String> descTable(String tableName){
		List<String> fieldList = null;
		String sqlDialect = this.getSQLDialect();
		log.trace("Detected SQLDialect:[" +sqlDialect+"]");
		
		if(sqlDialect.equals(STANDARD)){
			fieldList = descTableMySQL(tableName);
		}else if(sqlDialect.equals(SQLITE)){
			fieldList = descTableMySQL(tableName);
		} 
		return fieldList;
	}
	
	private List<String> descTableMySQL(String tableName){
		List<String> fieldList = new ArrayList<String>();
		try{
			MetaDto requestDto = MetaDto.getNewInstance(tableName);
			requestDto.setChronology(false);
			Hashtable wheres = null;
			List<MetaDto> risultati = search( requestDto, null,  false , null );
			MetaDto rigaZero = risultati.get(0); //BAD: funziona solo se la tabella ha almeno 1 record!!
			Set<String> keys = rigaZero.keySet();
			Iterator<String> iter = keys.iterator();
			String field;
			while (iter.hasNext()){
				field = iter.next();
				fieldList.add(field);
			}
		}catch (Exception e) {
			log.error(""+e);
		}
		return fieldList;
	}
	
	
	
	
	
	public boolean createTable(String tableName,List<String> fields){
		log.debug("BEGIN createTable()");
		boolean created = false;
		DbConnectionManager dbManager;
		MetaDao dao;
		SQLiteDatabase conn;
		log.debug("createTable BEGIN");
		try{
			dbManager = getDbConnectionManager();
			log.debug("A");
			conn = dbManager.getConnection();
			log.debug("B");
			dao = new MetaDao();
			log.debug("C");
			created = dao.createTable(tableName, fields, conn);
			log.debug("D");
		}catch (Exception e) {
			log.error(""+e);
		}
		log.debug("createTable END");
		return created;
	}
	
	public boolean dropTable(String tableName){
		boolean dropped = false;
		DbConnectionManager dbManager;
		MetaDao dao;
		SQLiteDatabase conn;
		try{
			dbManager = getDbConnectionManager();
			conn = dbManager.getConnection();
			dao = new MetaDao();
			dropped = dao.dropTable(tableName, conn);
		}catch (Exception e) {
			log.error(""+e);
		}
		return dropped;
	}
	public String getSQLDialect() {
		return SQL_DIALECT;
	}
	public void setSQLDialect(String sqlDialect) {
		SQL_DIALECT = sqlDialect;
	}
	
	public List<MetaDto> runQuery(String sqlInputString){
		log.trace("BEGIN runQuery");
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		List<MetaDto> results = null;
		
		String sqlDialect = this.SQL_DIALECT;
		try{
			log.trace("BEFORE conn");
			 conn = dbManager.getConnection();
			 log.trace("AFTER conn");
			 dao = new MetaDao();
			 if (null != sqlDialect){
				 dao.setSqlDialect(sqlDialect);
			 }
			 results = dao.runQuery(sqlInputString, conn);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
			return results;
		}
	
	public void execute(String sqlInputString){
		log.trace("BEGIN execute");
		DbConnectionManager dbManager = getDbConnectionManager();
		SQLiteDatabase conn = null;
		MetaDao dao;
		List<MetaDto> results = null;
		
		String sqlDialect = this.SQL_DIALECT;
		try{
			log.trace("BEFORE conn");
			 conn = dbManager.getConnection();
			 log.trace("AFTER conn");
			 dao = new MetaDao();
			 if (null != sqlDialect){
				 dao.setSqlDialect(sqlDialect);
			 }
			 dao.execute(sqlInputString, conn);
		}	catch (Exception e) {
			log.trace(e.getMessage());
		} finally {
			dbManager.releaseConnection(conn);
		}
			
		}
	
}
