#pragma once

#include <string>
#include <queue>
#include <thread>
#include <iostream>
#include <sstream>
#include <utility>
#include "boost/atomic.hpp"
#include "boost/chrono.hpp"

enum class Status : int {
	succesed = 1,
	failed_connect = 2,
	failed_send = 3,
	too_long_message = 4,
};

class ZabbixTrapper
{
public:
	ZabbixTrapper(std::string zabbix_host, int zabbix_port) :
            _zabbix_host(std::move(zabbix_host)), _zabbix_port(zabbix_port) {}

	int sendData(const std::string& client_host,
	             const std::string& client_key,
	             const std::string& raw_data);

private:
    int _zabbix_port;
    std::string _zabbix_host;
    std::string _client_host;
    std::string _client_key;
	std::mutex _host_key_mutex;

    Status sendPacket(const std::string& value);
	std::vector<char> createZabbixPacket(const std::string& value);
};
