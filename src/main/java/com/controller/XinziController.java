package com.controller;


import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;

import com.entity.ZhichuxinxiEntity;
import com.service.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.XinziEntity;

import com.entity.view.XinziView;
import com.entity.YonghuEntity;

import com.utils.PageUtils;
import com.utils.R;

/**
 * 薪资
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/xinzi")
public class XinziController {
    private static final Logger logger = LoggerFactory.getLogger(XinziController.class);

    @Autowired
    private XinziService xinziService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;



    //级联表service
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private ZhichuxinxiService zhichuxinxiService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
     
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isNotEmpty(role) && "普通员工".equals(role)){
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        }else if(StringUtil.isNotEmpty(role) && "财务经理".equals(role)){
            params.put("roleTypes",1);
        }
        params.put("orderBy","id");
        PageUtils page = xinziService.queryPage(params);

        //字典表数据转换
        List<XinziView> list =(List<XinziView>)page.getList();
        for(XinziView c:list){
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
        XinziEntity xinzi = xinziService.selectById(id);
        if(xinzi !=null){
            //entity转view
            XinziView view = new XinziView();
            BeanUtils.copyProperties( xinzi , view );//把实体数据重构到view中

            //级联表
            YonghuEntity yonghu = yonghuService.selectById(xinzi.getYonghuId());
            if(yonghu != null){
                BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                view.setYonghuId(yonghu.getId());
            }
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
    public R save(@RequestBody XinziEntity xinzi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,xinzi:{}",this.getClass().getName(),xinzi.toString());
        Wrapper<XinziEntity> queryWrapper = new EntityWrapper<XinziEntity>()
            .eq("yonghu_id", xinzi.getYonghuId())
            .eq("month", xinzi.getMonth())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        XinziEntity xinziEntity = xinziService.selectOne(queryWrapper);
        if(xinziEntity==null){
            Date date = new Date();
            xinzi.setCreateTime(date);
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      xinzi.set
        //  }
            xinziService.insert(xinzi);
            YonghuEntity yonghuEntity = yonghuService.selectById(xinzi.getYonghuId());
            ZhichuxinxiEntity zhichuxinxiEntity = new ZhichuxinxiEntity();
            zhichuxinxiEntity.setInsertTime(date);
            zhichuxinxiEntity.setCreateTime(date);
            zhichuxinxiEntity.setZhichuxinxiMoney(xinzi.getMoney());
            zhichuxinxiEntity.setZhichuxinxiTypes(1);
            zhichuxinxiEntity.setZhichuxinxiMingmuName(yonghuEntity.getYonghuName()+"的"+xinzi.getMonth()+"的工资"+xinzi.getMoney()+"元");
            zhichuxinxiEntity.setZhichuContent(yonghuEntity.getYonghuName()+"的"+xinzi.getMonth()+"的工资"+xinzi.getMoney()+"元");
            zhichuxinxiService.insert(zhichuxinxiEntity);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody XinziEntity xinzi, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,xinzi:{}",this.getClass().getName(),xinzi.toString());
        //根据字段查询是否有相同数据
        Wrapper<XinziEntity> queryWrapper = new EntityWrapper<XinziEntity>()
            .notIn("id",xinzi.getId())
            .andNew()
            .eq("yonghu_id", xinzi.getYonghuId())
            .eq("month", xinzi.getMonth())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        XinziEntity xinziEntity = xinziService.selectOne(queryWrapper);
        if(xinziEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      xinzi.set
            //  }
            xinziService.updateById(xinzi);//根据id更新
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
        xinziService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }






}

