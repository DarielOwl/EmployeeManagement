package org.plusuan.config.database;

import lombok.Data;

@Data
public class DBSecret { //Objeto para mapear las credenciales del secreto
    private String username;
    private String password;
    private String host;
    private int port;
    private String database;
}
