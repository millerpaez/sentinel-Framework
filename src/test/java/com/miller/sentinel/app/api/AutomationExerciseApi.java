package com.miller.sentinel.app.api;

import com.miller.sentinel.app.data.UserData;
import com.miller.sentinel.core.api.ApiResult;
import com.miller.sentinel.core.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Client for https://automationexercise.com/api. Steps and hooks use this class, never REST Assured directly.
 */
public class AutomationExerciseApi {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(Config.get("api.base.url"))
            .setContentType(ContentType.URLENC)
            .build();

    public ApiResult getProductsList() {
        return ApiResult.from(given(spec).get("/productsList"));
    }

    /**
     * A blank value is omitted from the request instead of being sent empty: the API answers 400
     * ("parameter is missing") only when the parameter is absent; an empty email returns 404.
     */
    public ApiResult verifyLogin(String email, String password) {
        Map<String, String> params = new LinkedHashMap<>();
        putIfNotBlank(params, "email", email);
        putIfNotBlank(params, "password", password);
        return ApiResult.from(given(spec).formParams(params).post("/verifyLogin"));
    }

    public ApiResult createAccount(UserData user) {
        return ApiResult.from(given(spec).formParams(user.toFormParams()).post("/createAccount"));
    }

    public ApiResult deleteAccount(String email, String password) {
        return ApiResult.from(given(spec)
                .formParam("email", email)
                .formParam("password", password)
                .delete("/deleteAccount"));
    }

    private static void putIfNotBlank(Map<String, String> params, String name, String value) {
        if (value != null && !value.isBlank()) {
            params.put(name, value);
        }
    }
}
