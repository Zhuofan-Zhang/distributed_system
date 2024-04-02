package com.group10.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@EnableOpenApi
public class SwaggerConfiguration {
    @Bean
    public Docket webApiDoc() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("user-interface-doc")
                .pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制，线上关闭
                .enable(true)
                //配置api文档元信息
                .apiInfo(apiInfo())
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.group10"))
                //正则匹配请求路径，并分配至当前分组
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .globalRequestParameters(globalReqeustParameters())
                .globalResponses(HttpMethod.GET, getGlabalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlabalResponseMessage());
    }

    @Bean
    public Docket adminApiDoc() {

        return new Docket(DocumentationType.OAS_30)
                .groupName("admin-interface-doc")
                .pathMapping("/")

                //定义是否开启Swagger，false是关闭，可以通过变量去控制，线上关闭
                .enable(true)

                //配置文档的元信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.group10"))
                //正则匹配请求路径，并分配到当前项目组
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }

    private List<RequestParameter> globalReqeustParameters() {

        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token")
                .description("登录令牌")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());

//        parameters.add(new RequestParameterBuilder()
//                .name("token2")
//                .description("登录令牌")
//                .in(ParameterType.HEADER)
//                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
//                .required(false)
//                .build());

        return parameters;

    }


    /**
     * 生成通用的响应信息
     */

    private List<Response> getGlabalResponseMessage() {

        List<Response> list = new ArrayList<>();
        list.add(new ResponseBuilder()
                .code("4xx")
                .description("请求错误，根据code和msg检查")
                .build());

        return list;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("1024ECommerce")
                .description("microservice-documents")
                .contact(new Contact("zzf", "https://zhuofan-zhang.github.io/zhuofan-zhang/", "zhangzhuofan2019@163.com"))
                .version("12")
                .build();
    }
}
