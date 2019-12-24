subfolder=(
    std net cmd cloud other
)

# std for standard library files
# net for security cmds, like nmap, metasploit
# cloud for provider
# cmd for other command not belong above

# We need to consider using the flat naming scheme

rm index
for i in "${subfolder[@]}"; do
    ls $i/*
done 2>/dev/null >>index

