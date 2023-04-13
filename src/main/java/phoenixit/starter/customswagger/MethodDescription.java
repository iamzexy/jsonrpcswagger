package phoenixit.starter.customswagger;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class MethodDescription {

    private String methodName;
    private Map<String, Object> params;
}
