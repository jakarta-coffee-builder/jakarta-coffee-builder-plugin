# Jakarta Coffee Builder Plugin
![Maven Central Version](https://img.shields.io/maven-central/v/com.apuntesdejava/jakarta-coffee-builder-plugin)  ![GitHub last commit](https://img.shields.io/github/last-commit/jakarta-coffee-builder/jakarta-coffee-builder-plugin)  [![GitHub](https://img.shields.io/badge/maven-plugin-darkgreen?logo=github)](https://github.com/jakarta-coffee-builder/jakarta-coffee-builder-plugin)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/jakarta-coffee-builder/jakarta-coffee-builder-plugin/maven-ci-cd.yml)

`develop`: ![GitHub branch check runs](https://img.shields.io/github/check-runs/jakarta-coffee-builder/jakarta-coffee-builder-plugin/develop) ![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/m/jakarta-coffee-builder/jakarta-coffee-builder-plugin/develop)


Maven plugin for adding and modifying Jakarta EE functionality to a project.

# Goals

## Jakarta Faces

### Add Jakarta Faces Configuration

Add Jakarta Faces Servlet configuration in `web.xml` file

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-faces 
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

**Showing**
[![asciicast](https://asciinema.org/a/30Zyd628az3toF03XtZ5eSLRc.svg)](https://asciinema.org/a/30Zyd628az3toF03XtZ5eSLRc)

### Add Jakarta Facelet Page (Template)

Add a Facelet page

```shell
mvn "com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-face-template" 
```

**Parameters**

| Parameter | Definition                     | Example                        |
|-----------|--------------------------------|--------------------------------|
| `name`    | Name of the Template to create | `/WEB-INF/templates/template1` |
| `inserts` | Names of inserts block names   | `section1,section2,section3`   |



**Example**
```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-face-template \
    -Dname=/WEB-INF/template/main.xhtml \
    -Dinserts=header,body,footer
```
**Showing**
[![asciicast](https://asciinema.org/a/tD2VpNkH5EHSQaTYRu9pasArc.svg)](https://asciinema.org/a/tD2VpNkH5EHSQaTYRu9pasArc)

### Add Jakarta Faces Page

Add a Face page, associating it with a Managed Bean. It can also be done by using a specified Facelet template

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-face-page
```

**Parameters**

| Parameter      | Definition                                                                                            | Default value | Example                        |
|----------------|-------------------------------------------------------------------------------------------------------|---------------|--------------------------------|
| `name`         | Name of the Face page to create                                                                       |               |                                |
| `managed-bean` | Boolean value indicating whether or not the Managed Bean class associated with the Face is created.   | `true`        |                                |
| `template`     | Path of the Facelet template to be implemented for the Face to be created. This parameter is optional |               | `/WEB-INF/templates/template1` |



**Example**
```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-face-page \
    -Dname=hello-world \
    -Dmanaged-bean=false

mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-face-page \
    -Dname=persons \
    -Dmanaged-bean=true

```

**Showing**

Creating page with / without Managed Bean:
[![asciicast](https://asciinema.org/a/And0N0LueNSaMCKV9VnyKm1gr.svg)](https://asciinema.org/a/And0N0LueNSaMCKV9VnyKm1gr)


## Jakarta Persistence

### Add Jakarta Persistence Configuration

Add Jakarta Persistence configuration in `persistence.xml` file

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-persistence
```

**Parameters**

| Parameter               | Definition                                                                                                             | Default value       |
|-------------------------|------------------------------------------------------------------------------------------------------------------------|---------------------|
| `datasource-name`       | This parameter defines the JNDI name of the DataSource. This value will be included in the DataSource configuration.   | `defaultDatasource` |
| `url`                   | This parameter defines the URL of the DataSource. This value will be included in the DataSource configuration.         |                     |
| `username`              | This parameter defines the username of the DataSource. This value will be included in the DataSource configuration.    |                     |
| `password`              | This parameter defines the password of the DataSource. This value will be included in the DataSource configuration.    |                     |
| `declare`               | Indicates how the DataSource is to be declared in the application. Possible values are `web.xml` `class`               | `class`             |
| `server-name`           | This parameter defines the server name of the DataSource. This value will be included in the DataSource configuration. |                     |
| `port-number`           | This parameter defines the port number of the DataSource. This value will be included in the DataSource configuration. |                     |
| `properties`            | This parameter defines the properties of the DataSource. This value will be included in the DataSource configuration.  |                     |
| `persistence-unit-name` | This parameter defines the name of the persistence unit. This value will be included in the persistence configuration. |                     | 


### Add DataSource configuration

Add DataSource configuration

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-datasource
```

**Parameters**

| Parameter               | Definition                                                                                                             | Default value       |
|-------------------------|------------------------------------------------------------------------------------------------------------------------|---------------------|
| `datasource-name`       | This parameter defines the JNDI name of the DataSource. This value will be included in the DataSource configuration.   | `defaultDatasource` |
| `url`                   | This parameter defines the URL of the DataSource. This value will be included in the DataSource configuration.         |                     |
| `username`              | This parameter defines the username of the DataSource. This value will be included in the DataSource configuration.    |                     |
| `password`              | This parameter defines the password of the DataSource. This value will be included in the DataSource configuration.    |                     |
| `declare`               | Indicates how the DataSource is to be declared in the application. Possible values are `web.xml` `class`               | `class`             |
| `server-name`           | This parameter defines the server name of the DataSource. This value will be included in the DataSource configuration. |                     |
| `port-number`           | This parameter defines the port number of the DataSource. This value will be included in the DataSource configuration. |                     |
| `properties`            | This parameter defines the properties of the DataSource. This value will be included in the DataSource configuration.  |                     |
| `persistence-unit-name` | This parameter defines the name of the persistence unit. This value will be included in the persistence configuration. |                     | 


### Add Jakarta Persistence Entity
```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-entities

```

**Parameters**

| Parameter       | Definition                                                                                        | Example                                 |
|-----------------|---------------------------------------------------------------------------------------------------|-----------------------------------------|
| `entities-file` | The name of the json file that contains the list of entities and their definitions to be created. | [entities.json](examples/entities.json) |

**Example JSON File**
```json
{
  "Category": {
    "fields": {
      "id": {
        "type": "UUID",
        "isId": true,
        "generatedValue": "UUID"
      },
      "name": {
        "type": "String",
        "column": {
          "length": 100
        }
      }
    }
  },
  "Pet": {
    "fields": {
      "id": {
        "type": "Long",
        "isId": true,
        "generatedValue": "Identity"
      },
      "category": {
        "type": "Category",
        "manyToOne": true,
        "joinColumn": {
          "name": "category_id",
          "nullable": false
        }
      },
      "name": {
        "type": "String",
        "column": {
          "length": 100
        }
      },
      "status": {
        "type": "String"
      }
    }
  } 
}
```


## Jakarta RESTful Web Services

### Create REST services with OpenAPI specifications

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:create-openapi
```

**Parameters**

| Parameter        | Definition                                                                        | Example                               |
|------------------|-----------------------------------------------------------------------------------|---------------------------------------|
| `openapi-server` | The name of the yml file is specified with the server-side OpenAPI specification. | [openapi.yaml](examples/openapi.yaml) |

**Note**
- Each endpoint must be identified by a label

### Add Glassfish Embedded Plugin

Add Glassfish Embedded Plugin

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-glassfish-embedded
```


**Parameters**

| Parameter     | Definition                                                      | Default value                |
|---------------|-----------------------------------------------------------------|------------------------------|
| `profile`     | This parameter defines the ID of the Maven profile to be added. | `glassfish`                  |
| `port`        | This parameter defines the GlassFish port                       | `8080`                       |
| `contextRoot` | Application Web Context Root                                    | `${project.build.finalName}` |

### Add PayaraMicro Plugin

Add PayaraMicro Plugin

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-payaramicro
```

### Add Domain Model

The domain model is based using the definition of entities

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-domain-models
```

**Parameters**

| Parameter       | Definition                                                                                        | Example                                 |
|-----------------|---------------------------------------------------------------------------------------------------|-----------------------------------------|
| `entities-file` | The name of the json file that contains the list of entities and their definitions to be created. | [entities.json](examples/entities.json) |

**Example**

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-domain-models \
    -Dentities-file=entities.json
```

### Add Forms (Primefaces) from Entities

```shell
mvn com.apuntesdejava:jakarta-coffee-builder-plugin:0.0.3-SNAPSHOT:add-forms-from-entities
```

**Parameters**

| Parameter       | Definition                                                                                         | Example                                 |
|-----------------|----------------------------------------------------------------------------------------------------|-----------------------------------------|
| `entities-file` | The name  of the json file that contains the list of entities and their definitions to be created. | [entities.json](examples/entities.json) |
| `forms-file`    | The name of the json file that contains the list of forms and columns to Faces Pages               | [forms.json](examples/forms.json)       |


**Example JSON File**
```json
{
  "CategoryList": {
    "entity": "Category",
    "title": "Categories List",
    "base": "/",
    "template": {
      "facelet": "/WEB-INF/templates/template1.xhtml",
      "define": "body"
    },
    "fields": {
      "id": {
        "label": "Category Id"
      },
      "name": {
        "label": "Category Name"
      }
    }
  }
}
```
 