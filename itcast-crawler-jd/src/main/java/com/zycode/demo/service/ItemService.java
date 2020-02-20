package com.zycode.demo.service;

import com.zycode.demo.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ItemService {
    /**
     * 保存商品
     * @param item
     */
    public void save(Item item);

    /**
     * 根据条件查询商品
     * @param item
     * @return
     */
    public List<Item> findAll(Item item);
}
