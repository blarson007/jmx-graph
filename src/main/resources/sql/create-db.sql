CREATE TABLE jmx_object_name (
	object_name_id			IDENTITY,
	canonical_object_name	VARCHAR(255),
	description				VARCHAR(255),
	UNIQUE(canonical_object_name)
);

CREATE TABLE jmx_attribute (
	attribute_id	IDENTITY,
	object_name_id	INTEGER,
	attribute_name	VARCHAR(100),
	attribute_type	VARCHAR(50),
	path			VARCHAR(50),
	enabled			INTEGER DEFAULT 1,
	FOREIGN KEY(object_name_id) REFERENCES jmx_object_name(object_name_id)
);

CREATE TABLE jmx_attribute_value (
	value_id		IDENTITY,
	attribute_id	INTEGER,
	attribute_value VARCHAR(100),
	poll_timestamp	TIMESTAMP,
	FOREIGN KEY(attribute_id) REFERENCES jmx_attribute(attribute_id)
);