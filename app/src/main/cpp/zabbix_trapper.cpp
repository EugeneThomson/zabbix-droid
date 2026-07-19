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
	const unsigned int kFullMessageSize = 65535;
}

Status ZabbixTrapper::sendPacket(const std::string& value) {
	asio::io_service io_service;
	asio::ip::tcp::socket socket(io_service);

	try {
		socket.connect(asio::ip::tcp::endpoint(asio::ip::address::from_string(_zabbix_host), _zabbix_port));
	} catch (std::exception & e) {
		return Status::failed_connect;
	}

	unsigned int serviceSize = _client_host.size() + _client_key.size() +
	                           sizeof(kJsonPrefix) +
	                           sizeof(kJsonKey) +
	                           sizeof(kJsonValue) +
	                           sizeof(kJsonSuffix);

	if ((value.length() + serviceSize) > kFullMessageSize) {
		return Status::too_long_message;
	}

	boost::system::error_code error;
	auto zabbixPacket = createZabbixPacket(value);
    const auto bytes_written = asio::write(socket, asio::buffer(zabbixPacket, zabbixPacket.size()), error);

	if (error || bytes_written != zabbixPacket.size()) {
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
						  + value
						  + std::string(kJsonSuffix);
	std::uint64_t payload_size = payload.size();
    std::vector<char> msg(payload_size + kHeaderSize);

	memcpy(msg.data(), kProtocolHeader.data(), kProtocolHeader.size());
	msg[kVersionOffset] = 0x01; // version
	memcpy(&msg[kLengthOffset], &payload_size, sizeof(payload_size));
	memcpy(&msg[kHeaderSize], payload.c_str(), payload_size);
	return msg;
}

int ZabbixTrapper::sendData(const std::string& client_host,
							const std::string& client_key,
							const std::string& raw_data) {
	std::lock_guard<std::mutex> lock(_host_key_mutex);
	_client_host = client_host;
	_client_key = client_key;
    return static_cast<int>(sendPacket(raw_data));
}