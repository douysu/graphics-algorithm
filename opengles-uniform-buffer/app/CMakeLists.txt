# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.
include_directories(src/main/cpp/main)
include_directories(src/main/cpp/util)
cmake_minimum_required(VERSION 3.4.1)
add_library( # Sets the name of the library.
             native-lib
             SHARED
             src/main/cpp/main/native-lib.cpp
             src/main/cpp/main/MyGLThread
             src/main/cpp/util/Triangle.cpp
             src/main/cpp/util/FileUtil.cpp
             src/main/cpp/util/MatrixState.cpp
             src/main/cpp/util/ShaderUtil.cpp
             src/main/cpp/util/Matrix.h

                           )
find_library( # Sets the name of the path variable.
              log-lib
              log
             )
target_link_libraries( # Specifies the target library.
                       native-lib
                       android
                       EGL GLESv3
                       ${log-lib} )
