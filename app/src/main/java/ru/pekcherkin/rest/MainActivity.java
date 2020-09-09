package ru.pekcherkin.rest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText editTextId, editTextName, editTextPhone;
    private TextView textViewId, textViewName, textViewPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextId = (EditText)findViewById(R.id.editText_ID);
        editTextName = (EditText)findViewById(R.id.editText_Name);
        editTextPhone = (EditText)findViewById(R.id.editText_Phone);
        textViewId = (TextView)findViewById(R.id.id_value);
        textViewName = (TextView)findViewById(R.id.name_value);
        textViewPhone = (TextView)findViewById(R.id.phone_value);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        myAdapter.setItems(Arrays.asList(new User("Вова",3),new User("Саша",45),new User("Серега",445),
                new User("Вова",3),new User("Саша",45),new User("Серега",445),
                new User("Вова",3),new User("Саша",45),new User("Серега",445)));
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onclickGet(View view) {
        new RestRequestGetUserById().execute();
    }

    public void onclickPost(View view) {
        new HttpRequestPost().execute();
    }

    public void onclickPut(View view) {
        new HttpRequestPut().execute();
    }

    public void onclickDelete(View view) {
        new HttpRequestDelete().execute();
    }


    private class RestRequestGetUserById extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {

            Integer userId = Integer.parseInt(editTextId.getText().toString());
            System.out.println("nbnnvnbnvbnnn"+userId);

            try {
                String url = "http://192.168.0.12:8080/user/" + userId;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                User user = restTemplate.getForObject(url, User.class);
                return user;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0,"не найден", 0);
        }

        @Override
        protected void onPostExecute(User user) {
            textViewId.setText(user.getId().toString());
            textViewName.setText(user.getName());
            textViewPhone.setText(user.getPhone().toString());
        }

    }

    private class HttpRequestPost extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            try {
                final String url = "http://192.168.0.12:8080/users";

                String name = editTextName.getText().toString();
                Integer phone = Integer.parseInt(editTextPhone.getText().toString());

                User user = new User(name,phone);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                User response = restTemplate.postForObject(url, user, User.class);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return new User(0, "не добавилось", 0);
        }

        @Override
        protected void onPostExecute(User user) {
            textViewId.setText(user.getId().toString());
            textViewName.setText(user.getName());
            textViewPhone.setText(user.getPhone().toString());
        }
    }

    private class HttpRequestPut extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            Integer userId = Integer.parseInt(editTextId.getText().toString());
            String name = editTextName.getText().toString();
            Integer phone = Integer.parseInt(editTextPhone.getText().toString());
            try {
                String url = "http://192.168.0.12:8080/usersput/" + userId;
                User user = new User(userId,name,phone);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.put(url, user);
                User response = restTemplate.getForObject("http://192.168.0.12:8080/user/" + userId, User.class);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0,"ошибка", 0);
        }

        @Override
        protected void onPostExecute(User user) {
            textViewId.setText("");
            textViewName.setText(user.getName());
            textViewPhone.setText("");
        }
    }
    private class HttpRequestDelete extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            Integer userId = Integer.parseInt(editTextId.getText().toString());

            try {
                String url = "http://192.168.0.12:8080/users/" + userId;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.delete(url);
                User response = new User(0,"удален", 0);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0,"не удалил", 0);
        }
        @Override
        protected void onPostExecute(User user) {
            textViewId.setText("");
            textViewName.setText(user.getName());
            textViewPhone.setText("");
        }
    }

}
