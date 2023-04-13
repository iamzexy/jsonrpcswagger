package phoenixit.starter.customswagger;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AutoJsonRpcServiceImpl
public class SwaggerRpcImpl implements SwaggerRpc {

    private final Map<String, Object> methodsDescription;

    public SwaggerRpcImpl(@Qualifier("methodsDescription") Map<String, Object> methodsDescription) {
        this.methodsDescription = methodsDescription;
    }

    @Override
    public Map<String, Object> getMethodsDescription() {
        return methodsDescription;
    }
}
