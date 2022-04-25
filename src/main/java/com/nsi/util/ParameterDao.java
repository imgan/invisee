/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Hatta Palino
 */
public class ParameterDao {

    private final Class clazz;
    private final Map<String, Object> equals = new HashMap<>();
    private final Map<String, Object> likes = new HashMap<>();
    private final Map<String, Object> notLikes = new HashMap<>();
    private final Map<String, List<Object>> ins = new HashMap<>();
    private final Map<String, List<Object>> notIns = new HashMap<>();
    private final Map<String, Object> notEquals = new HashMap<>();
    private final Map<String, Object> greaterThans = new HashMap<>();
    private final Map<String, Object> equalGreaterThans = new HashMap<>();
    private final Map<String, Object> lessThans = new HashMap<>();
    private final Map<String, Object> equalLessThans = new HashMap<>();
    private final Map<String, Object[]> betweens = new HashMap<>();
    private final List<String[]> orders = new ArrayList<>();
    private Integer first = 0;
    private Integer maxResult = 0;

    public ParameterDao(Class clazz) {
        this.clazz = clazz;
    }

    public void addLikeOrEquals(String key, Object value) {
        if (key != null && !key.isEmpty()) {
            if (value.toString().startsWith("%") || value.toString().endsWith("%")) {
                this.likes.put(key, value);
            } else {
                this.equals.put(key, value);
            }
        }
    }

    public void addNotLikeOrNotEquals(String key, Object value) {
        if (key != null && !key.isEmpty()) {
            if (value.toString().startsWith("%") || value.toString().endsWith("%")) {
                this.notLikes.put(key, value);
            } else {
                this.notEquals.put(key, value);
            }
        }
    }

    public void setPagging(Integer page, Integer maxResult) {
        if (page > 0) {
            page = page - 1;
        }
        page = page * maxResult;
        if (page > maxResult) {
            page = maxResult;
        }

        this.maxResult = maxResult;
        this.first = page;
    }

    public void addIn(String key, List<Object> values) {
        this.ins.put(key, values);
    }

    public void addNotIn(String key, List<Object> values) {
        this.notIns.put(key, values);
    }

    public void addLessThan(String key, Object value) {
        this.lessThans.put(key, value);
    }

    public void addEqualLessThan(String key, Object value) {
        this.equalLessThans.put(key, value);
    }

    public void addGreaterThan(String key, Object value) {
        this.greaterThans.put(key, value);
    }

    public void addEqualGreaterThan(String key, Object value) {
        this.equalGreaterThans.put(key, value);
    }

    public void addBetween(String key, Object valFrom, Object valTo) {
        this.betweens.put(key, new Object[]{valFrom, valTo});
    }

    public void addOrderAsc(String key) {
        this.orders.add(new String[]{key, "0"});
    }

    public void addOrderDesc(String key) {
        this.orders.add(new String[]{key, "1"});
    }

    public Class getClazz() {
        return clazz;
    }

    public Map<String, Object> getEquals() {
        return equals;
    }

    public Map<String, Object> getLikes() {
        return likes;
    }

    public Map<String, Object> getNotLikes() {
        return notLikes;
    }

    public Map<String, List<Object>> getIns() {
        return ins;
    }

    public Map<String, List<Object>> getNotIns() {
        return notIns;
    }

    public Map<String, Object> getNotEquals() {
        return notEquals;
    }

    public Map<String, Object> getGreaterThans() {
        return greaterThans;
    }

    public Map<String, Object> getEqualGreaterThans() {
        return equalGreaterThans;
    }

    public Map<String, Object> getLessThans() {
        return lessThans;
    }

    public Map<String, Object> getEqualLessThans() {
        return equalLessThans;
    }

    public Map<String, Object[]> getBetweens() {
        return betweens;
    }

    public List<String[]> getOrders() {
        return orders;
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getMaxResult() {
        return maxResult;
    }

}
