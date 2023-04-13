# jsonrpcswagger

Custom Swagger-like api for Spring boot applications with json-rpc.

# Instructions

1. Add dependency
2. Send request to {{api_address}}/api/v1/swagger.json
```
{
   "jsonrpc": "2.0",
   "id": "1",
   "method": "getMethodsDescription",
   "params":{}
}
```
3. Example of response:
```
{
    "jsonrpc": "2.0",
    "id": "1",
    "result": {
        "methodsDescription": {
            "/api/v1/urn.json": [
                {
                    "methodName": "firstMethod",
                    "params": {
                        "request": {
                            "stringField": "String",
                            "booleanField": "Boolean",
                            "listField": "List<SortInfo>",
                            "mapField": "Map<String, List<EnumObject>>"
                        }
                    }
                },
                {
                    "methodName": "secondMethod",
                    "params": {
                        "stringField": "String",
                        "booleanField": "Boolean",
                        "listField": "List<CustomObject>",
                        "mapField": "Map<String, List<CustomObject>>"
                    }
                }
            ],
            "models": {
                "EnumObject": [
                    "FIRST",
                    "SECOND"
                ],
                "CustomObject": {
                    "stringField": "String",
                    "integerField": "Integer"
                }
            }
        }
    }
}
```

# Maven Dependency

```
<dependency>
    <groupId>io.github.iamzexy</groupId>
    <artifactId>jsonrpcswagger</artifactId>
    <version>1.0.0</version>
</dependency>

```