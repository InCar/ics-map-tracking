package com.incarcloud.business.service.impl.mysql;

import com.incarcloud.base.anno.ICSAutowire;
import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.dao.jdbc.JdbcDataAccess;
import com.incarcloud.base.dao.jdbc.RowHandler;
import com.incarcloud.base.anno.ICSComponent;
import com.incarcloud.base.anno.ICSDataSource;
import com.incarcloud.base.dao.jdbc.sql.SqlListResult;
import com.incarcloud.base.dao.jdbc.sql.SqlUtil;
import com.incarcloud.base.exception.BaseRuntimeException;
import com.incarcloud.base.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.base.page.Page;
import com.incarcloud.base.page.PageResult;
import com.incarcloud.business.data.GpsSplitSummary;
import com.incarcloud.business.service.GpsService;
import com.incarcloud.business.source.GpsSource;

import java.util.*;

@ICSDataSource(DataSource.JDBC)
@ICSComponent
public class GpsServiceImpl extends BaseComponent implements GpsService {
    public static long GPS_SPLIT_TIME_MILLS=1000*60*10;
    public static long EVERY_FETCH_DATA_NUM=300000;

    @ICSAutowire
    JdbcDataAccess dataAccess;

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
    public List<GpsSource> listByVin(String vin,Date startTime, Date endTime) {
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        String sql="select vin,lng,lat,direction,time from t_gps where vin=? and time>=? and time <=?";
        SqlListResult sqlListResult= SqlUtil.replaceNull(sql,Arrays.asList(vin,startTime,endTime));
        return dataAccess.list(sqlListResult.getSql(),rowHandler,sqlListResult.getParamList().toArray());
    }

    @Override
    public PageResult<GpsSource> pageByVin(String vin,Date startTime, Date endTime, Page page) {
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        String sql="select vin,lng,lat,direction,time from t_gps where vin=? limit ?,?";
        String countSql="select count(*) as num from t_gps where vin=? and time>=? and time <=?";
        SqlListResult sqlListResult1= SqlUtil.replaceNull(countSql,Arrays.asList(vin,startTime,endTime));
        SqlListResult sqlListResult2= SqlUtil.replaceNull(sql,Arrays.asList(vin,startTime,endTime));
        return dataAccess.page(sqlListResult1.getSql(),sqlListResult2.getSql(),rowHandler,page,sqlListResult1.getParamList().toArray());
    }


    @Override
    public List<List<GpsSource>> listSplit(String vin, Integer num, Date startTime, Date endTime,Integer order) {
        List<List<GpsSource>> resultList=new ArrayList<>();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        List<GpsSource> dataList=new ArrayList<>();
        StringBuilder sql=new StringBuilder("select vin,lng,lat,direction,time from t_gps where vin=? and time>=? and time <=?");
        if(order==1){
            sql.append(" order by time asc");
        }else if(order ==2){
            sql.append(" order by time desc");
        }
        sql.append(" limit ?,?");
        int count=0;
        while(true){
            //1、记录开始循环的索引
            int beginIndex=dataList.size()-1;
            beginIndex=beginIndex<0?0:beginIndex;
            //2、添加每次查询结果到总结果集中
            SqlListResult sqlListResult= SqlUtil.replaceNull(sql.toString(),Arrays.asList(vin,startTime,endTime));
            sqlListResult.getParamList().add(count*EVERY_FETCH_DATA_NUM);
            sqlListResult.getParamList().add(EVERY_FETCH_DATA_NUM);
            List<GpsSource> curDataList=dataAccess.list(sqlListResult.getSql(),rowHandler,sqlListResult.getParamList().toArray());
            dataList.addAll(curDataList);
            count++;
            //3、循环合并后的结果集,依次检测相邻的元素的时间差,如果大于设置时间差,则算作一段轨迹
            int size=dataList.size();
            if(size==0){
                return resultList;
            }else if(size==1){
                resultList.add(dataList);
                return resultList;
            }else {
                GpsSource data1=null;
                GpsSource data2;
                int index;
                int dataSize=dataList.size();
                for (index = beginIndex; index <= dataSize - 2; ) {
                    if(data1==null){
                        data1 = dataList.get(index);
                    }
                    data2 = dataList.get(index + 1);
                    long diff ;
                    if(order==1){
                        diff=data2.getTime().getTime() - data1.getTime().getTime();
                    }else if(order==2){
                        diff=data1.getTime().getTime() - data2.getTime().getTime();
                    }else{
                        throw BaseRuntimeException.getException("Param[order] Must Be 1(ASC) Or 2(DESC)");
                    }
                    if (diff > GPS_SPLIT_TIME_MILLS) {
                        //3.1、如果大于时间差,则添加到结果集中
                        resultList.add(new ArrayList<>(dataList.subList(0,index+1)));
                        if (resultList.size() == num) {
                            //3.2、如果结果集的长度已经达到要求的长度,则直接返回
                            return resultList;
                        }
                    }else{
                        index++;
                    }
                }
                //4、如果循环完了,还是没有收集到传入参数的数量,检查是否还有数据,有则继续查,无则余下算做最后一段
                if (curDataList.size() < EVERY_FETCH_DATA_NUM) {
                    resultList.add(dataList);
                    return resultList;
                }else{
                    //5、如果没有循环完,则保留有效数据
                    dataList = new ArrayList<>(dataList.subList(index + 1, dataList.size()));
                }
            }
            //6、如果是逆序,则需要倒转每一个数据集的内容
            if(order==2){
                resultList.forEach(e->Collections.reverse(e));
            }
        }
    }

    @Override
    public List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime, Integer order) {
        List<GpsSplitSummary> resultList=new ArrayList<>();
        List<GpsSource> dataList=new ArrayList<>();
        StringBuilder sql=new StringBuilder("select lng,lat,time from t_gps where vin=? and time>=? and time <=?");
        if(order==1){
            sql.append(" order by time asc");
        }else if(order ==2){
            sql.append(" order by time desc");
        }
        sql.append(" limit ?,?");
        int count=0;
        while(true){
            //1、记录开始循环的索引
            int beginIndex=dataList.size()-1;
            beginIndex=beginIndex<0?0:beginIndex;
            //2、添加每次查询结果到总结果集中
            SqlListResult sqlListResult= SqlUtil.replaceNull(sql.toString(),Arrays.asList(vin,startTime,endTime));
            sqlListResult.getParamList().add(count*EVERY_FETCH_DATA_NUM);
            sqlListResult.getParamList().add(EVERY_FETCH_DATA_NUM);
            List<GpsSource> curDataList=dataAccess.list(sqlListResult.getSql(),rs->{
                GpsSource gpsSource=new GpsSource();
                Double lng=rs.getDouble("lng");
                Double lat=rs.getDouble("lat");
                Date time=new Date(rs.getTimestamp("time").getTime());
                gpsSource.setLng(lng);
                gpsSource.setLat(lat);
                gpsSource.setTime(time);
                return gpsSource;
            },sqlListResult.getParamList().toArray());
            dataList.addAll(curDataList);
            count++;
            //3、循环合并后的结果集,依次检测相邻的元素的时间差,如果大于设置时间差,则算作一段轨迹
            int size=dataList.size();
            if(size==0){
                return resultList;
            }else if(size==1){
                GpsSource data= dataList.get(0);
                resultList.add(new GpsSplitSummary(data.getTime(),data.getTime(),1,new double[]{data.getLng(),data.getLat()},new double[]{data.getLng(),data.getLat()}));
                return resultList;
            }else {
                GpsSource data1=null;
                GpsSource data2;
                int index;
                int dataSize=dataList.size();
                for (index = beginIndex; index <= dataSize - 2; ) {
                    if(data1==null){
                        data1 = dataList.get(index);
                    }
                    data2 = dataList.get(index + 1);
                    long diff=0 ;
                    if(order==1){
                        diff=data2.getTime().getTime() - data1.getTime().getTime();
                    }else if(order==2){
                        try {
                            diff = data1.getTime().getTime() - data2.getTime().getTime();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }else{
                        throw BaseRuntimeException.getException("Param[order] Must Be 1(ASC) Or 2(DESC)");
                    }
                    if (diff > GPS_SPLIT_TIME_MILLS) {
                        //3.1、如果大于时间差,则添加到结果集中
                        GpsSource startData=dataList.get(0);
                        GpsSource endData=data1;
                        resultList.add(new GpsSplitSummary(startData.getTime(),endData.getTime(),index+1,new double[]{startData.getLng(),startData.getLat()},new double[]{endData.getLng(),endData.getLat()}));
                        if (resultList.size() == num) {
                            //3.2、如果结果集的长度已经达到要求的长度,则直接返回
                            return resultList;
                        }
                    }else{
                        index++;
                    }
                    data1=data2;
                }
                //4、如果循环完了,还是没有收集到传入参数的数量,检查是否还有数据,有则继续查,无则余下算做最后一段
                if (curDataList.size() < EVERY_FETCH_DATA_NUM) {
                    int curSize=dataList.size();
                    GpsSource startData=dataList.get(0);
                    GpsSource endData=dataList.get(dataSize-1);
                    resultList.add(new GpsSplitSummary(startData.getTime(),endData.getTime(),curSize,new double[]{startData.getLng(),startData.getLat()},new double[]{endData.getLng(),endData.getLat()}));
                    return resultList;
                }else{
                    //5、如果没有循环完,则保留有效数据
                    dataList = new ArrayList<>(dataList.subList(index + 1, dataSize));
                }

            }
        }
    }

}
