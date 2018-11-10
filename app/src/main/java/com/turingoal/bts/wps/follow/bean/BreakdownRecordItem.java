package com.turingoal.bts.wps.follow.bean;

import java.util.Date;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import lombok.Data;

/**
 * 故障记录条目
 */
@Data
@Entity
public class BreakdownRecordItem {
    @Id
    public long oid; // 数据库用，从1开始自增
    public String id; // 后台id 后台
    public String trainSetTrips; // 车次
    public String trainSetCodeNum; // 车组
    public String source; // 故障来源,一直是乘务日志
    public String carriage; // 车厢
    public String systemType; // 故障类型
    public String pattern; // 故障模式，一直是其他
    public String breakdownCode; // 故障代码
    public String discoveryDesc; // 故障描述，140字
    public Date createTime;  // 创建时间
    public Date recordTime; // 记录时间
    public Date discoveryTime; // 发现时间
    public String indexNum; // 序号，这是第几个故障
    @Transient
    private List<String> photoPaths; // zip目录下的图片 // 打包的时候用
    public ToMany<FilePathBean> filePathBeans; // 本地数据库存储用
    public String userId; // 用户id
    public String username; // 用户名
    public String userCodeNum; // 用户工号
    public String userRealname; // 用户姓名
    public long breakdownRecordOid; // 故障记录id 本地数据库 很重要
    public String breakdownRecordId; // 故障记录id 后台 很重要
    public String sync; // 是否上传过

    public BreakdownRecordItem() {

    }
}