cmake_minimum_required(VERSION 3.22.1)
project("zabbixtrapperndk")

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS ON)

add_library(${CMAKE_PROJECT_NAME} SHARED
        jni_controller.cpp)

#static link boost
add_library( boost_chrono STATIC IMPORTED)
set_target_properties(boost_chrono PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/static/libboost_chrono.a)
add_library( boost_atomic STATIC IMPORTED)
set_target_properties(boost_atomic PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/static/libboost_atomic.a)

#link zabbixtrapper
add_library(zabbixtrapper STATIC
        zabbix_trapper.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/../include/zabbix_trapper.h
)
target_include_directories(zabbixtrapper PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/../include)
target_link_libraries(zabbixtrapper boost_atomic boost_chrono)

#include and final build
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/../include)

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        android
        log
        zabbixtrapper
)
