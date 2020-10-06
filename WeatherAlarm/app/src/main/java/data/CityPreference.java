package data;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences sharedPreferences;

    public CityPreference(Activity activity) {
        sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);

    }

    public String getCity() {
        return sharedPreferences.getString("city", "London,uk");
    }

    public void setCity(String city) {
        sharedPreferences.edit().putString("city", city).commit();
    }
}
