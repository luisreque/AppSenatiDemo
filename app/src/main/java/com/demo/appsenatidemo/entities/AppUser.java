package com.demo.appsenatidemo.entities;

/**
 * Created by Equipo on 06/08/2015.
 */
public class AppUser {
    private String Login;
    private String Nombre;
    private String Apellido;
    private int IdSupervisor;

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public int getIdSupervisor() {
        return IdSupervisor;
    }

    public void setIdSupervisor(int idSupervisor) {
        IdSupervisor = idSupervisor;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }
}
