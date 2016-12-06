package com.jmxgraph.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.jmxgraph.domain.JmxAttributePath;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.ui.GraphFilter;


public class JdbcAttributeRepository implements JmxAttributeRepository {
	
	private JdbcTemplate jdbcTemplate = null;
	private JmxAttributeResultSetExtractor jmxAttributeResultSetExtractor;
	private JmxAttributePathRowMapper jmxAttributePathRowMapper;
	
	private static JdbcAttributeRepository instance = null;
	
	public static JdbcAttributeRepository getInstance() {
		synchronized(instance) {
			if (instance == null) {
				instance = new JdbcAttributeRepository();
			}
			return instance;
		}
	}
	
	@Override
	public boolean isInitialized() {
		return jdbcTemplate != null;
	}
	
	@Override
	public void initialize(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jmxAttributeResultSetExtractor = new JmxAttributeResultSetExtractor();
		jmxAttributePathRowMapper = new JmxAttributePathRowMapper();
	}
	
	@Override
	public void insertJmxAttributePath(JmxAttributePath jmxAttributePath) {
		jdbcTemplate.update("INSERT INTO jmx_attribute_path (object_name, attribute, attribute_type, path) VALUES (?, ?, ?, ?)",
				new Object[] {
						jmxAttributePath.getObjectName(),
						jmxAttributePath.getAttribute(),
						jmxAttributePath.getAttributeType(),
						jmxAttributePath.getPath()
				});
	}

	@Override
	public void insertJmxAttributeValue(JmxAttributeValue jmxAttributeValue) {
		jdbcTemplate.update("INSERT INTO jmx_attribute_value (path_id, attribute_value, poll_timestamp) VALUES (?, ?, ?)", 
				new Object[] { 
						jmxAttributeValue.getPathId(),
						String.valueOf(jmxAttributeValue.getAttributeValue()),
						jmxAttributeValue.getTimestamp()
				});
	}

	@Override
	public Collection<JmxAttributePath> getAllJmxAttributeValues() {
		String sql =
				"SELECT jap.path_id, jap.object_name, jap.attribute, jap.attribute_type, jap.path, jap.enabled, jav.value_id, jav.attribute_value, jav.poll_timestamp " +
				"FROM jmx_attribute_path jap " +
				"JOIN jmx_attribute_value jav ON jap.path_id = jav.path_id " +
				"ORDER BY path_id ASC, poll_timestamp ASC";
		
		return jdbcTemplate.query(sql, jmxAttributeResultSetExtractor);
	}
	
	public class JmxAttributeResultSetExtractor implements ResultSetExtractor<Collection<JmxAttributePath>> {
		public Collection<JmxAttributePath> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Integer, JmxAttributePath> attributePaths = new HashMap<>();
			
			while (rs.next()) {
				int pathId = rs.getInt("path_id");
				
				JmxAttributePath jmxAttributePath = attributePaths.get(pathId);
				if (jmxAttributePath == null) {
					jmxAttributePath = new JmxAttributePath(pathId, rs.getString("object_name"), rs.getString("attribute"), rs.getString("attribute_type"), rs.getString("path"), rs.getInt("enabled") == 1);
					attributePaths.put(pathId, jmxAttributePath);
				}
				jmxAttributePath.getAttributeValues().add(new JmxAttributeValue(rs.getInt("value_id"), pathId, rs.getObject("attribute_value"), rs.getTimestamp("poll_timestamp")));
			}
			
			return attributePaths.values();
		}
	}
	
	public class JmxAttributePathRowMapper implements RowMapper<JmxAttributePath> {
		public JmxAttributePath mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new JmxAttributePath(rs.getInt("path_id"),  rs.getString("object_name"), rs.getString("attribute"), rs.getString("attribute_type"), rs.getString("path"), rs.getInt("enabled") == 1);
		}
	}
	
	public JmxAttributePath getJmxAttributeValuesByPathId(final int pathId, GraphFilter filter) {
		String sql =
				"SELECT jap.path_id, jap.object_name, jap.attribute, jap.attribute_type, jap.path, jap.enabled, jav.value_id, jav.attribute_value, jav.poll_timestamp " +
				"FROM jmx_attribute_path jap " +
				"JOIN jmx_attribute_value jav ON jap.path_id = jav.path_id " +
				"WHERE path_id = ? " +
				"ORDER BY poll_timestamp DESC LIMIT " + filter.getSqlLimit();
		
		return jdbcTemplate.query(sql, new Object[] { pathId }, jmxAttributeResultSetExtractor).iterator().next();
	}
	
	@Override
	public Collection<JmxAttributePath> getAllEnabledAttributePaths() {
		return jdbcTemplate.query("SELECT * FROM jmx_attribute_path WHERE enabled = 1", jmxAttributePathRowMapper);
	}
	
	@Override
	public JmxAttributePath getJmxAttributePath(String objectName, String attribute) {
		return jdbcTemplate.queryForObject("SELECT * FROM jmx_attribute_path WHERE object_name = ? AND attribute = ?", new Object[] { objectName, attribute }, jmxAttributePathRowMapper);
	}
	
	@Override
	public void enableJmxAttributePath(final int pathId) {
		jdbcTemplate.update("UPDATE jmx_attribute_path SET enabled = 1 WHERE path_id = ?", new Object[] { pathId });
	}
	
	@Override
	public void disableJmxAttributePath(final int pathId) {
		jdbcTemplate.update("UPDATE jmx_attribute_path SET enabled = 0 WHERE path_id = ?", new Object[] { pathId });
	}
}
