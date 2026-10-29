cmake_minimum_required(VERSION 3.22.1)

project("nback_cimpl")

# Creates shared library containing JNI bridge and nback C implementation
add_library(JniBridge SHARED JniBridge.c nback.c)

find_library(log-lib log)

target_link_libraries(JniBridge PRIVATE ${log-lib})
