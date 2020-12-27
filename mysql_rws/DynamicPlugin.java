package net.fuzui.StudentInfo.mysql_rws;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;

@Intercepts({
	@Signature(type = Executor.class,method = "update",args = { MappedStatement.class,Object.class}),
	@Signature(type = Executor.class,method = "query",args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})
})
public class DynamicPlugin implements Interceptor {
	
	private Logger log = LoggerFactory.getLogger(DynamicPlugin.class);
	private static final Map<String,DynamicDataSourceGlobal> cacheMap = new ConcurrentHashMap<>();
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// TODO Auto-generated method stub
		boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
		if(!synchronizationActive) {
			Object[] object = invocation.getArgs();
			MappedStatement ms = (MappedStatement) object[0];
			DynamicDataSourceGlobal dynamicDataSourceGlobal = null;
			if((dynamicDataSourceGlobal = cacheMap.get(ms.getId())) == null) {
				if(ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
					if(ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
						dynamicDataSourceGlobal = DynamicDataSourceGlobal.WRITE;
					}else {
						dynamicDataSourceGlobal = DynamicDataSourceGlobal.READ;
					}
				}else {
					dynamicDataSourceGlobal = DynamicDataSourceGlobal.WRITE;
				}
				System.out.println("设置方法[{"+ms.getId()+"}] use [{"+dynamicDataSourceGlobal.name()+"}] Strategy, SqlCommandType [{"+ms.getSqlCommandType().name()+"}]..");
				log.warn("设置方法[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), dynamicDataSourceGlobal.name(), ms.getSqlCommandType().name());
				cacheMap.put(ms.getId(), dynamicDataSourceGlobal);
			}
			DynamicDataSourceHolder.putDataSource(dynamicDataSourceGlobal);
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
	}
}
