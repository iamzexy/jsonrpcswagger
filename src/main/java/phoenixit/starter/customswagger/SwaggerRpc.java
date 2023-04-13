package phoenixit.starter.customswagger;

import com.googlecode.jsonrpc4j.JsonRpcService;

import java.util.Map;

@JsonRpcService("/api/v1/swagger.json")
public interface SwaggerRpc {

    Map<String, Object> getMethodsDescription();
}
