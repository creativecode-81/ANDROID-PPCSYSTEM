package com.francode.college_political_parties.Utils;

public class Apis {
    private static final String BASE_URL = "http://IPv4:8080"; // Cambia esto por tu URL base

    public static TypeDocService getTypeDocService() {
        return Cliente.getClient(BASE_URL).create(TypeDocService.class);
    }
}
