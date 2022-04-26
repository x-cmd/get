#! /usr/bin/env bash

get_git_provider(){
    if [ -z "$GIT_PROVIDER" ]; then
        GIT_PROVIDER=gitee.com
        # if curl --connect-timeout 2 https://google.com 1>/dev/null 2>&1; then
        [ "$(curl --connect-timeout 3 ipinfo.io/country 2>/dev/null)" != CN ] && GIT_PROVIDER=github.com
        export GIT_PROVIDER
    fi
    echo "$GIT_PROVIDER"
}

in_china_net(){
    if [ "$(curl --connect-timeout 3 ipinfo.io/country 2>/dev/null)" != CN ]; then
        return 1
    fi
    return 0
    # curl --connect-timeout "${TEST_TIMEOUT:-2}" https://google.com 1>/dev/null 2>&1;
}


    # Auto detect.
    # If in China. Try sequence is "gitee.com", "github.com"
    # If in Other. Try sequence is "gitee.com", "github.com"

    # if [ ! -f "$X_BASH_SRC_PATH/.X_CMD_OPTIMIZED_IN_CHINA" ]; then
    #     if [ "$(curl --connect-timeout 3 ipinfo.io/country 2>/dev/null)" != CN ]; then
    #         echo y > "$X_BASH_SRC_PATH/.X_CMD_OPTIMIZED_IN_CHINA"
    #     else
    #         echo n > "$X_BASH_SRC_PATH/.X_CMD_OPTIMIZED_IN_CHINA"
    #     fi
    # fi
    # X_CMD_OPTIMIZED_IN_CHINA=$(cat "$X_BASH_SRC_PATH/.X_CMD_OPTIMIZED_IN_CHINA")


