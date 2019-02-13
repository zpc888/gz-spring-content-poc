#!/usr/bin/env bash

CONTENT_ID=$( curl -X POST --header 'Content-Type: application/json' -d '{"name": "spring-content-poc-test", "summary": "test spring content", "contentId": "gz-test-spring-content-008", "contentLength": 8}' 'http://localhost:8080/files' | grep href | tail -1 | sed -e 's/.*\///;s/"//' )
echo "INFO: Document: #${CONTENT_ID} meta data is saved"

> /tmp/t1.txt > /tmp/t2.txt
echo "22334455" > /tmp/t1.txt

curl -X PUT --header 'Content-Type: multipart/form-data' -F "file=@/tmp/t1.txt" "http://localhost:8080/files/${CONTENT_ID}"
echo "INFO: Document: #${CONTENT_ID} content is saved"

curl -X GET "http://localhost:8080/files/${CONTENT_ID}" > /tmp/t2.txt

echo ""
echo ""
echo "TEST RESULT:"
echo "    +=================================+"
RESULT=$(diff /tmp/t1.txt /tmp/t2.txt | wc -l)
if [ $RESULT -eq 0 ]
then 
    echo "    | save, retrive test successfully |"
else 
    echo "    | save, retrive test fails        |"
fi
echo "    +=================================+"
echo ""

