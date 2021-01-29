package server;

import java.util.ArrayList;
import java.util.List;

public interface AuthService {
    void start();

    String getNickByLoginPass(String login, String pass);

    void stop();

    ArrayList<String> getLogins();
}

