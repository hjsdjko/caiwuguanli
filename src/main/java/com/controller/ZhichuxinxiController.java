package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.ZhichuxinxiEntity;

import com.service.ZhichuxinxiService;
import com.entity.view.ZhichuxinxiView;

import com.utils.PageUtils;
import com.utils.R;

/**
 * 支出信息
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/zhichuxinxi")
public class ZhichuxinxiController {
    private static final Logger logger = LoggerFactory.getLogger(ZhichuxinxiController.class);

    @Autowired
    private ZhichuxinxiService zhichuxinxiService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;



    //级联表service


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
     
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "普通员工".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }
        params.put("orderBy","id");
        PageUtils page = zhichuxinxiService.queryPage(params);

        //字典表数据转换
        List<ZhichuxinxiView> list =(List<ZhichuxinxiView>)page.getList();
        for(ZhichuxinxiView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ZhichuxinxiEntity zhichuxinxi = zhichuxinxiService.selectById(id);
        if(zhichuxinxi !=null){
            //entity转view
            ZhichuxinxiView view = new ZhichuxinxiView();
            BeanUtils.copyProperties( zhichuxinxi , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody ZhichuxinxiEntity zhichuxinxi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,zhichuxinxi:{}",this.getClass().getName(),zhichuxinxi.toString());
        Wrapper<ZhichuxinxiEntity> queryWrapper = new EntityWrapper<ZhichuxinxiEntity>()
            .eq("zhichuxinxi_mingmu_name", zhichuxinxi.getZhichuxinxiMingmuName())
            .eq("zhichuxinxi_types", zhichuxinxi.getZhichuxinxiTypes())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZhichuxinxiEntity zhichuxinxiEntity = zhichuxinxiService.selectOne(queryWrapper);
        if(zhichuxinxiEntity==null){
            zhichuxinxi.setInsertTime(new Date());
            zhichuxinxi.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      zhichuxinxi.set
        //  }
            zhichuxinxiService.insert(zhichuxinxi);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody ZhichuxinxiEntity zhichuxinxi, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,zhichuxinxi:{}",this.getClass().getName(),zhichuxinxi.toString());
        //根据字段查询是否有相同数据
        Wrapper<ZhichuxinxiEntity> queryWrapper = new EntityWrapper<ZhichuxinxiEntity>()
            .notIn("id",zhichuxinxi.getId())
            .andNew()
            .eq("zhichuxinxi_mingmu_name", zhichuxinxi.getZhichuxinxiMingmuName())
            .eq("zhichuxinxi_types", zhichuxinxi.getZhichuxinxiTypes())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZhichuxinxiEntity zhichuxinxiEntity = zhichuxinxiService.selectOne(queryWrapper);
        if(zhichuxinxiEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      zhichuxinxi.set
            //  }
            zhichuxinxiService.updateById(zhichuxinxi);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        zhichuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }






}

