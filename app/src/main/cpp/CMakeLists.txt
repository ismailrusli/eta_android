cmake_minimum_required(VERSION 3.10.2)

# Set the path to the Oboe library
set(OBOE_DIR ${CMAKE_SOURCE_DIR}/oboe)

# Include the Oboe directory
include_directories(${OBOE_DIR}/include)

# Add the Oboe library
add_subdirectory(${OBOE_DIR} ${CMAKE_BINARY_DIR}/oboe)

# Define your native library
add_library(native-lib SHARED
            MyAudioStream.cpp)

# Link Oboe to your native library
target_link_libraries(native-lib
                      oboe
                      log)