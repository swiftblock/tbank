<!DOCTYPE html>
<html>
<head>
    <#import "/spring.ftl" as spring />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="chrome=1,IE=edge"/>
    <title>Help-Info</title>
    <link rel="stylesheet" href="/css/main.css" type="text/css"/>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.21/dist/vue.js"></script>
</head>
<body class="mainbody">

<div id="app">
    <div class="helpHeadPhoto">
        <span class="returnPrePage" @click="back"><@spring.message "common.back" /></span>
        <div class="titleHelp"><@spring.message "help.title" /></div>
    </div>

    <div class="helpBodyDiv">
        <div class="helpContent">
            <span class="helpContentText">  <@spring.message "help.content.1" /></span>
            <span class="helpContentText">  <@spring.message "help.content.2" /></span>
            <span class="helpContentText">  <@spring.message "help.content.3" /></span>
            <span class="helpContentText">  <@spring.message "help.content.4" /></span>
        </div>
    </div>
</div>

</body>
</html>

<script type="text/javascript">
    var vm = new Vue({
        el: '#app',
        methods: {
            back: function() {
                history.back();
            }
        }
    });
</script>