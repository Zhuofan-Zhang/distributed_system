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
                .enable(true)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.group10"))
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

                .enable(true)

                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.group10"))
                .paths(PathSelectors.ant("/admin/**"))
                .build();
    }

    private List<RequestParameter> globalReqeustParameters() {

        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token")
                .description("Login token")
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());

        return parameters;

    }


    /**
     * 生成通用的响应信息
     */

    private List<Response> getGlabalResponseMessage() {

        List<Response> list = new ArrayList<>();
        list.add(new ResponseBuilder()
                .code("4xx")
                .description("Request failed，please reference to code and msg")
                .build());

        return list;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Distributed System Group Project")
                .description("Microservice Documents")
                .contact(new Contact("zzf", "https://zhuofan-zhang.github.io/zhuofan-zhang/", "zzhang4@tcd.ie"))
                .build();
    }
}
