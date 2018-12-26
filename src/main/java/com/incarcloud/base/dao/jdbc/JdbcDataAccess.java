package com.incarcloud.base.dao.jdbc;

import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSConditionalOnMissingBean;
import com.incarcloud.base.anno.ICSDataSource;
import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.config.JdbcConfig;
import com.incarcloud.base.context.Context;
import com.incarcloud.base.context.Initializable;
import com.incarcloud.base.dao.DataAccess;
import com.incarcloud.base.exception.BaseRuntimeException;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@ICSComponent
@ICSDataSource(DataSource.JDBC)
@ICSConditionalOnMissingBean(DataAccess.class)
public class JdbcDataAccess implements DataAccess<Connection>,Initializable{
    public final static String MYSQL_DRIVER_CLASS_NAME="com.mysql.jdbc.Driver";
    public final static String MYSQL_DRIVER_CLASS_NAME_8="com.mysql.cj.jdbc.Driver";
    public final static long WARNING_SQL_TIME_MILLS=1000L;

    protected Context context;
    protected LinkedBlockingDeque<Connection> pool;

    @Override
    public <R>R doInConnection(Function<Connection,R> function) {
        JdbcConfig jdbcConfig= context.getConfig().getJdbcConfig();
        try {
            Connection connection= pool.pollLast(jdbcConfig.getMaxWaitSeconds(), TimeUnit.SECONDS);
            if(connection==null){
                String msg="Wait Jdbc Pool More Than ["+jdbcConfig.getMaxWaitSeconds()+"],No Available Connection";
                throw BaseRuntimeException.getException(msg);
            }else{
                try {
                    return function.apply(connection);
                }finally {
                    pool.addFirst(connection);
                }
            }
        } catch (InterruptedException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    @Override
    public void init(Context context) {
        this.context=context;
        try {
            if(pool==null){
                synchronized (this) {
                    if(pool==null) {
                        pool=new LinkedBlockingDeque<>();
                        JdbcConfig mysqlConfig = context.getConfig().getJdbcConfig();
                        Integer poolSize = mysqlConfig.getPoolSize();
                        for (int i = 1; i <= poolSize; i++) {
                            pool.addFirst(getConnection());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        JdbcConfig jdbcConfig= context.getConfig().getJdbcConfig();
        try {
            Class.forName(jdbcConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw BaseRuntimeException.getException(e);
        }
        return DriverManager.getConnection(jdbcConfig.getUrl(),jdbcConfig.getUser(),jdbcConfig.getPassword());
    }

    public <T>List<T> list(String sql, RowHandler<T> rowHandler, Object ... params){
        return doInConnection(connection -> {
            try(PreparedStatement ps=connection.prepareStatement(sql)){
                List<T> resultList=new ArrayList<>();
                if(params!=null&&params.length>0){
                    for(int i=1;i<=params.length;i++){
                        ps.setObject(i,params[i-1]);
                    }
                }
                long t1=System.currentTimeMillis();
                ResultSet rs= ps.executeQuery();
                long t2=System.currentTimeMillis();
                long diff=t2-t1;
                if(diff>WARNING_SQL_TIME_MILLS){
                    context.getConfig().getLogger().warning("Sql["+sql+"] Take "+diff);
                }
                while(rs.next()){
                    T t= rowHandler.apply(rs);
                    resultList.add(t);
                }
                return resultList;
            } catch (SQLException e) {
                throw BaseRuntimeException.getException(e);
            }
        });
    }

    public <T>PageResult<T> page(String countSql, String sql, RowHandler<T> rowHandler, Page page, Object ... params){
        return doInConnection(connection -> {
            int count;
            try{
                try (PreparedStatement countPs = connection.prepareStatement(countSql)) {
                    if (params != null && params.length > 0) {
                        for (int i = 1; i <= params.length; i++) {
                            countPs.setObject(i, params[i]);
                        }
                    }
                    long t1=System.currentTimeMillis();
                    ResultSet countRs = countPs.executeQuery();
                    long t2=System.currentTimeMillis();
                    long diff=t2-t1;
                    if(diff>WARNING_SQL_TIME_MILLS){
                        context.getConfig().getLogger().warning("CountSql["+countSql+"] Take "+diff);
                    }
                    countRs.next();
                    count = countRs.getInt(1);
                }
                if (count == 0) {
                    return new PageResult<>(new ArrayList<>(), 0);
                } else {
                    try(PreparedStatement ps = connection.prepareStatement(sql)) {
                        int len = 0;
                        if (params != null && (len = params.length) > 0) {
                            for (int i = 1; i <= params.length; i++) {
                                ps.setObject(i, params[i - 1]);
                            }
                        }
                        ps.setObject(len + 1, (page.getPageNum() - 1) * page.getPageSize());
                        ps.setObject(len + 2, page.getPageSize());
                        long t1=System.currentTimeMillis();
                        ResultSet rs = ps.executeQuery();
                        long t2=System.currentTimeMillis();
                        long diff=t2-t1;
                        if(diff>WARNING_SQL_TIME_MILLS){
                            context.getConfig().getLogger().warning("Sql["+sql+"] Take "+diff);
                        }
                        List<T> dataList = new ArrayList<>();
                        while (rs.next()) {
                            T t = rowHandler.apply(rs);
                            dataList.add(t);
                        }
                        return new PageResult<>(dataList, count);
                    }
                }
            }catch (SQLException e) {
                throw BaseRuntimeException.getException(e);
            }
        });
    }

}
