package com.miller.sentinel.core.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Normalized API response.
 *
 * <p>Some APIs answer HTTP 200 for every request and put the real status in the body, sometimes with a
 * {@code text/html} Content-Type. The body is parsed from the raw string so the Content-Type header never
 * matters, and callers check {@link #responseCode()} instead of the HTTP status.
 */
public record ApiResult(int httpStatus, int responseCode, String message, JsonPath body) {

    public static ApiResult from(Response response) {
        JsonPath body = JsonPath.from(response.asString());
        return new ApiResult(
                response.statusCode(),
                body.getInt("responseCode"),
                body.getString("message"),
                body);
    }
}
