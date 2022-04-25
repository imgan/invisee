/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.util;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Hatta Palino
 */
public class ResultBaseRepository implements Serializable {

    private Integer totalSize;
    private List list;

    public ResultBaseRepository(Integer totalSize, List list) {
        this.totalSize = totalSize;
        this.list = list;
    }
    
    public Integer getTotalSize() {
        if(totalSize != null && totalSize == 0) {
            totalSize = list.size();
        }
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
