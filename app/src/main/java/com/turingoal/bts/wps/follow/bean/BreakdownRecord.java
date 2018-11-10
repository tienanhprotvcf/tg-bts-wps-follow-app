package com.turingoal.bts.wps.follow.bean;

import java.util.Date;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import lombok.Data;

/**
 * 故障记录
 */
@Data
@Entity
public class BreakdownRecord {
    @Id
    public long oid; // 数据库用，从1开始自增
    public String id; // 后台id
    public String trainSetTrips; // 车次
    public String trainSetCodeNum; // 车组
    public String source; // 故障来源,一直是乘务日志
    public Date createTime;  // 创建时间
    public String createTimeStr; // 创建时间字符串,方便查询,yyyyMMdd
    public Date recordTime; // 记录时间
    public String userId; // 用户id
    public String username; // 用户名
    public String userCodeNum; // 用户工号
    public String userRealname; // 用户姓名
    @Transient
    public List<BreakdownRecordItem> breakdownRecordItemList; // 打包的时候用
    public ToMany<BreakdownRecordItem> breakdownRecordItems; // 本地数据库存储用
    public String sync; // 是否上传过

    public BreakdownRecord() {

    }
}