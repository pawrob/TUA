package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import pl.lodz.pl.it.ssbd2021.ssbd05.util.SymmetricCrypt;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.naming.NamingException;

public class VersionCryptJsonbAdapter implements JsonbAdapter<Long, JsonValue> {

    static final String INVALID_VERSION_MESSAGE = "Invalid version provided";

    public VersionCryptJsonbAdapter() throws NamingException {
    }

    @Override
    public JsonValue adaptToJson(Long s) throws Exception {
        return Json.createValue(SymmetricCrypt.encrypt((String.valueOf(s))));
    }

    @Override
    public Long adaptFromJson(JsonValue jsonValue) {
        try {
            String decrypted = SymmetricCrypt.decrypt(jsonValue.toString().replaceAll("\"", ""));
            if (null == decrypted || decrypted.isBlank())
                return 0L;
            return Long.valueOf(decrypted);
        } catch (IllegalArgumentException ex) {
            throw new JsonbException(INVALID_VERSION_MESSAGE, ex);
        }
    }
}
