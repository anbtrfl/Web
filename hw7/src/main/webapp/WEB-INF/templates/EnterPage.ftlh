<#import "commons.ftlh" as c>

<@c.page>
    <div class="enter form-box">
        <div class="header">Enter</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="enter"/>
                <div class="field">
                    <div class="name">
                        <label for="login">Login</label>
                    </div>
                    <div class="value">
                        <input id="login" name="login"/>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="password">Password</label>
                    </div>
                    <div class="value">
                        <input id="password" name="password" type="password"/>
                    </div>
                </div>
                <div class="error"></div>
                <div class="button-field">
                    <input type="submit" value="Enter">
                </div>
            </form>
        </div>
    </div>

    <script>
        $(function () {
            $(".enter form").submit(function () { <#-- давайте перехватим у этой формочки событие что она посабмичена-->
                const login = $(this).find("input[name='login']").val(); <#-- в поддереве,соотв. этой форме ищу все инпуты,у которых нейм = логин и беру их значения-->
                const password = $(this).find("input[name='password']").val(); <#-- аналогично-->
                const $error = $(this).find(".error");
                const data = {
                    action: "enter",
                    login,
                    password
                }

                ajax(data, $error)

                return false; <#-- чтобы браузер не использовал стандартное поведение и браузер перестал самовольно чето делать-->
            });
        })
    </script>

</@c.page>
