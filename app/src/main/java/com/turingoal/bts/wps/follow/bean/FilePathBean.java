package com.turingoal.bts.wps.follow.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import lombok.Data;

/**
 * 文件类，专门用于数据库
 */
@Data
@Entity
public class FilePathBean {
    @Id
    public long oid;
    public String path; // 手机的路径

    public FilePathBean() {

    }

    public FilePathBean(String path) {
        this.path = path;
    }
}
