subfolder=(
    std net cmd cloud other
)

for i in "${subfolder[@]}"; do
    echo "$(ls $i/* 2>/dev/null)" >>index
done

