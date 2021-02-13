
```bash
# Hardcore problem:
# str_regex "../abc" "^\.\./" && echo hi
# str_regex "/abc" "^/" && echo hi
# str_regex "\"/abc" "^\"/"
```

```bash
str_regex(){
    local value="${1}"
    local pattern="${2:?str_regex(): Provide pattern}"

    # Only dash does not support pattern="${pattern//\\/\\\\}"

    value=$(echo "$value" | tr '"\\' "\001\002")
    pattern=$(echo "$pattern" | tr '"\\' "\001\002")

    echo "" | awk -v value="$value" -v pattern="$pattern" 'END {
        
        gsub("\001", "\"", value)
        gsub("\002", "\\", value)
        gsub("\001", "\"", pattern)
        gsub("\002", "\\", pattern)

        if (match(value, pattern)) {
            exit 0
        } else {
            exit 1;
        }
    }'
}
```

```bash
STR_REGEX_SEP="$(printf "\001")"
str_regex(){
    # Only dash does not support pattern="${pattern//\\/\\\\}"
    awk -v FS="${STR_REGEX_SEP}" '{
        if (match($1, $2))  exit 0
        else                exit 1
    }' <<A
${1}${STR_REGEX_SEP}${2:?str_regex(): Provide pattern}
A
}
```


