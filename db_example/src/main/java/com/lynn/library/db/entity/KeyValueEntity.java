package com.lynn.library.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Lynn.
 */
@Entity
public class KeyValueEntity {
    @Id
    public long id;
    public String key;
    public String value;
    @Generated(hash = 1685566852)
    public KeyValueEntity(long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }
    @Generated(hash = 1674801752)
    public KeyValueEntity() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
