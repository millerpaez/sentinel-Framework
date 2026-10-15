package com.miller.sentinel.app.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable account data with every field required by POST /api/createAccount.
 */
public record UserData(
        String name,
        String email,
        String password,
        String title,
        String birthDate,
        String birthMonth,
        String birthYear,
        String firstname,
        String lastname,
        String company,
        String address1,
        String address2,
        String country,
        String zipcode,
        String state,
        String city,
        String mobileNumber) {

    /** Field names exactly as the API expects them. */
    public Map<String, String> toFormParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("title", title);
        params.put("birth_date", birthDate);
        params.put("birth_month", birthMonth);
        params.put("birth_year", birthYear);
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("company", company);
        params.put("address1", address1);
        params.put("address2", address2);
        params.put("country", country);
        params.put("zipcode", zipcode);
        params.put("state", state);
        params.put("city", city);
        params.put("mobile_number", mobileNumber);
        return params;
    }
}
