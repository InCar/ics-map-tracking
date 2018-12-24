package com.incarcloud.business.service.impl.mysql;

import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.config.MysqlConfig;
import com.incarcloud.base.db.mysql.DBUtil;
import com.incarcloud.base.db.mysql.RowHandler;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSDataSource;
import com.incarcloud.base.db.sql.SqlListResult;
import com.incarcloud.base.db.sql.SqlUtil;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.service.GpsService;
import com.incarcloud.business.source.GpsSource;

import java.util.*;

@ICSDataSource(DataSource.MYSQL)
@ICSComponent
public class GpsServiceImpl extends BaseComponent implements GpsService {
    public RowHandler<GpsSource> getGpsSourceRowHandler(){
        return rs->{
            String vin=rs.getString("vin");
            Float direction=rs.getFloat("direction");
            Double lng=rs.getDouble("lng");
            Double lat=rs.getDouble("lat");
            Date time=new Date(rs.getTimestamp("time").getTime());
            return new GpsSource(vin,lng,lat,direction,time);
        };
    }

    @Override
    public List<GpsSource> listByVin(String vin) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(vin==null){
            String sql="select vin,lng,lat,direction,time from t_gps";
            return DBUtil.list(mysqlConfig,sql,rowHandler);
        }else{
            String sql="select vin,lng,lat,direction,time from t_gps where vin=?";
            return DBUtil.list(mysqlConfig,sql,rowHandler,vin);
        }
    }

    @Override
    public PageResult<GpsSource> pageByVin(String vin, Page page) {
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        if(vin==null){
            String countSql="select count(*) as num from t_gps";
            String sql="select vin,lng,lat,direction,time from t_gps limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page);
        }else{
            String countSql="select count(*) as num from t_gps where vin=?";
            String sql="select vin,lng,lat,direction,time from t_gps where vin=? limit ?,?";
            return DBUtil.page(mysqlConfig,countSql,sql,rowHandler,page,vin);
        }
    }

    @Override
    public List<List<GpsSource>> listSplit(String vin, Integer num, Date startTime, Date endTime) {
        List<List<GpsSource>> resultList=new ArrayList<>();
        MysqlConfig mysqlConfig= config.getMysqlConfig();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        int interval=60*1000;
        int count=1000;
        List<GpsSource> dataList=new ArrayList<>();
        String sql="select vin,lng,lat,direction,time from t_gps where vin=? and time>=? and time <=? limit ?,?";
        int index=0;
        while(true){
            //1、记录开始循环的索引
            int beginIndex=dataList.size()-1;
            beginIndex=beginIndex<0?0:beginIndex;
            //2、添加每次查询结果到总结果集中
            SqlListResult sqlListResult= SqlUtil.replaceNull(sql,Arrays.asList(vin,startTime,endTime));
            sqlListResult.getParamList().add(index*count);
            sqlListResult.getParamList().add(count);
            List<GpsSource> curDataList=DBUtil.list(mysqlConfig,sqlListResult.getSql(),rowHandler,sqlListResult.getParamList().toArray());
            dataList.addAll(curDataList);
            index++;
            //3、循环合并后的结果集,依次检测相邻的元素的时间差,如果大于设置时间差,则算作一段轨迹
            int size=dataList.size();
            if(size==0){
                return resultList;
            }else if(size==1){
                resultList.add(dataList);
                return resultList;
            }else {
                for (int i = beginIndex; i <= dataList.size() - 2; ) {
                    GpsSource data1 = dataList.get(i);
                    GpsSource data2 = dataList.get(i + 1);
                    long diff = data2.getTime().getTime() - data1.getTime().getTime();
                    if (diff > interval) {
                        //3.1、如果大于时间差,则添加到结果集中
                        resultList.add(new ArrayList<>(dataList.subList(0,i+1)));
                        if (resultList.size() == num) {
                            //3.2、如果结果集的长度已经达到要求的长度,则直接返回
                            return resultList;
                        } else {
                            //3.3、否则设置dataList为余下的元素集合并重置索引
                            dataList = new ArrayList<>(dataList.subList(i + 1, dataList.size()));
                            i = 0;
                        }
                    }else{
                        i++;
                    }
                }
                //4、如果循环完了,还是没有收集到传入参数的数量,检查是否还有数据,有则继续查,无则余下算做最后一段
                if (curDataList.size() < count) {
                    resultList.add(dataList);
                    return resultList;
                }
            }
        }
    }

}
