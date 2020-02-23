#!/bin/bash

do_work() {
	# newman run /Users/serenatse/Desktop/cs122b/test/cs122b-tests/hw6/APIGatewayService.postman_collection.json --global-var host=localhost --global-var port=7180
	FILE="test_output_";
	newman run APIGatewayService.postman_collection.json --global-var host=localhost --global-var port=7180 > "$FILE$1";
	# newman run /Users/serenatse/Desktop/cs122b/test/cs122b-tests/hw6/APIGatewayService.postman_collection.json --global-var host=localhost --global-var port=7180 > "$FILE$1";
}

set -m
for i in {1..10}
do
	do_work &
done

while [ 1 ]
do
	fg 2>/dev/null;
	[ $?==1 ] && break;
done

