package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;

/**
 * Klasa implementująca adapter JsonB ukrywająca treść transportowaną w stronę widoku
 */
public class HidingContentJsonbAdapter implements JsonbAdapter<String, JsonValue> {

    @Override
    public JsonValue adaptToJson(String s) throws Exception {
        return Json.createValue("*HIDDEN*");
    }

    @Override
    public String adaptFromJson(JsonValue jsonValue) {
        return jsonValue.toString().replaceAll("\"", "");
    }
}
