package com.example.smarttasks.repository.services.preferences;

public interface PreferencesServiceInter {

    <T> void put(String key, T value);

    <T> T get(String key, T defaultValue);

}
