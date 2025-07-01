package com.apuntesdejava.jakartacoffeebuilder.config;

public interface DataSourceConfigProvider {
    String getDatasourceName();

//    String getClassName(); // Este campo no está en AddPersistenceMojo, lo añadiremos

    String getServerName();

    Integer getPortNumber();

    String getUrl();

    String getUser();

    String getPassword();

    String getProperties();
}
