INSERT INTO jmx_attribute_path (object_name, attribute, attribute_type, path) 
VALUES ('com.jamfsoftware:Context=/,Type=ThreadPoolMBean,Name=com.jamfsoftware.jss.managementextensions.GeneralThreadPoolMBean', 'ActiveThreadCount', 'int', NULL);
INSERT INTO jmx_attribute_path (object_name, attribute, attribute_type, path) 
VALUES ('com.jamfsoftware:Context=/,Type=ThreadPoolMBean,Name=com.jamfsoftware.jss.managementextensions.GeneralThreadPoolMBean', 'CurrentPoolSize', 'int', NULL);
INSERT INTO jmx_attribute_path (object_name, attribute, attribute_type, path) 
VALUES ('com.jamfsoftware:Context=/,Type=ThreadPoolMBean,Name=com.jamfsoftware.jss.managementextensions.GeneralThreadPoolMBean', 'QueuedTaskCount', 'int', NULL);
INSERT INTO jmx_attribute_path (object_name, attribute, attribute_type, path) 
VALUES ('com.jamfsoftware:Context=/,Type=CounterMBean,Name=com.jamfsoftware.jss.managementextensions.LdapConnectionsInUseMBean', 'Count', 'int', NULL);