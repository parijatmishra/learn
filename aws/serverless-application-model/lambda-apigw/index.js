'use strict';
// Expects a POST request from an Api Gateway API
// event: contents of POST request body -- JSON object with key "name", string value
// response:
//  - 200 OK, with body containing a string message
//  - 400 Bad Request, if the POST request body did not contain JSON with key "name"
exports.handler = (event, context, callback) => {
    var name = "World";

    console.log("Received " + JSON.stringify(event));
    
    if (typeof(event.queryStringParameters) != 'undefined'
        && event.queryStringParameters != null
        && "name" in event.queryStringParameters)
    {
        name = event.queryStringParameters.name;
    }
    callback(null, {
        "statusCode": 200,
        "body": "Hello, " + name
    })
}
