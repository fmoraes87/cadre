package com.cadre.server.core.process.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SvrProcess;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POInfoColumn;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ProcessInfoParameter;
import com.cadre.server.core.process.SvrProcessException;
import com.cadre.server.core.service.ModelService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@SvrProcess(ImportDataFile.PROCESS_NAME)
public class ImportDataFile extends CadreProcess {
	
	private static final String ERROR_INVALID_FILE = "Invalid file";


	private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataFile.class);


	static final String PROCESS_NAME = "importDataFile";
	static final String PARAM_ENTITY_NAME = "entityName";
	static final String PARAM_FILE_CONTENT = "fileContent";

	
	private String p_TableName;
	private String p_FileContent;
	
	private ModelService genericService;

	public ImportDataFile() {
		this.genericService = CDI.current().select(ModelService.class).get();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getProcessName() {
		return PROCESS_NAME;
	}
		

	
	@Override
	public void prepare() {
		ProcessInfoParameter[] para = getParams();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getName();
			if (para[i].getValue() == null)
				;
			else if (name.equalsIgnoreCase(PARAM_ENTITY_NAME))
				p_TableName = (String) para[i].getValue();
			else if (name.equalsIgnoreCase(PARAM_FILE_CONTENT))
				p_FileContent = (String) para[i].getValue();
			else
				LOGGER.warn("Unknown Parameter: " + name);
		}
		
		
	}

	@Override
	public Object doIt(String trxName){
		byte [] decoded = Base64.getDecoder().decode(p_FileContent);
		List<String[]> r;		
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
		try ( Reader targetReader = new StringReader(new String(decoded));
				CSVReader reader = new CSVReaderBuilder(targetReader).withCSVParser(csvParser).build()) {
			
		      r = reader.readAll();

		} catch (IOException ex) {
			LOGGER.error("Error processing content file", ex);
			throw new SvrProcessException(Status.BAD_REQUEST.getStatusCode(),ERROR_INVALID_FILE);
		} 
		
		int listIndex = 0;
		String[] header= {};
		List<String> identifiers =new ArrayList<>();
		POInfo poInfo = genericService.getPOInfo(p_TableName);
		
		for (String[] arrays : r) {
			LOGGER.info("\nString[" + listIndex + "] : " + Arrays.toString(arrays));
			
			if (listIndex==0) {
				header = arrays;
				for (final String h : header) {
					if (StringUtils.isNotBlank(h)) {
						POInfoColumn c = poInfo.getPOInfoColumn(h);
						if (c!=null && c.isNaturalKey()) {
							identifiers.add(h);
						}else if (c==null) {
							LOGGER.warn("Invalid column: " + h);
							throw new SvrProcessException(Status.BAD_REQUEST.getStatusCode(), "Invalid column: " + h);
						}
					}
				}
				
			}else {
				Map<String, Object> values = new HashMap<>();
				for (int index=0; index < header.length; index++) {
					values.put(header[index], arrays[index]);
				}	
				
				if (CollectionUtils.isNotEmpty(identifiers)) {
					
					JDBCQueryImpl.Builder queryBuilder = new JDBCQueryImpl.Builder(p_TableName);
					for (final String propertie : identifiers) {
						final Object value = values.get(propertie);
						queryBuilder.and(GenericCondition.equals(propertie, value));
					}
					
					SearchResult<? extends POModel> searchPO = genericService.search(trxName, queryBuilder.build());
					POModel poModel = searchPO.getSingleResult(false);
					if (poModel!=null) {
						setPOValues(poInfo, values, poModel);
					}else {
						createEntity(trxName,poInfo,values);
					}
					
					
				}else {
					createEntity(trxName,poInfo,values);					
				}
				
			}
			
			listIndex++;
			
			
		}
		
		return null;
	}
	
	private void createEntity(String trxName, POInfo poInfo,Map<String, Object> columnValues) {
		POModel poModel = genericService.createPO(trxName, p_TableName);
		
		setPOValues(poInfo, columnValues, poModel);
	}



	private void setPOValues(POInfo poInfo, Map<String, Object> columnValues, POModel poModel) {
		columnValues.forEach((propertieName, propertieValue) -> {
			if (!propertieName.equals(p_TableName + "_ID")) {
				int index = poInfo.getColumnIndex(propertieName);
				if (index >= 0) {
					if (poInfo.getPOInfoColumn(propertieName).isUpdatable) {
						poModel.setValueOfColumn(propertieName, propertieValue);
					}else {
						LOGGER.warn("Column is not updatable: " + propertieName);
					}
					
				}else {
					LOGGER.warn("Column not found: " + propertieName);
				}

			}
		
		});



		genericService.save(poModel);
	}

	

}
