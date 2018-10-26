CREATE TABLE if not exists  `pv_hour_1` (
    `nas_port_id` varchar(125) NOT NULL,
    `ip_address` varchar(50),
    `page_type` smallint(6) NOT NULL,
    `datetime` varchar(30) DEFAULT NULL,
    `username` varchar(50) DEFAULT NULL,
    `custom_name` varchar(50) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOAD DATA LOCAL INFILE 'D:/BaiduYunDownload/20160319/test/output.txt'
INTO TABLE `pv_hour_1`
FIELDS TERMINATED BY ']  ['
LINES STARTING BY 'NasPortId[' TERMINATED BY ']\n';