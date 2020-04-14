package com.yiwu.kotlinx.entity;

import java.io.Serializable;

/**
 * @Author:yiwu
 * @Date: Created in 12:06 2020/3/27
 * @Description:
 */
public class User implements Serializable {

    public User(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    private static final long serialVersionUID = -2749177986271026866L;
    private String name;
    private String tel;

}
