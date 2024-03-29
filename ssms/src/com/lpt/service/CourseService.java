package com.lpt.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lpt.bean.Clazz;
import com.lpt.bean.Course;
import com.lpt.bean.Grade;
import com.lpt.bean.Page;
import com.lpt.bean.Student;
import com.lpt.dao.impl.BaseDaoImpl;
import com.lpt.dao.impl.ClazzDaoImpl;
import com.lpt.dao.inter.BaseDaoInter;
import com.lpt.dao.inter.ClazzDaoInter;
import com.lpt.tools.MysqlTool;
import com.lpt.tools.StringTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 课程服务层
 * @author lpt
 *
 */
public class CourseService {
	
	BaseDaoInter dao = new BaseDaoImpl();
	
	/**
	 * 获取所有课程
	 * @return
	 */
	public String getCourseList(String gradeid){
		List<Object> list;
		if(StringTool.isEmpty(gradeid)){
			list = dao.getList(Course.class, "SELECT * FROM course");
		} else{
			list = dao.getList(Course.class, 
					"SELECT c.* FROM course c, grade_course gc WHERE c.id=gc.courseid AND gc.gradeid=?", 
					new Object[]{Integer.parseInt(gradeid)});
		}
		//json化
        String result = JSONArray.fromObject(list).toString();
        
        return result;
	}

	/**
	 * 添加课程
	 * @param course
	 */
	public void addCourse(Course course) {
		dao.insert("INSERT INTO course(name) value(?)", new Object[]{course.getName()});
	}

	/**
	 * 删除课程
	 * @param courseid
	 * @throws Exception 
	 */
	public void deleteClazz(int courseid) throws Exception {
		//获取连接
		Connection conn = MysqlTool.getConnection();
		try {
			//开启事务
			MysqlTool.startTransaction();
			//删除成绩表
			dao.deleteTransaction(conn, "DELETE FROM escore WHERE courseid=?", new Object[]{courseid});
			//删除班级的课程和老师的关联
			dao.deleteTransaction(conn, "DELETE FROM clazz_course_teacher WHERE courseid=?", new Object[]{courseid});
			//删除年级与课程关联
			dao.deleteTransaction(conn, "DELETE FROM grade_course WHERE courseid=?",  new Object[]{courseid});
			//最后删除课程
			dao.deleteTransaction(conn, "DELETE FROM course WHERE id=?",  new Object[]{courseid});
			
			//提交事务
			MysqlTool.commit();
		} catch (Exception e) {
			//回滚事务
			MysqlTool.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			MysqlTool.closeConnection();
		}
	}
	
	
	
	
}
