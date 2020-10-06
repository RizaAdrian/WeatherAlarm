package com.android.weatheralarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import Utils.Utils;
import data.CityPreference;
import data.JsonWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {
    private TextView cityName;
    private TextView temp;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private ImageView iconView;
    TextToSpeech t1;
    String ed1;
    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityText);
        iconView = findViewById(R.id.thumbnailIcon);
        temp = findViewById(R.id.tempText);
        description = findViewById(R.id.cloudText);
        humidity = findViewById(R.id.humidText);
        pressure = findViewById(R.id.pressureText);
        wind = findViewById(R.id.windText);
        sunrise = findViewById(R.id.riseText);
        sunset = findViewById(R.id.setText);
        updated = findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        renderWeatherData(cityPreference.getCity());
    }

    public void renderWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            DateFormat dateFormat = DateFormat.getTimeInstance();
            String sunriseDate = dateFormat.format(new Date(weather.place.getSunrise()));
            String sunsetDate = dateFormat.format(new Date(weather.place.getSunset()));
            String lastUpdateDate = dateFormat.format(new Date(weather.place.getLastupdate()));



            String tempFormate = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + ", " + weather.place.getCountry());
            temp.setText("" + tempFormate + "°C");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa");
            wind.setText("Wind: " + weather.wind.getSpeed() + " kps");
            sunrise.setText("Sunrise: " + sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last Updated: " + lastUpdateDate);
            description.setText(" Sky: " + weather.currentCondition.getCondition() + " (" +
                    weather.currentCondition.getDescription() + ")");

                    ed1 = decimalFormat.format(weather.currentCondition.getTemperature())+" degree and the condition " + weather.currentCondition.getCondition() + " and " + weather.currentCondition.getDescription()  + " the humidity is " + weather.currentCondition.getHumidity()+ "%";
                    t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.UK);
                                t1.speak(ed1, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    });

            }



        @Override
        protected Weather doInBackground(String... strings) {
            String data = ((new WeatherHttpClient()).getWeatherData(strings[0]));
            weather.iconData = weather.currentCondition.getIcon();
            weather = JsonWeatherParser.getWeather(data);
            Log.v("Data ", weather.place.getCity());

            //new DownloadImageAsyncTask().execute(weather.iconData);

            return weather;
        }
    }

    private void triggerAlarm() {
        /*Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 30); //Seconds
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();*/

        ed1 = decimalFormat.format(weather.currentCondition.getTemperature())+"degres and the condition" + weather.currentCondition.getCondition() + weather.currentCondition.getCondition() + weather.currentCondition.getDescription()  + " the humidity is " + weather.currentCondition.getHumidity()+ "%";
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(ed1, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



    }

    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");
        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("London,uk");
        builder.setView(editText);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(editText.getText().toString());

                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.change_city) {
            showInputDialog();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImage(strings[0]);
        }

        private Bitmap downloadImage(String code) {
            final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");

            try {
                HttpResponse httpResponse = defaultHttpClient.execute(getRequest);
                final int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.e("Download Image", "Error" + statusCode);
                    return null;
                }
                final HttpEntity entity = httpResponse.getEntity();

                if (entity != null) {
                    InputStream inputStream = entity.getContent();

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context k1, Intent k2) {

        }
    }
}
