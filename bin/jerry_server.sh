#!/bin/bash
THIS_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pushd "${THIS_DIR}/../"
java -cp "conf/:target/jserver-1.0.jar:target/dependency/*" com.dulvac.jerry.Server $@
popd
