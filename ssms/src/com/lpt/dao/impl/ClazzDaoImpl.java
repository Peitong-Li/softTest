package com.lpt.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import com.lpt.bean.Clazz;
import com.lpt.bean.Grade;
import com.lpt.bean.Page;
import com.lpt.bean.Student;
import com.lpt.dao.inter.BaseDaoInter;
import com.lpt.dao.inter.ClazzDaoInter;
import com.lpt.dao.inter.StudentDaoInter;
import com.lpt.tools.MysqlTool;
import com.lpt.tools.StringTool;

public class ClazzDaoImpl extends BaseDaoImpl implements ClazzDaoInter {

	public List<Clazz> getClazzDetailList(String gradeid, Page page) {
		//数据集合
		List<Clazz> list = new LinkedList<>();
		try {
			StringBuffer sb = new StringBuffer("SELECT c.id cid, c.name cname, g.id gid, g.name gname FROM clazz c, grade g WHERE c.gradeid = g.id");
			List<Object> param = new LinkedList<>();
			if(!StringTool.isEmpty(gradeid)){
				sb.append(" AND c.gradeid=? ");
				param.add(Integer.parseInt(gradeid));
			}
			sb.append(" LIMIT ?,?");
			param.add(page.getStart());
			param.add(page.getSize());
			
			String sql = sb.toString();
			//获取数据库连接
			Connection conn = MysqlTool.getConnection();
			//预编译
			PreparedStatement ps = conn.prepareStatement(sql);
			//设置参数
			if(param != null && param.size() > 0){
				for(int i = 0;i < param.size();i++){
					ps.setObject(i+1, param.get(i));
				}
			}
			//执行sql语句
			ResultSet rs = ps.executeQuery();
			//遍历结果集
			while(rs.next()){
				//创建对象
				Clazz clazz = new Clazz();
				Grade grade = new Grade();
				//封装参数
				grade.setId(rs.getInt("gid"));
				grade.setName(rs.getString("gname"));
				clazz.setId(rs.getInt("cid"));
				clazz.setName(rs.getString("cname"));
				clazz.setGradeid(rs.getInt("gid"));
				//添加
				clazz.setGrade(grade);
				//添加到集合
				list.add(clazz);
			}
			//关闭连接
			MysqlTool.closeConnection();
			MysqlTool.close(ps);
			MysqlTool.close(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

}
