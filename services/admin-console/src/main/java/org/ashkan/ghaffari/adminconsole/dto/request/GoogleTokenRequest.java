package org.ashkan.ghaffari.adminconsole.dto.request;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record GoogleTokenRequest(
    String code,
    String client_id,
    String client_secret,
    String redirect_uri,
    String grant_type
) {
    public MultiValueMap<String, String> toFormData() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", client_id);
        form.add("client_secret", client_secret);
        form.add("redirect_uri", redirect_uri);
        form.add("grant_type", grant_type);
        return form;
    }
}
