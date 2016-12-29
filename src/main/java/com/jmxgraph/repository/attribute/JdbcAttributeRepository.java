package com.jmxgraph.repository.attribute;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxAttributeProperties;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.ui.GraphFilter;


public class JdbcAttributeRepository implements JmxAttributeRepository {
	
	private JdbcTemplate jdbcTemplate;
	private JmxAttributeResultSetExtractor jmxAttributeResultSetExtractor;
	private JmxObjectNameResultSetExtractor jmxObjectNameResultSetExtractor;
	
	private JdbcAttributeRepository() {  }
	
	private static class InstanceHolder {
		private static final JdbcAttributeRepository instance = new JdbcAttributeRepository();
	}
	
	public static JdbcAttributeRepository getInstance() {
		return InstanceHolder.instance;
	}
	
	@Override
	public void initialize(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jmxAttributeResultSetExtractor = new JmxAttributeResultSetExtractor();
		jmxObjectNameResultSetExtractor = new JmxObjectNameResultSetExtractor();
	}
	
	@Override
	public boolean isInitialized() {
		return jdbcTemplate != null;
	}
	
	@Override
	public void insertJmxObjectName(final JmxObjectName jmxObjectName) {
		final String insertSql = "INSERT INTO jmx_object_name (canonical_object_name, description) VALUES (?, ?)";
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, jmxObjectName.getCanonicalName());
					preparedStatement.setString(2, jmxObjectName.getDescription());
					
					return preparedStatement;
				}
		}, holder);
		
		for (JmxAttribute jmxAttribute : jmxObjectName.getAttributes()) {
			int attributeId = insertJmxAttribute(holder.getKey().intValue(), jmxAttribute);
			insertJmxAttributeProperties(attributeId, jmxAttribute.getAttributeProperties());
		}
	}
	
	@Override
	public int insertJmxAttribute(final int objectNameId, final JmxAttribute jmxAttribute) {
		final String insertSql = "INSERT INTO jmx_attribute (object_name_id, attribute_name, attribute_type, path) VALUES (?, ?, ?, ?)";
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setInt(1, objectNameId);
				preparedStatement.setString(2, jmxAttribute.getAttributeName());
				preparedStatement.setString(3, jmxAttribute.getAttributeType());
				preparedStatement.setString(4, jmxAttribute.getPath());
				
				return preparedStatement;
			}
		}, holder);
		
		return holder.getKey().intValue();
	}

	@Override
	public void insertJmxAttributeValue(final JmxAttributeValue jmxAttributeValue) {
		jdbcTemplate.update("INSERT INTO jmx_attribute_value (attribute_id, attribute_value, poll_timestamp) VALUES (?, ?, ?)", 
				new Object[] { 
						jmxAttributeValue.getAttributeId(),
						String.valueOf(jmxAttributeValue.getAttributeValue()),
						jmxAttributeValue.getTimestamp()
				});
	}
	
	private void insertJmxAttributeProperties(final int attributeId, final JmxAttributeProperties jmxAttributeProperties) {
		final String insertSql = "INSERT INTO jmx_attribute_property (attribute_id, property_name, property_value) VALUES (?, ?, ?)";
		for (Map.Entry<String, String> entrySet : jmxAttributeProperties.entrySet()) {
			jdbcTemplate.update(insertSql, new Object[] { attributeId, entrySet.getKey(), entrySet.getValue() });
		}
	}
	
	@Override
	public void batchInsertAttributeValues(final List<JmxAttributeValue> jmxAttributeValues) {
		String sql = "INSERT INTO jmx_attribute_value (attribute_id, attribute_value, poll_timestamp) VALUES (?, ?, ?)";
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JmxAttributeValue jmxAttributeValue = jmxAttributeValues.get(i);
				
				ps.setInt(1, jmxAttributeValue.getAttributeId());
				ps.setString(2, String.valueOf(jmxAttributeValue.getAttributeValue()));
				ps.setDate(3, new Date(jmxAttributeValue.getTimestamp().getTime()));
			}

			@Override
			public int getBatchSize() {
				return jmxAttributeValues.size();
			}
		});
	}
	
	public class JmxAttributeResultSetExtractor implements ResultSetExtractor<Collection<JmxAttribute>> {
		public Collection<JmxAttribute> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Integer, JmxAttribute> attributeMap = new HashMap<>();
			
			while (rs.next()) {
				int attributeId = rs.getInt("attribute_id");
				
				JmxAttribute jmxAttribute = attributeMap.get(attributeId);
				if (jmxAttribute == null) {
					jmxAttribute = new JmxAttribute(attributeId, rs.getInt("object_name_id"), rs.getString("attribute_name"), rs.getString("attribute_type"), rs.getString("path"), rs.getInt("enabled") == 1);
					attributeMap.put(attributeId, jmxAttribute);
				}
				jmxAttribute.getAttributeValues().add(new JmxAttributeValue(rs.getInt("value_id"), attributeId, rs.getObject("attribute_value"), rs.getTimestamp("poll_timestamp")));
			}
			
			return attributeMap.values();
		}
	}
	
	public class JmxObjectNameResultSetExtractor implements ResultSetExtractor<Collection<JmxObjectName>> {
		public Collection<JmxObjectName> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Integer, JmxObjectName> objectNameMap = new HashMap<>();
			
			while (rs.next()) {
				int objectNameId = rs.getInt("object_name_id");
				
				JmxObjectName jmxObjectName = objectNameMap.get(objectNameId);
				if (jmxObjectName == null) {
					jmxObjectName = new JmxObjectName(objectNameId, rs.getString("canonical_object_name"), rs.getString("description"));
					objectNameMap.put(objectNameId, jmxObjectName);
				}
				jmxObjectName.addAttribute(new JmxAttribute(rs.getInt("attribute_id"), objectNameId, rs.getString("attribute_name"), rs.getString("attribute_type"), rs.getString("path"), rs.getInt("enabled") == 1));
			}
			
			return objectNameMap.values();
		}
	}
	
	public class JmxAttributePropertyResultSetExtractor implements ResultSetExtractor<JmxAttributeProperties> {
		public JmxAttributeProperties extractData(ResultSet rs) throws SQLException, DataAccessException {
			JmxAttributeProperties jmxAttributeProperties = new JmxAttributeProperties();
			
			while (rs.next()) {
				jmxAttributeProperties.put(rs.getString("property_name"), rs.getString("property_value"));
			}
			
			return jmxAttributeProperties;
		}
	}
	
	public JmxAttribute getJmxAttributeValuesByAttributeId(final int attributeId, GraphFilter filter) {
		// TODO: Get attribute values and properties in a single query
		String selectQuery =
				"SELECT ja.attribute_id, ja.object_name_id, ja.attribute_name, ja.attribute_type, ja.path, ja.enabled, jav.value_id, jav.attribute_value, jav.poll_timestamp " +
				"FROM jmx_attribute ja LEFT JOIN jmx_attribute_value jav ON ja.attribute_id = jav.attribute_id " +
				"WHERE attribute_id = ? AND poll_timestamp > ?";
		
		JmxAttribute jmxAttribute = jdbcTemplate.query(selectQuery, new Object[] { attributeId, filter.getSqlDateClause() }, jmxAttributeResultSetExtractor).iterator().next();
		jmxAttribute.getAttributeProperties().putAll(jdbcTemplate.query("SELECT property_name, property_value FROM jmx_attribute_property WHERE attribute_id = ?", new Object[] { attributeId }, new JmxAttributePropertyResultSetExtractor()));
		
		return jmxAttribute;
	}
	
	@Override
	public Collection<JmxObjectName> getAllEnabledAttributePaths() {
		String selectQuery =
				"SELECT jon.object_name_id, jon.canonical_object_name, jon.description, ja.attribute_id, ja.attribute_name, ja.attribute_type, ja.path, ja.enabled " +
				"FROM jmx_object_name jon JOIN jmx_attribute ja ON jon.object_name_id = ja.object_name_id " +
				"WHERE ja.enabled = 1";
		
		return jdbcTemplate.query(selectQuery, jmxObjectNameResultSetExtractor);
	}
	
	@Override
	public JmxObjectName getJmxObjectName(final String objectName) {
		String selectQuery = "SELECT object_name_id, canonical_object_name, description FROM jmx_object_name WHERE canonical_object_name = ?";
		
		return jdbcTemplate.queryForObject(selectQuery, new Object[] { objectName }, new RowMapper<JmxObjectName>() {
			public JmxObjectName mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new JmxObjectName(rs.getInt("object_name_id"), rs.getString("canonical_object_name"), rs.getString("description"));
			}
		});
	}
	
	@Override
	public JmxObjectName getJmxObjectNameWithAttributes(final String objectName) {
		String selectQuery =
				"SELECT jon.object_name_id, jon.canonical_object_name, jon.description, ja.attribute_id, ja.attribute_name, ja.attribute_type, ja.path, ja.enabled " +
				"FROM jmx_object_name jon JOIN jmx_attribute ja ON jon.object_name_id = ja.object_name_id " +
				"WHERE jon.canonical_object_name = ?";
		
		Collection<JmxObjectName> objectNameCollection = jdbcTemplate.query(selectQuery, new Object[] { objectName }, jmxObjectNameResultSetExtractor);
		return objectNameCollection.isEmpty() ? null : objectNameCollection.iterator().next();
	}
	
	@Override
	public JmxAttribute getJmxAttribute(final int objectNameId, final String attributeName) {
		String selectQuery = "SELECT attribute_id, object_name_id, attribute_name, attribute_type, path, enabled FROM jmx_attribute WHERE object_name_id = ? AND attribute_name = ?";
		
		return jdbcTemplate.queryForObject(selectQuery, new Object[] { objectNameId, attributeName }, new RowMapper<JmxAttribute>() {
			public JmxAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new JmxAttribute(rs.getInt("attribute_id"), objectNameId, attributeName, rs.getString("attribute_type"), rs.getString("path"), rs.getInt("enabled") == 1);
			}
		});
	}
	
	@Override
	public void enableJmxAttributePath(final int attributeId) {
		jdbcTemplate.update("UPDATE jmx_attribute SET enabled = 1 WHERE attribute_id = ?", new Object[] { attributeId });
	}
	
	@Override
	public void disableJmxAttributePath(final int attributeId) {
		jdbcTemplate.update("UPDATE jmx_attribute SET enabled = 0 WHERE attribute_id = ?", new Object[] { attributeId });
	}
}
