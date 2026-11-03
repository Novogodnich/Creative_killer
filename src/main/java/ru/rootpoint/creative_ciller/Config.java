package ru.rootpoint.creative_ciller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Config(boolean activeAllMode, boolean permissionsForOpsNeeded) {
}