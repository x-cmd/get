
# Support ash and dash
[[ "abc" =~ ^ab ]] 2>/dev/null || {
    # boot.debug "[[ does NOT support regex."
    _psuedo_simple_double_square_bracket(){
        echo "Activate" >&2
        local value="${1:?Provide value}"
        local op="${2}"
        local pattern="${3:?Provide pattern}"

        case "$op" in
        ==|=) pattern="${pattern//*/[[:print:]]+}";;
        =~ ) ;;
        *)
            local s="[ " i ss=$(($# - 1));
            for i in $(seq 1 $ss); do
                s="$s \$$i"
            done
            eval "$s ]"
            return
        ;;
        esac

        echo "$value" | awk "{ 
            if (\$0 ~ /$pattern/) { 
                exit 0
            } else { 
                exit 1; 
            } 
        }"
    }

    alias [[=_psuedo_simple_double_square_bracket
}


a=3; [[ $a -gt 2 ]] && echo hi
b=abc; [[ $b =~ [a-z]+ ]] && echo hi

_psuedo_simple_double_square_bracket(){
    echo "Activate" >&2
    local value="${1:?Provide value}"
    local op="${2}"
    local pattern="${3:?Provide pattern}"

    case "$op" in
    ==|=) pattern="${pattern//*/[[:print:]]+}";;
    =~ ) ;;
    esac

    echo "$value" | awk "{ 
    if (\$0 ~ /$pattern/) { 
        exit 0
    } else { 
        exit 1; 
    } 
}"
}

alias [[=_psuedo_simple_double_square_bracket

_psuedo_simple_double_square_bracket ab\? =~ a[0-9A-Za-z]+ && echo match
_psuedo_simple_double_square_bracket abcde == abc* ]] && echo match
