package com.motorist.businesslogic.restcontroller.data.out;

public record APIResponse(
    APIResponseMetadata metadata,
    APIResponseContent content)

{
    public record APIResponseMetadata (String digest, String iv, String secretKey) {}

    public record APIResponseContent (boolean success, String data) {}
}
