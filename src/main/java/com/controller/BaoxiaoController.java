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

import com.entity.BaoxiaoEntity;

import com.entity.view.BaoxiaoView;
import com.entity.YonghuEntity;

import com.utils.PageUtils;
import com.utils.R;

/**
 * 报销信息
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/baoxiao")
public class BaoxiaoController {
    private static final Logger logger = LoggerFactory.getLogger(BaoxiaoController.class);

    @Autowired
    private BaoxiaoService baoxiaoService;


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
        }
        params.put("orderBy","id");
        PageUtils page = baoxiaoService.queryPage(params);

        //字典表数据转换
        List<BaoxiaoView> list =(List<BaoxiaoView>)page.getList();
        for(BaoxiaoView c:list){
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
        BaoxiaoEntity baoxiao = baoxiaoService.selectById(id);
        if(baoxiao !=null){
            //entity转view
            BaoxiaoView view = new BaoxiaoView();
            BeanUtils.copyProperties( baoxiao , view );//把实体数据重构到view中

            //级联表
            YonghuEntity yonghu = yonghuService.selectById(baoxiao.getYonghuId());
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
    public R save(@RequestBody BaoxiaoEntity baoxiao, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,baoxiao:{}",this.getClass().getName(),baoxiao.toString());
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if("普通员工".equals(role)){
            baoxiao.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        }else{
            return R.error(511,"您没有权限新增报销");
        }
        Wrapper<BaoxiaoEntity> queryWrapper = new EntityWrapper<BaoxiaoEntity>()
            .eq("yonghu_id", baoxiao.getYonghuId())
            .eq("baoxiao_name", baoxiao.getBaoxiaoName())
            .eq("baoxiao_types", baoxiao.getBaoxiaoTypes())
//            .eq("baoxiao_shangjiao_types", baoxiao.getBaoxiaoShangjiaoTypes())
//            .eq("baoxiao_yesno_types", baoxiao.getBaoxiaoYesnoTypes())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        BaoxiaoEntity baoxiaoEntity = baoxiaoService.selectOne(queryWrapper);
        if(baoxiaoEntity==null){
            baoxiao.setInsertTime(new Date());
            baoxiao.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      baoxiao.set
        //  }
            baoxiao.setBaoxiaoShangjiaoTypes(1);
            baoxiaoService.insert(baoxiao);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody BaoxiaoEntity baoxiao, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,baoxiao:{}",this.getClass().getName(),baoxiao.toString());
        //根据字段查询是否有相同数据
        Wrapper<BaoxiaoEntity> queryWrapper = new EntityWrapper<BaoxiaoEntity>()
            .notIn("id",baoxiao.getId())
            .andNew()
            .eq("yonghu_id", baoxiao.getYonghuId())
            .eq("baoxiao_name", baoxiao.getBaoxiaoName())
            .eq("baoxiao_types", baoxiao.getBaoxiaoTypes())
//            .eq("baoxiao_shangjiao_types", baoxiao.getBaoxiaoShangjiaoTypes())
//            .eq("baoxiao_yesno_types", baoxiao.getBaoxiaoYesnoTypes())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        BaoxiaoEntity baoxiaoEntity = baoxiaoService.selectOne(queryWrapper);
        if(baoxiaoEntity==null){
            String role = String.valueOf(request.getSession().getAttribute("role"));
            if("财务经理".equals(role)){
                BaoxiaoEntity oldBaoxiao = baoxiaoService.selectById(baoxiao.getId());// 老的报销
                if(oldBaoxiao.getBaoxiaoShangjiaoTypes() != 2 && baoxiao.getBaoxiaoShangjiaoTypes() ==2){// 之前财务人员不是审核通过,这次财务人员审核通过
                    baoxiao.setBaoxiaoYesnoTypes(1);
                }
            }else if("管理员".equals(role)){
                BaoxiaoEntity oldBaoxiao = baoxiaoService.selectById(baoxiao.getId());// 老的报销
                if(oldBaoxiao.getBaoxiaoYesnoTypes() != 2 && baoxiao.getBaoxiaoYesnoTypes() ==2){// 之前管理员不是审核通过,这次管理员审核通过
                    YonghuEntity yonghuEntity = yonghuService.selectById(baoxiao.getYonghuId());
                    // 添加支出信息
                    ZhichuxinxiEntity zhichuxinxiEntity = new ZhichuxinxiEntity();
                    Date date = new Date();
                    zhichuxinxiEntity.setCreateTime(date);
                    zhichuxinxiEntity.setInsertTime(date);
                    zhichuxinxiEntity.setZhichuxinxiMingmuName(yonghuEntity.getYonghuName()+"的报销");
                    zhichuxinxiEntity.setZhichuxinxiTypes(3);
                    zhichuxinxiEntity.setZhichuContent(yonghuEntity.getYonghuName()+"的报销,报销金额是"+baoxiao.getBaoxiaoMoney());
                    zhichuxinxiEntity.setZhichuxinxiMoney(baoxiao.getBaoxiaoMoney());
                    zhichuxinxiService.insert(zhichuxinxiEntity);

                }
            }
            baoxiaoService.updateById(baoxiao);//根据id更新
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
        baoxiaoService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }






}

