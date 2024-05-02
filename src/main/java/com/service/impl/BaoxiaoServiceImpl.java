package com.service.impl;

import com.utils.StringUtil;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.*;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.dao.BaoxiaoDao;
import com.entity.BaoxiaoEntity;
import com.service.BaoxiaoService;
import com.entity.view.BaoxiaoView;

/**
 * 报销信息 服务实现类
 */
@Service("baoxiaoService")
@Transactional
public class BaoxiaoServiceImpl extends ServiceImpl<BaoxiaoDao, BaoxiaoEntity> implements BaoxiaoService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        if(params != null && (params.get("limit") == null || params.get("page") == null)){
            params.put("page","1");
            params.put("limit","10");
        }
        Page<BaoxiaoView> page =new Query<BaoxiaoView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }


}
