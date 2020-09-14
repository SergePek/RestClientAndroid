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
import android.widget.Toast;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextId, editTextName, editTextPhone;
    private TextView textViewId, textViewName, textViewPhone;
    private MyAdapter myAdapter;
    private RecyclerView recyclerView;
    MyAdapter.UserClickListener userClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextId = (EditText) findViewById(R.id.editText_ID);
        editTextName = (EditText) findViewById(R.id.editText_Name);
        editTextPhone = (EditText) findViewById(R.id.editText_Phone);
        textViewId = (TextView) findViewById(R.id.id_value);
        textViewName = (TextView) findViewById(R.id.name_value);
        textViewPhone = (TextView) findViewById(R.id.phone_value);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userClickListener = new MyAdapter.UserClickListener() {
            @Override
            public void onUserClick(User user) {
                editTextId.setText(user.getId().toString());
                editTextName.setText(user.getName());
                editTextPhone.setText(user.getPhone().toString());
            }
        };
        myAdapter = new MyAdapter(userClickListener);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onclickGet(View view) {
        String id = editTextId.getText().toString();
        if (id.equals("")) {
            Toast.makeText(MainActivity.this, "Введите ID", Toast.LENGTH_SHORT).show();
        } else
            new RestRequestGetUserById().execute();
    }

    public void onclickPost(View view) {

        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        if (name.equals("")) {
            Toast.makeText(MainActivity.this, "Введите Имя", Toast.LENGTH_SHORT).show();
        } else if (phone.equals("")) {
            Toast.makeText(MainActivity.this, "Введите телефон", Toast.LENGTH_SHORT).show();
        } else new HttpRequestPost().execute();
    }

    public void onclickPut(View view) {
        String id = editTextId.getText().toString();
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        if (id.equals("")) {
            Toast.makeText(MainActivity.this, "Введите ID", Toast.LENGTH_SHORT).show();
        } else if (name.equals("")) {
            Toast.makeText(MainActivity.this, "Введите Имя", Toast.LENGTH_SHORT).show();
        } else if (phone.equals("")) {
            Toast.makeText(MainActivity.this, "Введите телефон", Toast.LENGTH_SHORT).show();
        } else new HttpRequestPut().execute();
    }

    public void onclickDelete(View view) {
        String id = editTextId.getText().toString();
        if (id.equals("")) {
            Toast.makeText(MainActivity.this, "Введите ID", Toast.LENGTH_SHORT).show();
        } else
            new HttpRequestDelete().execute();
    }

    public void onclickGetAll(View view) {
        new RestRequestGetAllUsers().execute();
    }


    private class RestRequestGetAllUsers extends AsyncTask<Void, Void, List<User>> {
        private ArrayList<User> users;

        @Override
        protected List<User> doInBackground(Void... params) {

            try {
                String url = "http://192.168.0.12:8080/usersAll/";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                User[] users = restTemplate.getForObject(url, User[].class);
                List<User> usersList = Arrays.asList(users);
                return usersList;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return Arrays.asList(new User(0, "ошибка", 0));
        }

        @Override
        protected void onPostExecute(List<User> users) {
            myAdapter.clearItems();
            myAdapter.setItems(users);
        }

    }

    private class RestRequestGetUserById extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {

            String id = editTextId.getText().toString();
            Integer userId = Integer.parseInt(id);

            try {
                String url = "http://192.168.0.12:8080/user/" + userId;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                User user = restTemplate.getForObject(url, User.class);
                return user;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0, "не найден", 0);
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

                User user = new User(name, phone);
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
            new RestRequestGetAllUsers().execute();
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
                User user = new User(userId, name, phone);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.put(url, user);
                User response = restTemplate.getForObject("http://192.168.0.12:8080/user/" + userId, User.class);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0, "ошибка", 0);
        }

        @Override
        protected void onPostExecute(User user) {
            textViewId.setText("");
            textViewName.setText(user.getName());
            textViewPhone.setText("");

            new RestRequestGetAllUsers().execute();
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
                User response = new User(0, "удален", 0);
                return response;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return new User(0, "не удалил", 0);
        }

        @Override
        protected void onPostExecute(User user) {
            textViewId.setText("");
            textViewName.setText(user.getName());
            textViewPhone.setText("");

            new RestRequestGetAllUsers().execute();
        }
    }

}
