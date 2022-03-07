

cmake_minimum_required(VERSION 3.18.1)

project("blur")
# 设置taglib本地路径变量
set(taglib_location "${CMAKE_CURRENT_SOURCE_DIR}/libs")


# 将tag加入so库
add_library(
        blurc
        STATIC
        IMPORTED)

# 制定tag库的so文件
set_target_properties(
        blurc
        PROPERTIES
        IMPORTED_LOCATION
        ${taglib_location}/${ANDROID_ABI}/libblur.so)


add_library( # Sets the name of the library.
        blur

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        blur.cpp)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        blur

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        jnigraphics
        )
