
function l1(){ trap -p return; echo l1; return 0; }
function l2(){ trap "echo RET" return; l1; echo l2; return 0; }
function l3(){ trap -p return; l2; echo l3; return 0; }

@defer(){
    local latest_code=$(trap -p return)
    # Smart as I
    local code="eval \"trap \\\"$1; ${latest_code:-trap "" return}\\\" return\""
    # echo "$final_code"
    trap "$code" return
}

hello(){
    @trap "echo trap hi"
    return
}

function l1x(){ echo l1x; return 0; }
function l2x(){ @defer "echo WHAT"; l1x; echo l2x; return 0; }
function l3x(){ l2x; echo l3x; return 0; }

@finally(){
    local latest_code=$(trap -p return)
    # Smart as I, using eval to avoid the real return statement being invoked when this function ends.
    local code="eval \"trap \\\"$1 
    ${latest_code:-trap return}
    \\\" return\"
    "
    echo "$code"
    trap "$code" return
}

testfin(){
    @finally '

    echo before final
    echo final
    echo after final
'
}
