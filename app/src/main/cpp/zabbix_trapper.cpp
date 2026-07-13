#include "zabbix_trapper.h"
#include "boost/asio.hpp"

namespace asio = boost::asio;

namespace {
	constexpr char kJsonPrefix[] = R"({"request":"sender data","data":[{"host":")";
	constexpr char kJsonKey[] = R"(","key":")";
	constexpr char kJsonValue[] = R"(","value":")";
	constexpr char kJsonSuffix[] = R"("}]})";
	constexpr std::array<char, 4> kProtocolHeader = {'Z', 'B', 'X', 'D'};
	constexpr std::size_t kVersionOffset = 4;
	constexpr std::size_t kLengthOffset = 5;
	constexpr std::size_t kHeaderSize = 13;
	static const unsigned int kFullMessageSize = 65535;
	static const unsigned int kMinMessageSize = 1023;
//	static const unsigned int kQueueCleanSize = 100;
}

//void ZabbixTrapper::hostKeySet(std::string client_host, std::string client_key) {
//	std::lock_guard<std::mutex> lock(_host_key_mutex);
//	_client_host = client_host;
//	_client_key = client_key;
//}

//void ZabbixTrapper::queueControl() {
//	while (ready) {
//		if (!queue.empty()) {
//			Status checkSending = Status::not_enough_time;
//			asyncCheck.push_back(checkSending);
//			unsigned int asyncCheckSize = asyncCheck.size();
//			std::string data = queue.front();
//			queue.pop();
//			asyncCheck[asyncCheckSize - 1] = sendPacket(data, &checkSending);
//		}
//		if (queue.size() > kQueueCleanSize) {
//			queue.empty();
//			asyncCheck.clear();
//		}
//	}
//}

Status ZabbixTrapper::sendPacket(std::string value) {
	asio::io_service io_service;
	asio::ip::tcp::socket socket(io_service);

	try {
		socket.connect(asio::ip::tcp::endpoint(asio::ip::address::from_string(_zabbix_host), _zabbix_port));
	} catch (std::exception & e) {
		return Status::failed_connect;
	}

	unsigned int serviceSize = std::string(_client_host + _client_key).size() +
	                           sizeof(kJsonPrefix) +
	                           sizeof(kJsonKey) +
	                           sizeof(kJsonValue) +
	                           sizeof(kJsonSuffix);

	if ((value.length() + serviceSize) > kFullMessageSize) {
		return Status::too_long_message;
	}

	boost::system::error_code error;
	auto zabbixPacket = createZabbixPacket(value);
	asio::write(socket, asio::buffer(zabbixPacket, zabbixPacket.size()), error);

	if (error) {
		return Status::failed_send;
	} else {
		return Status::succesed;
	}
}

std::vector<char> ZabbixTrapper::createZabbixPacket(const std::string& value) {
	std::string payload = std::string(kJsonPrefix)
	                      + _client_host
	                      + std::string(kJsonKey)
	                      + _client_key
	                      + std::string(kJsonValue)
						  + value.substr(0, value.length())
						  + std::string(kJsonSuffix);
	std::uint64_t payload_size = payload.size();

//	std::vector<char> msg(kMinMessageSize + kHeaderSize);
//	if (payload_size > kMinMessageSize) {
//		msg.resize(payload_size + kHeaderSize);
//	}
    std::vector<char> msg(payload_size + kHeaderSize);

	memset(msg.data(), 0x00, payload_size + kHeaderSize);

	memcpy(msg.data(), kProtocolHeader.data(), kProtocolHeader.size());
	msg[kVersionOffset] = 0x01; // version
	memcpy(&msg[kLengthOffset], &payload_size, sizeof(payload_size));
	memcpy(&msg[kHeaderSize], payload.c_str(), payload_size);
	return msg;
}

//Status ZabbixTrapper::asyncCheckControl(unsigned int number_message) {
//	if (asyncCheck.size() <= number_message) {
//		return(asyncCheck[number_message - 1]);
//	}
//	return Status::message_does_not_exist;
//}

int ZabbixTrapper::sendData(std::string client_host,
							std::string client_key,
							std::string raw_data) {
	std::lock_guard<std::mutex> lock(_host_key_mutex);
	_client_host = client_host;
	_client_key = client_key;
//	std::string data;
//	if ((std::is_same<Type, char>::value) || (std::is_same<Type, std::string>::value)
//	    || (std::is_same<Type, unsigned int>::value) || (std::is_same<Type, float>::value)) {
//		std::ostringstream temp;
//		temp << raw_data;
//		data = temp.str();
//	} else {
//		return static_cast<int>(Status::type_error);
//	}
	std::ostringstream temp;
	temp << raw_data;
	std::string data = temp.str();

//	if (sync) {
//		Status check_sending = Status::not_enough_time;
//		std::thread sender(&ZabbixTrapper::sendPacket, this, data, &check_sending);
//		sender.detach();
//		std::this_thread::sleep_for(std::chrono::milliseconds(_ttl));
//		return static_cast<int>(check_sending);
//	} else {
//		queue.push(data);
//		return asyncCheck.size() + 1; //возвращает количество сообщений после PUSH
//		//по этому числу можно будет проверить статус отправки
//	}

//	Status check_sending = Status::not_enough_time;
//	Status check_sending = ZabbixTrapper::sendPacket(data, &check_sending);
//	std::thread sender(&ZabbixTrapper::sendPacket, this, data, &check_sending);
//	sender.detach();
//	std::this_thread::sleep_for(std::chrono::milliseconds(_ttl));
//	return static_cast<int>(check_sending);
//	return static_cast<int>(check_sending);
	return static_cast<int>(sendPacket(data));
}