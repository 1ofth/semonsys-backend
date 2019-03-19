<%@page contentType="text/html" pageEncoding="UTF-8"
        language="java" import="java.util.List, java.util.ArrayList"
        session="true"
%>
<%@ page import="java.util.Collections" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
</head>
<body >
<main>
    <header id="header">
        <div class="head">Группа P3212</div>
        <div class="head">Ибраимов Эдем, Морозов Иван</div>
        <div class="head">Вариант 40192</div>
    </header>

    <div class="wrapper">
        <div class="right-column">
            <form id="main_form" method="post" onsubmit="return false;" >

                <input type="text" name="ip" id="ip"   placeholder="ip here" />

                <input class="download" id="btn" onclick="buttonClick()" type="button"  value="Проверить">
            </form>

        </div>
    </div>
</main>

<script type="text/javascript">
    function buttonClick() {

        let data = new URLSearchParams();
        data.append('ip', document.getElementById("ip").value);

        fetch('http://localhost:8080/monitor/top', {
            method: 'POST',
            body: data,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            credentials: 'include'

        }).then((data) => {
            alert(data);
        });
    }
</script>
</body>

</html>