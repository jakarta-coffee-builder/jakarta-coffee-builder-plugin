# Jakarta Coffee Builder Plugin

Maven plugin for adding and modifying Jakarta EE functionality to a project.

# Goals

## Jakarta Faces

### Add Jakarta Faces Configuration

Add Jakarta Faces Servlet configuration in `web.xml` file

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:add-faces 
```

**Parameters**

| Parameter      | Definition                                                                                                                | Default value |
|----------------|---------------------------------------------------------------------------------------------------------------------------|---------------|
| `url-pattern`  | This parameter defines the URL pattern for all Faces pages. This value will be included in the servlet configuration.     | `*.faces`     |
| `welcome-file` | This parameter indicates which page is displayed at startup by default. It must be related to the `url-pattern` parameter | `index.faces` |

**Result**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="6.0" xmlns="https://jakarta.ee/xml/ns/jakartaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd">
    <servlet>
        <description>Jakarta Faces Servlet Definition</description>
        <servlet-name>JakartaServlet</servlet-name>
        <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>JakartaServlet</servlet-name>
        <url-pattern>*.faces</url-pattern>
    </servlet-mapping>
    <welcome-file-list>
        <welcome-file>index.faces</welcome-file>
    </welcome-file-list>
</web-app>

```

### Add Jakarta Faces Page

Add a Face page, associating it with a Managed Bean. It can also be done by using a specified Facelet template

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:add-face-page
```

**Parameters**

| Parameter      | Definition                                                                                            | Default value |
|----------------|-------------------------------------------------------------------------------------------------------|---------------|
| `name`         | Name of the Face page to create                                                                       |               |
| `managed-bean` | Boolean value indicating whether or not the Managed Bean class associated with the Face is created.   | `true`        |
| `template`     | Path of the Facelet template to be implemented for the Face to be created. This parameter is optional |               |


## Jakarta Persistence

### Add Jakarta Persistence Configuration

Add Jakarta Persistence configuration in `persistence.xml` file

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:add-persistence
```

**Parameters**

| Parameter               | Definition                                                                                                             | Default value |
|-------------------------|------------------------------------------------------------------------------------------------------------------------|---------------|
| `persistence-unit-name` | This parameter defines the name of the persistence unit. This value will be included in the persistence configuration. | `defaultPU`   |

### Add DataSource configuration

Add DataSource configuration 

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:add-datasource
```

**Parameters**

| Parameter          | Definition                                                                                                              | Default value                 |
|--------------------|-------------------------------------------------------------------------------------------------------------------------|-------------------------------|
| `name`             | This parameter defines the JNDI name of the DataSource. This value will be included in the DataSource configuration.    |                               |
| `class-name`       | This parameter defines the driver class of the DataSource. This value will be included in the DataSource configuration. | `org.h2.jdbcx.JdbcDataSource` |
| `url`              | This parameter defines the URL of the DataSource. This value will be included in the DataSource configuration.          |                               |
| `username`         | This parameter defines the username of the DataSource. This value will be included in the DataSource configuration.     |                               |
| `password`         | This parameter defines the password of the DataSource. This value will be included in the DataSource configuration.     |                               |
| `coordinates-jdbc` | This parameter defines the coordinates of the JDBC driver. This value will be included in the DataSource configuration. | `com.h2database:h2:1.4.200`   |
| `declare`          | Indicates how the DataSource is to be declared in the application. Possible values are `web.xml` `class`                | `class`                       |
| `server-name`      | This parameter defines the server name of the DataSource. This value will be included in the DataSource configuration.  |                               |
| `port-number`      | This parameter defines the port number of the DataSource. This value will be included in the DataSource configuration.  |                               |
| `properties`       | This parameter defines the properties of the DataSource. This value will be included in the DataSource configuration.   |                               |
| `persistence-unit` | This parameter defines the name of the persistence unit. This value will be included in the persistence configuration.  |                               | 

