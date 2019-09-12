package com.lyh.gate.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lyh.gate.model.MpUser;
@Component("mpUserDao")
public class MpUserDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void save(MpUser mpUser){
		/* 插入实现 */
		jdbcTemplate.update("insert into mp_user(id,bizUid) values(?,?)",
				new Object[] { mpUser.getId(),mpUser.getBizUid()});
	}
	
	public void update(int id, String bizUid){
		jdbcTemplate.update("update mp_user set bizUid=? where id=?",
				new Object[] {bizUid, id});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MpUser getBeanByBizUid(String bizUid){
		List<MpUser> list = new ArrayList<MpUser>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from mp_user where bizUid=? ");
		list = (List<MpUser>) jdbcTemplate.query(sql.toString(), new Object[] { bizUid }, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MpUser qm = new MpUser();
				qm.setId(rs.getInt("id"));
				qm.setBizUid(rs.getString("bizUid"));
				return qm;
			}
		});
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MpUser getBeanById(Integer id){
		List<MpUser> list = new ArrayList<MpUser>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from mp_user where id=? ");
		list = (List<MpUser>) jdbcTemplate.query(sql.toString(), new Object[] { id }, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MpUser qm = new MpUser();
				qm.setId(rs.getInt("id"));
				qm.setBizUid(rs.getString("bizUid"));
				return qm;
			}
		});
		if(list!=null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
