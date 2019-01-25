package com.incarcloud.maptracking.service.impl.mysql;

import com.incarcloud.skeleton.anno.ICSAutowire;
import com.incarcloud.skeleton.anno.ICSConditionalOnMissingBean;
import com.incarcloud.skeleton.config.DataSource;
import com.incarcloud.skeleton.dao.jdbc.JdbcDataAccess;
import com.incarcloud.skeleton.dao.jdbc.RowHandler;
import com.incarcloud.skeleton.anno.ICSComponent;
import com.incarcloud.skeleton.anno.ICSDataSource;
import com.incarcloud.skeleton.dao.jdbc.sql.SqlListResult;
import com.incarcloud.skeleton.dao.jdbc.sql.SqlUtil;
import com.incarcloud.skeleton.exception.BaseRuntimeException;
import com.incarcloud.skeleton.handler.dynamicrequest.component.BaseComponent;
import com.incarcloud.skeleton.page.Page;
import com.incarcloud.skeleton.page.PageResult;
import com.incarcloud.maptracking.data.GpsSplitSummary;
import com.incarcloud.maptracking.service.GpsService;
import com.incarcloud.maptracking.source.GpsSource;

import java.util.*;

@ICSDataSource(DataSource.JDBC)
@ICSComponent
@ICSConditionalOnMissingBean(GpsService.class)
public class GpsServiceImpl extends BaseComponent implements GpsService {
    public static long EVERY_FETCH_DATA_NUM=100000;

    @ICSAutowire
    JdbcDataAccess dataAccess;

    public RowHandler<GpsSource> getGpsSourceRowHandler(){
        return rs->{
            String vin=rs.getString("vin");
            Float direction=rs.getFloat("direction");
            Double lng=rs.getDouble("lng");
            Double lat=rs.getDouble("lat");
            Date time=new Date(rs.getTimestamp("time").getTime());
            Integer speed = rs.getInt("speed");
            return new GpsSource(vin,lng,lat,direction,time,speed);
        };
    }

    @Override
    public List<GpsSource> list(String vin, Date startTime, Date endTime) {
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        String sql="select vin,lng,lat,direction,time,speed from t_gps where vin=? and time>=? and time <=?";
        return dataAccess.list(sql,rowHandler,vin,startTime,endTime);
    }

    @Override
    public PageResult<GpsSource> page(String vin, Date startTime, Date endTime, Page page) {
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        String countSql="select count(*) as num from t_gps where vin=? and time>=? and time <=?";
        String sql="select vin,lng,lat,direction,time,speed from t_gps where vin=? and time>=? and time <=? limit ?,?";
        return dataAccess.page(countSql,sql,rowHandler,page,vin,startTime,endTime);
    }


    @Override
    public List<List<GpsSource>> listSplit(String vin, Integer num, Date startTime, Date endTime,Long gpsSplitTimeMills,Integer order) {
        List<List<GpsSource>> resultList=new ArrayList<>();
        RowHandler<GpsSource> rowHandler=getGpsSourceRowHandler();
        List<GpsSource> dataList=new ArrayList<>();
        StringBuilder sql=new StringBuilder("select vin,lng,lat,direction,speed,time from t_gps where vin=? and time>=? and time <=?");
        if(order==1){
            sql.append(" order by time asc");
        }else if(order ==2){
            sql.append(" order by time desc");
        }else{
            throw BaseRuntimeException.getException("Param[order] Must Be 1(ASC) Or 2(DESC)");
        }
        sql.append(" limit ?,?");
        int count=0;
        while(true){
            //1、记录开始循环的索引
            int beginIndex=dataList.size()-1;
            beginIndex=beginIndex<0?0:beginIndex;
            //2、添加每次查询结果到总结果集中
            SqlListResult sqlListResult= SqlUtil.replaceNull(sql.toString(),Arrays.asList(vin,startTime,endTime));
            List<GpsSource> curDataList=dataAccess.list(sqlListResult.getSql(),rowHandler,vin,startTime,endTime,count*EVERY_FETCH_DATA_NUM,EVERY_FETCH_DATA_NUM);
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
                int index1=0;
                int index2;
                int dataSize=dataList.size();
                for (index2 = beginIndex; index2 <= dataSize - 2; ) {
                    if(data1==null){
                        data1 = dataList.get(index2);
                    }
                    data2 = dataList.get(index2 + 1);
                    long diff ;
                    if(order==1){
                        diff=data2.getTime().getTime() - data1.getTime().getTime();
                    }else{
                        diff=data1.getTime().getTime() - data2.getTime().getTime();
                    }
                    if (diff > gpsSplitTimeMills) {
                        //3.1、如果大于时间差,则添加到结果集中
                        resultList.add(new ArrayList<>(dataList.subList(index1,index2+1)));
                        index1=index2+1;
                        if (resultList.size() == num) {
                            //3.2、如果结果集的长度已经达到要求的长度,则直接返回
                            return resultList;
                        }
                    }else{
                        index2++;
                    }
                    data1=data2;
                }
                //4、如果循环完了,还是没有收集到传入参数的数量,检查是否还有数据,有则继续查,无则余下算做最后一段
                if (curDataList.size() < EVERY_FETCH_DATA_NUM) {
                    resultList.add(new ArrayList<>(dataList.subList(index1,index2)));
                    return resultList;
                }else{
                    //5、如果没有循环完,则保留有效数据
                    dataList = new ArrayList<>(dataList.subList(index1, index2));
                }
            }

        }
    }

    @Override
    public List<GpsSplitSummary> listSplitSummary(String vin, Integer num, Date startTime, Date endTime,Long gpsSplitTimeMills, Integer order) {
        List<GpsSplitSummary> resultList=new ArrayList<>();
        List<GpsSource> dataList=new ArrayList<>();
        StringBuilder sql=new StringBuilder("select lng,lat,time from t_gps where vin=? and time>=? and time <=?");
        if(order==1){
            sql.append(" order by time asc");
        }else if(order ==2){
            sql.append(" order by time desc");
        }else{
            throw BaseRuntimeException.getException("Param[order] Must Be 1(ASC) Or 2(DESC)");
        }
        sql.append(" limit ?,?");
        int count=0;
        while(true){
            //1、记录开始循环的索引
            int beginIndex=dataList.size()-1;
            beginIndex=beginIndex<0?0:beginIndex;
            //2、添加每次查询结果到总结果集中
            List<GpsSource> curDataList=dataAccess.list(sql.toString(),rs->{
                GpsSource gpsSource=new GpsSource();
                Double lng=rs.getDouble("lng");
                Double lat=rs.getDouble("lat");
                Date time=new Date(rs.getTimestamp("time").getTime());
                gpsSource.setLng(lng);
                gpsSource.setLat(lat);
                gpsSource.setTime(time);
                return gpsSource;
            },vin,startTime,endTime,count*EVERY_FETCH_DATA_NUM,EVERY_FETCH_DATA_NUM);
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
                int index1=0;
                int index2;
                int dataSize=dataList.size();
                for (index2 = beginIndex; index2 <= dataSize - 2; ) {
                    if(data1==null){
                        data1 = dataList.get(index2);
                    }
                    data2 = dataList.get(index2 + 1);
                    long diff;
                    if(order==1){
                        diff=data2.getTime().getTime() - data1.getTime().getTime();
                    }else {
                        diff = data1.getTime().getTime() - data2.getTime().getTime();
                    }
                    if (diff > gpsSplitTimeMills) {
                        //3.1、如果大于时间差,则添加到结果集中
                        GpsSource startData;
                        GpsSource endData;
                        if(order==1){
                            startData=dataList.get(index1);
                            endData=dataList.get(index2);
                        }else{
                            startData=dataList.get(index2);
                            endData=dataList.get(index1);
                        }
                        resultList.add(new GpsSplitSummary(startData.getTime(),endData.getTime(),index2-index1+1,new double[]{startData.getLng(),startData.getLat()},new double[]{endData.getLng(),endData.getLat()}));
                        index1=index2+1;
                        if (resultList.size() == num) {
                            //3.2、如果结果集的长度已经达到要求的长度,则直接返回
                            return resultList;
                        }
                    }else{
                        index2++;
                    }
                    data1=data2;
                }
                //4、如果循环完了,还是没有收集到传入参数的数量,检查是否还有数据,有则继续查,无则余下算做最后一段
                if (curDataList.size() < EVERY_FETCH_DATA_NUM) {
                    GpsSource startData;
                    GpsSource endData;
                    if(order==1){
                        startData=dataList.get(index1);
                        endData=dataList.get(index2);
                    }else{
                        startData=dataList.get(index2);
                        endData=dataList.get(index1);
                    }
                    resultList.add(new GpsSplitSummary(startData.getTime(),endData.getTime(),index2-index1+1,new double[]{startData.getLng(),startData.getLat()},new double[]{endData.getLng(),endData.getLat()}));
                    return resultList;
                }else{
                    //5、如果没有循环完,则保留有效数据
                    dataList = new ArrayList<>(dataList.subList(index1, index2));
                }

            }
        }
    }

}
