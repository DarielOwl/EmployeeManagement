package org.plusuan.config.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DBSecret getDBSecret() throws Exception {
        String secretName = "employee-bd";
        Region region = Region.of("sa-east-1");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response;
        try {
            response = client.getSecretValue(request);
        } catch (Exception e) {
            throw e;
        }

        String secretString = response.secretString();

        return objectMapper.readValue(secretString, DBSecret.class);
    }
}