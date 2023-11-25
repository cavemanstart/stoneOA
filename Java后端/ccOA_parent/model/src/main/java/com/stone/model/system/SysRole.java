package com.stone.model.system;

import com.stone.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@TableName("sys_role")
public class SysRole extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//角色名称
	@TableField("role_name")
	private String roleName;

	//角色编码
	@TableField("role_code")
	private String roleCode;

	//描述
	@TableField("description")
	private String description;
}

