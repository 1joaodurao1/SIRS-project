package com.motorist.businesslogic.restcontroller.data.out;

import java.util.List;

public record APIResponseGetLogs(
    APIResponseGetLogsMetadata metadata,
    APIResponseGetLogsContent content)
{
    public record APIResponseGetLogsMetadata (String digest, String iv, String secretKey) {}

    public record APIResponseGetLogsContent (boolean success, List<String> data) {}
}
