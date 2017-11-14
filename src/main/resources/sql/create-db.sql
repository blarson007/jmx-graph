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
	FOREIGN KEY(object_name_id) REFERENCES jmx_object_name(object_name_id),
	UNIQUE(object_name_id, attribute_name, path)
);

CREATE TABLE jmx_attribute_value (
	value_id		IDENTITY,
	attribute_id	INTEGER,
	attribute_value VARCHAR(100),
	poll_timestamp	TIMESTAMP,
	FOREIGN KEY(attribute_id) REFERENCES jmx_attribute(attribute_id)
);

CREATE TABLE jmx_attribute_property (
	property_id		IDENTITY,
	attribute_id	INTEGER,
	property_name	VARCHAR(100),
	property_value	VARCHAR(255),
	FOREIGN KEY(attribute_id) REFERENCES jmx_attribute(attribute_id)
);

CREATE TABLE jmx_graph (
	graph_id		IDENTITY,
	graph_name		VARCHAR(200),
	graph_type		VARCHAR(20),
	multiplier		INTEGER DEFAULT 1,
	integer_value	INTEGER DEFAULT 0,
	UNIQUE(graph_name)
);

CREATE TABLE jmx_graph_attribute (
	graph_id		INTEGER,
	attribute_id	INTEGER,
	PRIMARY KEY(graph_id, attribute_id)
);