#pragma once

#include <string>
#include <queue>
#include <thread>
#include <iostream>
#include <sstream>
#include "boost/atomic.hpp"
#include "boost/chrono.hpp"

enum class Error : int {
	succesed = 1,
	failed_connect = 2,
	failed_send = 3,
	too_long_message = 4,
	not_enough_time = 5, // not enoug time for sync, async require additional checking
	type_error = 6,
	message_does_not_exist = 7
};

class ZabbixTrapper
{
public:
	ZabbixTrapper(const std::string zabbixHost, int zabbixPort, bool ready = false, int ttl = 100) :
			_zabbix_host(zabbixHost), _zabbix_port(zabbixPort), ready(ready), _ttl(ttl) {	}
			// адрес хоста, его порт, нужен ли async (0 - нет, 1 - да) и time_to_life для сообщения

	void start() {
		_sender_thread = std::thread(&ZabbixTrapper::queueControl, this);
	}

	virtual ~ZabbixTrapper() {
		stop();
	}

	void stop() {
		ready = false;
		if (_sender_thread.joinable()) {
			_sender_thread.join();
		}
	}

    virtual void hostKeySet(const std::string client_host, const std::string client_key);

    template<typename Type> int sendData(Type raw_data, bool sync) {
		std::string data;
		if ((std::is_same<Type, char>::value) || (std::is_same<Type, std::string>::value)
			|| (std::is_same<Type, unsigned int>::value) || (std::is_same<Type, float>::value)) {
			std::ostringstream temp;
			temp << raw_data;
			data = temp.str();
		} else {
			return static_cast<int>(Error::type_error);
		}
		if (sync) {
			Error check_sending = Error::not_enough_time;
			std::thread sender(&ZabbixTrapper::sending, this, data, &check_sending);
			sender.detach();
			std::this_thread::sleep_for(std::chrono::milliseconds(_ttl));
			return static_cast<int>(check_sending);
		} 
		else {
			queue.push(data);
			return asyncCheck.size() + 1; //возвращает количество сообщений после PUSH
									//по этому числу можно будет проверить статус отправки
		}
	};

protected:
	int _ttl;
    int _zabbix_port;
    std::string _zabbix_host;
    std::string _client_host;
    std::string _client_key;
	std::uint64_t _payload_size;
    std::queue<std::string> queue;
	std::thread _sender_thread;
	std::atomic<bool> ready;
	mutable std::mutex _host_key_mutex;

    virtual Error sending(std::string value, Error *checkSending);
	std::vector<char> createZabbixPacket(const std::string& value);
    virtual void queueControl();
	virtual Error asyncCheckControl(unsigned int number_message);
	std::vector<Error> asyncCheck;
};
