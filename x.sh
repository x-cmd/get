#! /usr/bin/env bash

# support basic x functions
# x python
# x java
# x node
# x $URL
# Download X-Cmd if
# 1. private url
# 2. lack of running engine
# 3. other functions not support

x(){
   case  ${1:-version}  in
        version)       
        
        ;;
        python)
        ;;         
        node)  
        echo node
        ;;
        java)
        echo java
        ;;
        *)
        echo hi
        # download meta data
        #             
    esac 
}

