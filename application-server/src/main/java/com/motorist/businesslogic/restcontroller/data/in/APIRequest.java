package com.motorist.businesslogic.restcontroller.data.in;

public record APIRequest (
    APIRequestMetadata metadata,
    APIRequestContent content)
{
    public record APIRequestContent (String role, String command) {}

    public record APIRequestMetadata(String digest, String iv, String secretKey) {}
}
