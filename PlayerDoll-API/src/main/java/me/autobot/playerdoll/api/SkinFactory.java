package me.autobot.playerdoll.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class SkinFactory {
    public static UUID getUUIDFromStripedUUID(String stripedUUID) {
        String formattedUUID = stripedUUID.replaceFirst( "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        return UUID.fromString(formattedUUID);
    }

    public static String getStringUUIDFromName(String name) {
        UUIDRespond respond = new Gson().fromJson(urlToJson("https://api.minetools.eu/uuid/", name), UUIDRespond.class);
        if (!respond.status.equalsIgnoreCase("OK")) {
            throw new NoSuchElementException("Cannot Fetch UUID From name: " + name);
        }
        return respond.id;
    }
    public static GameProfile getProfileFromName(String name) {
        String propertyValue;
        String propertySignature;

        String stringUUID = getStringUUIDFromName(name);

        JsonObject json = JsonParser.parseReader(urlToJson("https://api.minetools.eu/profile/", stringUUID)).getAsJsonObject();
        if (!json.has("decoded")) {
            throw new NoSuchElementException("Json does not contain \"decoded\" key");
        }
        JsonObject decodedObject = json.getAsJsonObject("decoded");
        if (decodedObject.isJsonNull()) {
            throw new NoSuchElementException("\"decoded\" key is Empty");
        }
        JsonObject propertyObject = json.getAsJsonObject("raw").getAsJsonArray("properties").get(0).getAsJsonObject();
        propertySignature = propertyObject.getAsJsonPrimitive("signature").getAsString();
        propertyValue = propertyObject.getAsJsonPrimitive("value").getAsString();

        GameProfile profile = new GameProfile(getUUIDFromStripedUUID(stringUUID), name);
        profile.getProperties().put("textures", new Property("textures", propertyValue, propertySignature));

        return profile;
    }

    public static InputStreamReader urlToJson(String website, String addition) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(new URL(website + addition).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return reader;
    }

    private static class UUIDRespond {
        public Map<String, Object> cache;
        public String id;
        public String name;
        public String status;
    }
}
