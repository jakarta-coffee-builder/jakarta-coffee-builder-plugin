<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:f="jakarta.faces.core"
      xmlns:h="jakarta.faces.html">
    <f:view contentType="text/html;charset=UTF-8" encoding="UTF-8">
        <h:head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>
                <ui:insert name="title">title</ui:insert> 
            </title>
        </h:head>

        <h:body>
    <#list contents as content>
            <ui:insert name="${content}">${content}</ui:insert>
    </#list>


        </h:body>
    </f:view>
</html>
