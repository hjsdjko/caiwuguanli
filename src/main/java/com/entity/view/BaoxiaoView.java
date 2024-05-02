package com.entity.view;

import com.entity.BaoxiaoEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 报销信息
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("baoxiao")
public class BaoxiaoView extends BaoxiaoEntity implements Serializable {
    private static final long serialVersionUID = 1L;
		/**
		* 报销类型的值
		*/
		private String baoxiaoValue;
		/**
		* 是否上交的值
		*/
		private String baoxiaoShangjiaoValue;
		/**
		* 是否批准的值
		*/
		private String baoxiaoYesnoValue;



		//级联表 yonghu
			/**
			* 姓名
			*/
			private String yonghuName;
			/**
			* 手机号
			*/
			private String yonghuPhone;
			/**
			* 身份证号
			*/
			private String yonghuIdNumber;
			/**
			* 照片
			*/
			private String yonghuPhoto;
			/**
			* 权限类型
			*/
			private Integer roleTypes;
				/**
				* 权限类型的值
				*/
				private String roleValue;

	public BaoxiaoView() {

	}

	public BaoxiaoView(BaoxiaoEntity baoxiaoEntity) {
		try {
			BeanUtils.copyProperties(this, baoxiaoEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



			/**
			* 获取： 报销类型的值
			*/
			public String getBaoxiaoValue() {
				return baoxiaoValue;
			}
			/**
			* 设置： 报销类型的值
			*/
			public void setBaoxiaoValue(String baoxiaoValue) {
				this.baoxiaoValue = baoxiaoValue;
			}
			/**
			* 获取： 是否上交的值
			*/
			public String getBaoxiaoShangjiaoValue() {
				return baoxiaoShangjiaoValue;
			}
			/**
			* 设置： 是否上交的值
			*/
			public void setBaoxiaoShangjiaoValue(String baoxiaoShangjiaoValue) {
				this.baoxiaoShangjiaoValue = baoxiaoShangjiaoValue;
			}
			/**
			* 获取： 是否批准的值
			*/
			public String getBaoxiaoYesnoValue() {
				return baoxiaoYesnoValue;
			}
			/**
			* 设置： 是否批准的值
			*/
			public void setBaoxiaoYesnoValue(String baoxiaoYesnoValue) {
				this.baoxiaoYesnoValue = baoxiaoYesnoValue;
			}
















				//级联表的get和set yonghu
					/**
					* 获取： 姓名
					*/
					public String getYonghuName() {
						return yonghuName;
					}
					/**
					* 设置： 姓名
					*/
					public void setYonghuName(String yonghuName) {
						this.yonghuName = yonghuName;
					}
					/**
					* 获取： 手机号
					*/
					public String getYonghuPhone() {
						return yonghuPhone;
					}
					/**
					* 设置： 手机号
					*/
					public void setYonghuPhone(String yonghuPhone) {
						this.yonghuPhone = yonghuPhone;
					}
					/**
					* 获取： 身份证号
					*/
					public String getYonghuIdNumber() {
						return yonghuIdNumber;
					}
					/**
					* 设置： 身份证号
					*/
					public void setYonghuIdNumber(String yonghuIdNumber) {
						this.yonghuIdNumber = yonghuIdNumber;
					}
					/**
					* 获取： 照片
					*/
					public String getYonghuPhoto() {
						return yonghuPhoto;
					}
					/**
					* 设置： 照片
					*/
					public void setYonghuPhoto(String yonghuPhoto) {
						this.yonghuPhoto = yonghuPhoto;
					}
					/**
					* 获取： 权限类型
					*/
					public Integer getRoleTypes() {
						return roleTypes;
					}
					/**
					* 设置： 权限类型
					*/
					public void setRoleTypes(Integer roleTypes) {
						this.roleTypes = roleTypes;
					}


						/**
						* 获取： 权限类型的值
						*/
						public String getRoleValue() {
							return roleValue;
						}
						/**
						* 设置： 权限类型的值
						*/
						public void setRoleValue(String roleValue) {
							this.roleValue = roleValue;
						}




}
