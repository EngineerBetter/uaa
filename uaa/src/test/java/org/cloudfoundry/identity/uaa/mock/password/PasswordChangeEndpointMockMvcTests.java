
/*******************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2015] Pivotal Software, Inc. All Rights Reserved.
 * <p/>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p/>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/

package org.cloudfoundry.identity.uaa.mock.password;

import org.cloudfoundry.identity.uaa.message.PasswordChangeRequest;
import org.cloudfoundry.identity.uaa.mock.InjectedMockContextTest;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.cloudfoundry.identity.uaa.test.TestClient;
import org.cloudfoundry.identity.uaa.util.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import static org.cloudfoundry.identity.uaa.mock.util.MockMvcUtils.utils;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PasswordChangeEndpointMockMvcTests extends InjectedMockContextTest {
    private RandomValueStringGenerator generator = new RandomValueStringGenerator();
    private String passwordWriteToken;

    @Before
    public void setUp() throws Exception {
        TestClient testClient = new TestClient(getMockMvc());
        String adminToken = testClient.getClientCredentialsOAuthAccessToken("admin", "adminsecret",
                "clients.read clients.write clients.secret scim.write");
        String clientId = generator.generate().toLowerCase();
        String clientSecret = generator.generate().toLowerCase();

        BaseClientDetails clientDetails = new BaseClientDetails(clientId, null, null, "client_credentials", "password.write");
        clientDetails.setClientSecret(clientSecret);

        utils().createClient(getMockMvc(), adminToken, clientDetails);

        passwordWriteToken = testClient.getClientCredentialsOAuthAccessToken(clientId, clientSecret,"password.write");
        ScimUser user = new ScimUser("user-id", "user@example.com", "name", "familyname");
        user.addEmail("user@example.com");
        user.setPassword("secr3T");
        utils().createUser(getMockMvc(), adminToken, user);
    }

    @Test
    public void changePassword_withInvalidPassword_returnsErrorJson() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setOldPassword("secr3T");
        request.setPassword("newsecret");
        getMockMvc().perform(put("/Users/user-id/password").header("Authorization", "Bearer " + passwordWriteToken)
                .contentType(APPLICATION_JSON)
                .content(JsonUtils.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("invalid_password"))
                .andExpect(jsonPath("$.message").value("Password must contain at least one upper case character." +
                        ",Password must contain at least one digit."));
    }
}