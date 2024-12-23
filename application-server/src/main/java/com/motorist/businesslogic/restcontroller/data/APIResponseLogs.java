package com.motorist.businesslogic.restcontroller.data;

import java.util.List;

public record APIResponseLogs (
    boolean success,
    List<String> content)
{}
