CREATE TABLE jmx_attribute_path (
	path_id			IDENTITY,
	object_name 	VARCHAR(255),
	attribute		VARCHAR(100),
	attribute_type	VARCHAR(50),
	path			VARCHAR(100),
	enabled			INTEGER DEFAULT 1
);

CREATE TABLE jmx_attribute_value (
	value_id		IDENTITY,
	path_id			INTEGER,
	attribute_value VARCHAR(100),
	poll_timestamp	TIMESTAMP,
	FOREIGN KEY(path_id) REFERENCES jmx_attribute_path(path_id)
);