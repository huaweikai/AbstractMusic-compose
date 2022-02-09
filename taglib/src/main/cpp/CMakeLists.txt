
#cmake版本
cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

#项目名
project("taglib")

# 设置taglib本地路径变量
set(taglib_location "${CMAKE_CURRENT_SOURCE_DIR}/libs")

# 将tag加入so库
add_library(
        tag
        STATIC
        IMPORTED)

# 制定tag库的so文件
set_target_properties(
        tag
        PROPERTIES
        IMPORTED_LOCATION
        ${taglib_location}/${ANDROID_ABI}/libtag.so)

#头文件
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include/taglib)

#把自己写的c文件，加载到cmake编译
add_library( # Sets the name of the library.
             taglib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             taglib.cpp )



#内置库
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


#把上面的操作链接到项目中
target_link_libraries(
                       taglib
                        PRIVATE android
                        PRIVATE tag
                       ${log-lib}
        )