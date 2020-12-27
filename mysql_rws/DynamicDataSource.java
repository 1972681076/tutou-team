package net.fuzui.StudentInfo.mysql_rws;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	//写数据源
	private Object writeDataSource;
	//读数据源
	private Object readDataSource;
	
	@Override
	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
		if(this.writeDataSource == null) {
			throw new IllegalArgumentException("Property writeDataSource is required");
		}
		setDefaultTargetDataSource(writeDataSource);
		Map<Object,Object> targetDataSource = new HashMap<>();
		targetDataSource.put(DynamicDataSourceGlobal.WRITE.name(), writeDataSource);
		if(readDataSource != null) {
			targetDataSource.put(DynamicDataSourceGlobal.READ.name(), readDataSource);
		}
		setTargetDataSources(targetDataSource);
		super.afterPropertiesSet();
	}

	//确定当前查找键
	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		DynamicDataSourceGlobal dataSourceGlobal = DynamicDataSourceHolder.getDataSource();
		if(dataSourceGlobal == null || dataSourceGlobal == DynamicDataSourceGlobal.WRITE) {
			return DynamicDataSourceGlobal.WRITE.name();
		}
		return DynamicDataSourceGlobal.READ.name();
	}

	public Object getWriteDataSource() {
		return writeDataSource;
	}

	public void setWriteDataSource(Object writeDataSource) {
		this.writeDataSource = writeDataSource;
	}

	public Object getReadDataSource() {
		return readDataSource;
	}

	public void setReadDataSource(Object readDataSource) {
		this.readDataSource = readDataSource;
	}

}
