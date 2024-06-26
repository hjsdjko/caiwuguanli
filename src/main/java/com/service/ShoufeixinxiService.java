package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ShoufeixinxiEntity;
import java.util.Map;

/**
 * 收费信息 服务类
 */
public interface ShoufeixinxiService extends IService<ShoufeixinxiEntity> {

    /**
    * @param params 查询参数
    * @return 带分页的查询出来的数据
    */
     PageUtils queryPage(Map<String, Object> params);
}